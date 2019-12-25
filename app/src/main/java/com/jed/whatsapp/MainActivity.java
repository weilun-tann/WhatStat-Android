package com.jed.whatsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.firebase.client.Firebase;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    // ATTRIBUTES
    private static final int UPLOAD_REQUEST_CODE = 1;
    private static final int ANALYZE_REQUEST_CODE = 2;
    private static final int GRAPH_REQUEST_CODE = 3;
    private static final int LOGIN_REQUEST_CODE = 4;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EMAIL BUTTON
        FloatingActionButton fab = findViewById(R.id.emailButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "NO FUNCTIONALITY YET", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });

        // UPLOAD BUTTON
        final Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });

        // ANALYZE BUTTON
        final Button analyzeButton = findViewById(R.id.analyzeButton);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WaitingScreenActivity.class);
                startActivityForResult(intent, ANALYZE_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });

        // GRAPH BUTTON
        final Button graphButton = findViewById(R.id.graphButton);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScatterChartTime.class);
                startActivityForResult(intent, GRAPH_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPLOAD_REQUEST_CODE:
                    String selectedFilePath = data.getData().getPath();
                    File selectedFile = new File(selectedFilePath);
                    FileProcessing.setUploadedFile(selectedFile);
                    FileProcessing.setUserIntent(data);
                    FileProcessing.setInitialized(false);

                    // FIREBASE STORAGE SETUP
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    Uri uri = data.getData();

                    // GET CURRENT USER IF EXISTS
                    String currentUserName;
                    String timeStamp = LocalDateTime.now().toString();
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        currentUserName =
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    } else {
                        currentUserName = "Guest User";
                    }

                    StorageReference filePath =
                            mStorageRef.child(currentUserName).child(uri.getLastPathSegment()+"_"+timeStamp);
                    UploadTask uploadTask = filePath.putFile(uri);


                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getApplicationContext(), "Upload Failed",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Toast.makeText(getApplicationContext(), "Upload Done",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                    break;

                case ANALYZE_REQUEST_CODE:
                    break;

                case GRAPH_REQUEST_CODE:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
