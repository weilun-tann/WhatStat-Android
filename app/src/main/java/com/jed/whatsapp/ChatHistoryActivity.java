package com.jed.whatsapp;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.Nullable;


import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class ChatHistoryActivity extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        // RETRIEVE UPLOADED FILES
        HashMap uploadedFiles = (HashMap<Date, String>)getIntent().getExtras().get("uploadedFiles");

        Toast.makeText(getApplicationContext(), uploadedFiles.toString(), Toast.LENGTH_LONG).show();
    }
}
