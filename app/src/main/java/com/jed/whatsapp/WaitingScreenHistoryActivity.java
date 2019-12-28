package com.jed.whatsapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class WaitingScreenHistoryActivity extends AppCompatActivity {

    // CLASS ATTRIBUTES
    private List<String> promptList = Arrays.asList(
        "Hold on while I pull your uploaded conversations from the cloud!",
        "All right, we're almost there, this will be done real soon!",
        "My my, you're really active in this app, I need just a little more time!");
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(currentUser.getDisplayName());
    private TreeMap<Date, String> uploadedFiles = new TreeMap<>();

    public TreeMap<Date, String> getUploadedFiles() {
        return uploadedFiles;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen);

        // Perform analysis iff not already done
        if (!FileProcessing.isInitialized()) {
            new logicThread().start();
            new backgroundThread().start();
            new textThread().start();
        } else {
            Intent intent = new Intent(WaitingScreenHistoryActivity.this, MessageStatisticsActivity.class);
            startActivityForResult(intent, 300);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            WaitingScreenHistoryActivity.this.finish();
        }
    }

    // This thread shall run our computationally-heavy methods
    public class logicThread extends Thread {
        @Override
        public void run() {
            // RETRIEVE FILES AND STORE IN TREEMAP, SORTED BY LAST_MODIFIED DATE
            mStorageRef.listAll().addOnSuccessListener(result -> {
                if (result.getItems().size() != 0) {
                    for (StorageReference fileRef : result.getItems()) {
                        fileRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                            Date fileDate = new Date(storageMetadata.getUpdatedTimeMillis());
                            String fileName = fileRef.getName();
                            uploadedFiles.put(fileDate, fileName);

                            if (uploadedFiles.size() == result.getItems().size()) {
                                // REDIRECT USER TO CHAT HISTORY SCREEN
                                Intent intent = new Intent(WaitingScreenHistoryActivity.this, ChatHistoryActivity.class);
                                intent.putExtra("uploadedFiles", uploadedFiles);
                                startActivityForResult(intent, 300);
                                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                                WaitingScreenHistoryActivity.this.finish();
                            }
                        });
                    }
                } else {
                    // REDIRECT USER TO CHAT HISTORY SCREEN
                    Intent intent = new Intent(WaitingScreenHistoryActivity.this, ChatHistoryActivity.class);
                    intent.putExtra("uploadedFiles", uploadedFiles);
                    startActivityForResult(intent, 300);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    WaitingScreenHistoryActivity.this.finish();
                }
            });
        }
    }

    // This thread shall loop our background gradients
    public class backgroundThread extends Thread {
        @Override
        public void run() {
            RelativeLayout cl = findViewById(R.id.layout);
            AnimationDrawable ad = (AnimationDrawable) cl.getBackground();
            ad.setEnterFadeDuration(2000);
            ad.setExitFadeDuration(4000);
            ad.start();
        }
    }

    // This thread shall loop our user prompts
    public class textThread extends Thread {
        @Override
        public void run() {
            final TextView talkToUser = (TextView) findViewById(R.id.talkToUser);
            for (int i = 0; i < 99; i++) {
                try {
                    talkToUser.setText(promptList.get(new Random().nextInt(promptList.size())));
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
