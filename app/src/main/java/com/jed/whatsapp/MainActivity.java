package com.jed.whatsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;

public class MainActivity extends Activity {

    // ATTRIBUTES
    private static final int VIEW_HISTORY_CODE = 1;
    private static final int LOGIN_REQUEST_CODE = 2;
    private static final int UPLOAD_REQUEST_CODE = 3;
    private static final int ANALYZE_REQUEST_CODE = 4;
    private static final int GRAPH_REQUEST_CODE = 5;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuthRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // INITIALIZE FIREBASE REFERENCES
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuthRef = FirebaseAuth.getInstance();

        // GUEST VS LOGGED IN USER
        if (mAuthRef.getCurrentUser() != null) {

            // SET LOGGED IN USER SCREEN
            setContentView(R.layout.activity_main_logged_in);

            // VIEW HISTORY BUTTON
            final Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
            viewHistoryButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, WaitingScreenHistoryActivity.class);
                startActivityForResult(intent, VIEW_HISTORY_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        } else {

            // SET GUEST SCREEN
            setContentView(R.layout.activity_main_guest);

            // LOGIN BUTTON
            final Button loginButton = findViewById(R.id.loginButton);
            loginButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            });
        }

        // UPLOAD BUTTON
        final Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            startActivityForResult(intent, UPLOAD_REQUEST_CODE);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        // ANALYZE BUTTON
        final Button analyzeButton = findViewById(R.id.analyzeButton);
        analyzeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WaitingScreenStatsActivity.class);
            startActivityForResult(intent, ANALYZE_REQUEST_CODE);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        // GRAPH BUTTON
        final Button graphButton = findViewById(R.id.graphButton);
        graphButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScatterChartTime.class);
            startActivityForResult(intent, GRAPH_REQUEST_CODE);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            switch (requestCode) {
                case UPLOAD_REQUEST_CODE:
//                    String selectedFilePath = data.getData().getPath();
//                    File selectedFile = new File(selectedFilePath);
//                    FileProcessing.setUploadedFile(selectedFile);
//                    FileProcessing.setUserIntent(data);
                    FileProcessing.setUploadedFileURI(data.getData());
                    FileProcessing.setInitialized(false);
                    uploadToFB(data);
                    break;

                case ANALYZE_REQUEST_CODE:
                    break;

                case GRAPH_REQUEST_CODE:
                    break;
            }
        }
    }


    /**
     * Uploads the chosen file to Firebase Storage in the cloud
     * @param data : Intent associated with the file upload
     */
    public void uploadToFB(Intent data) {

        // GET FILE NAME
        Uri uri = data.getData();
        String fileName = getFileName(uri);

        // GET CURRENT USER'S USERNAME
        String currentUserName = "GuestUser";
        String timeStamp = LocalDateTime.now().toString();
        try {
            currentUserName = mAuthRef.getCurrentUser().getDisplayName();
        } catch (NullPointerException e) {

        }

        // SET CLOUD PATH AND PERFORM UPLOAD
        StorageReference filePath = mStorageRef.child(currentUserName).child(fileName + "_" + timeStamp);
        UploadTask uploadTask = filePath.putFile(uri);

        // ADD SUCCESS AND FAILURE LISTENERS TO FILE UPLOAD PROCESS
        uploadTask.addOnFailureListener(exception ->
                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot ->
                        Toast.makeText(getApplicationContext(), "Upload Done", Toast.LENGTH_SHORT).show());
    }

    /**
     * Utility function to retrieve file name from URI
     * @param uri : URI of the file to extract name from
     * @return fileName of the file represented by its URI
     */
    public String getFileName(Uri uri) {
        ContentResolver cr = getApplicationContext().getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor metaCursor = cr.query(uri, projection, null, null, null);
        String fileName = "Unnamed File";
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    fileName = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return fileName;
    }
}
