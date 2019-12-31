package com.jed.whatsapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        HashMap uploadedFiles = (HashMap<Date, String>) getIntent().getSerializableExtra("uploadedFiles");
        Set<Map.Entry> entries = uploadedFiles.entrySet();
        for (Map.Entry entry : entries) {
            UploadedCloudFile f = new UploadedCloudFile();
            f.setLastModified((Date) entry.getKey());
            f.setFileName((String) entry.getValue());
            uploadedCloudFiles.add(f);
        }

        uploadedCloudFileAdapter = new UploadedCloudFileAdapter(uploadedCloudFiles, this);

        recyclerView = findViewById(R.id.uploadedFilesRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(uploadedCloudFileAdapter);

        // DEBUG : TO DELETE LATER
        Toast.makeText(this, uploadedFiles.toString(), Toast.LENGTH_LONG).show();
    }
}
