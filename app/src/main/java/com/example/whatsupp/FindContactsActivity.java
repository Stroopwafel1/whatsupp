package com.example.whatsupp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class FindContactsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_contacts);

        InitializeFields();

        mToolbar = findViewById(R.id.find_contacts_appbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Contacts");


    }



    private void InitializeFields() {

        searchButton = findViewById(R.id.search_contacts_button);
    }



}
