package com.example.whatsupp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View ContactsView;
    private RecyclerView myContactList;
    private DatabaseReference Contactsref, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Toolbar mToolbar;
    private Button searchButton;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         ContactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

         searchButton = ContactsView.findViewById(R.id.search_contacts_button);

         myContactList = ContactsView.findViewById(R.id.contacts_list);
         myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

         mAuth = FirebaseAuth.getInstance();
         currentUserID = mAuth.getCurrentUser().getUid();

         Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

         UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

         searchButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 GoToFindAllContacts();
             }
         });
        return ContactsView;
    }







    @Override
    public void onStart() {


        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(Contactsref, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                String userIds = getRef(position).getKey();

                UsersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            if (dataSnapshot.child("userState").hasChild("state")){

                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                if (state.equals("online")){

                                    holder.onlineIcon.setVisibility(View.VISIBLE);

                                }
                                else if (state.equals("offline")){

                                    holder.onlineIcon.setVisibility(View.INVISIBLE);

                                }
                            }
                            else {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);

                            }



                            if (dataSnapshot.hasChild("image")){

                                String userImage = dataSnapshot.child("image").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            }
                            else {

                                String profileStatus = dataSnapshot.child("status").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);


                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {



                    }
                });


            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_diplay_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status_green);
        }
    }

    private void GoToFindAllContacts() {

        Intent goToSearch = new Intent(getActivity(), FindContactsActivity.class);
        startActivity(goToSearch);
    }
}
