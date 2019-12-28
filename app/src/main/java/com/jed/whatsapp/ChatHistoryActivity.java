package com.jed.whatsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ChatHistoryActivity extends Activity {

    // DECLARE ALL ATTRIBUTES
    RecyclerView recyclerView;
    UploadedCloudFileAdapter uploadedCloudFileAdapter;
    ArrayList<UploadedCloudFile> uploadedCloudFiles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        // RETRIEVE UPLOADED FILES
        Intent i = getIntent();
        if (i == null) {
            Toast.makeText(getApplicationContext(), "NULL INTENT", Toast.LENGTH_LONG).show();
            System.out.println("ERROR! NULL INTENT");
        }
        else {
            System.out.println("INTENT IS NORMAL");
        }
        HashMap uploadedFiles = (HashMap<Date, String>) i.getSerializableExtra("uploadedFiles");

        Set<Map.Entry> entries = uploadedFiles.entrySet();
        for (Map.Entry entry : entries) {
            UploadedCloudFile f = new UploadedCloudFile();
            f.setLastModified((Date)entry.getKey());
            f.setFileName((String)entry.getValue());
            uploadedCloudFiles.add(f);
        }

        uploadedCloudFileAdapter = new UploadedCloudFileAdapter(uploadedCloudFiles);

        recyclerView = findViewById(R.id.uploadedFilesRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(uploadedCloudFileAdapter);

        // DEBUG : TO DELETE LATER
        Toast.makeText(getApplicationContext(), uploadedFiles.toString(), Toast.LENGTH_LONG).show();
    }
}
