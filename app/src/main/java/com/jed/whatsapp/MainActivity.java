package com.jed.whatsapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    // ATTRIBUTES
    private static final String TAG = "MainActivity";
    private static final int VIEW_HISTORY_CODE = 1;
    private static final int LOGIN_REQUEST_CODE = 2;
    private static final int UPLOAD_REQUEST_CODE = 3;
    private static final int ANALYZE_REQUEST_CODE = 4;
    private static final int GRAPH_REQUEST_CODE = 5;
    private FirebaseAuth mAuth = null;
    private FirebaseUser mCurrentUser = null;
    private GoogleApiClient mGoogleApiClient = null;
    private StorageReference mStorageRef = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FIREBASE AUTHENTICATION STORAGE
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // LOGIN FAB
        // TODO : SET TO USER'S FIRST NAME INITIAL UPON LOGIN, TO MINIMIZE CONFUSION
        final FloatingActionButton loginButton = findViewById(R.id.loginFAB);
        loginButton.setOnClickListener(view -> {
            Log.d(TAG, "LOGIN BUTTON TRIGGERED");
            signIn();
        });

        loginButton.setOnLongClickListener(v -> {
            signOut();
            return true;
        });

        // VIEW HISTORY BUTTON
        // TODO : CHECK FOR CURRENT USER --> SNACKBAR --> NOT AVAILABLE IF NULL
        final Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        viewHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WaitingScreenHistoryActivity.class);
            startActivityForResult(intent, VIEW_HISTORY_CODE);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
        });

        // UPLOAD BUTTON
        /*
        TODO
        1. CHECK IF EXISTING UPLOAD (FROM FILE_PROCESSING.INITIALIZED) IS TRUE
        2. PROVIDE CONFIRMATION SCREEN IF ALREADY INITIALIZED
         */
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
            if (FileProcessing.getUploadedFileURI() == null) {
                String error = "I'm sorry, but you haven't yet given me a file to work with!";
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainActivity.this, WaitingScreenStatsActivity.class);
                intent.putExtra("StatsOrGraph", "Stats");
                startActivityForResult(intent, ANALYZE_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });

        // GRAPH BUTTON
        final Button graphButton = findViewById(R.id.graphButton);
        graphButton.setOnClickListener(v -> {
            if (!FileProcessing.isInitialized) {
                String error = "I'm sorry, but you haven't yet given me a file to work with!";
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            } else if (!ReplyTiming.isInitialized) {
                Intent intent = new Intent(MainActivity.this, WaitingScreenStatsActivity.class);
                intent.putExtra("StatsOrGraph", "Graph");
                startActivityForResult(intent, GRAPH_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            } else {
                Intent intent = new Intent(MainActivity.this, ScatterTimeActivity.class);
                startActivityForResult(intent, GRAPH_REQUEST_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case UPLOAD_REQUEST_CODE:
//                    String selectedFilePath = data.getData().getPath();
//                    File selectedFile = new File(selectedFilePath);
//                    FileProcessing.setUploadedFile(selectedFile);
//                    FileProcessing.setUserIntent(data);
                    FileProcessing.reset();
                    ReplyTiming.reset();
                    FileProcessing.setUploadedFileURI(data.getData());
                    FileProcessing.setIsInitialized(true);
                    uploadToFB(data);
                    break;

                case LOGIN_REQUEST_CODE:
                    Log.d(TAG, "TRIGGERED LOGIN PROCESS");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        Log.d(TAG, "START LOGIN PROCESS");
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e);
                    }
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
     *
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
            currentUserName = mAuth.getCurrentUser().getDisplayName();
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
     *
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

    // TODO : UNABLE TO LOGIN! (BUG)
    private void signIn() {
        Log.d(TAG, "signIn() function TRIGGERED");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, LOGIN_REQUEST_CODE);
            Log.d(TAG, "signIn() function ENDED");
            Log.d(TAG, "signInIntent" + signInIntent.toString());
        } else {
            String msg = "LONG HOLD TO SIGN OUT.";
            View contextView = findViewById(R.id.activity_main);
            Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
        }
    }

    // TODO : FIX BUG WHERE USER CAN STILL ACCESS HISTORY EVEN AFTER NOT SIGNING IN
    private void signOut() {
        if (mCurrentUser == null) {
            String msg = "SHORT TAP TO SIGN IN";
            View contextView = findViewById(R.id.activity_main);
            Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                String msg = "Goodbye! But do return soon with more chats for me!";
                View contextView = findViewById(R.id.activity_main);
                Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
            });
            mAuth.signOut();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed : " + connectionResult);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in mCurrentUser's information
                        Log.d(TAG, "signInWithCredential:success");
                        mCurrentUser = mAuth.getCurrentUser();
                        String msg = "Welcome, " + mCurrentUser.getDisplayName() + " I've " +
                                "securely linked you up with Google and our servers!";
                        View contextView = findViewById(R.id.activity_main);
                        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
                        Log.d(TAG, "SIGN IN SUCCESS");

                    } else {
                        // If sign in fails, display a message to the mCurrentUser.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
