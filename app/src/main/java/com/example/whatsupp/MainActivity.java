package com.example.whatsupp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;


    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Whatsupp");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){

            SendUserToLoginActivity();
        }
        else {

            updateUserStatus("online");
            VerifyUserExistance();
        }
    }

    @Override
    protected void onStop() {


        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

            updateUserStatus("offline");
        }
    }

    private void VerifyUserExistance() {

        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())){

                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                }
                else {

                    SendUserToSettingsActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option){

            updateUserStatus("offline");

            mAuth.signOut();
            SendUserToLoginActivity();

        }

        if (item.getItemId() == R.id.main_settings_option){

            SendUserToSettingsActivity();

        }

        if (item.getItemId() == R.id.main_find_friends_option){

            SendUserToFindFriendsActivity();



        }

        if (item.getItemId() == R.id.main_create_group_option){

            RequestNewGroup();

        }

        return true;


    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name:");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Life of a waffle");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please write title of the group", Toast.LENGTH_SHORT).show();
                }
                else{

                    CreateNewGroup(groupName);

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName) {

        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(MainActivity.this, groupName + " is created!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity() {

        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);

    }

    private void SendUserToFindFriendsActivity() {

        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);

    }

    private void updateUserStatus(String state){

        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);


        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);






    }
}
