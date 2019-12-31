package com.jed.whatsapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
    private GoogleApiClient mGoogleApiClient = null;
    private StorageReference mStorageRef = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FIREBASE AUTHENTICATION STORAGE
        mAuth = FirebaseAuth.getInstance();
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
        final FloatingActionButton loginButton = findViewById(R.id.loginFAB);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loginButton.setImageResource(R.drawable.black_connected_icon);
        }

        loginButton.setOnClickListener(view -> {
            signIn();
        });

        loginButton.setOnLongClickListener(v -> {
            signOut();
            return true;
        });

        // FILE FAB - onCreate() will be called again upon return
        final FloatingActionButton fileButton = findViewById(R.id.fileFAB);

        fileButton.setOnClickListener(view -> {
            if (FileProcessing.isInitialized()) {
                String msg = "Current File : " + FileProcessing.getFileName();
                View contextView = findViewById(R.id.activity_main);
                Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
            } else {
                String msg = "No File Uploaded";
                View contextView = findViewById(R.id.activity_main);
                Snackbar uploadBar = Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG);
                uploadBar.setAction(R.string.uploadBarButton, new uploadBarListener());
                uploadBar.setActionTextColor(getResources().getColor(R.color.white));
                uploadBar.show();
            }
        });

        // VIEW HISTORY BUTTON
        final Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        viewHistoryButton.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                String error = "Try signing in first before accessing your secret file stash!";
                View contextView = findViewById(R.id.activity_main);
                Snackbar signInBar = Snackbar.make(contextView, error, Snackbar.LENGTH_LONG);
                signInBar.setAction(R.string.loginBarButton, new loginBarListener());
                signInBar.setActionTextColor(getResources().getColor(R.color.white));
                signInBar.show();
            } else {
                Intent intent = new Intent(MainActivity.this, WaitingScreenHistoryActivity.class);
                startActivityForResult(intent, VIEW_HISTORY_CODE);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            }
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
                View contextView = findViewById(R.id.activity_main);
                Snackbar.make(contextView, error, Snackbar.LENGTH_LONG).show();
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
            if (!FileProcessing.isInitialized()) {
                String error = "I'm sorry, but you haven't yet given me a file to work with!";
                View contextView = findViewById(R.id.activity_main);
                Snackbar.make(contextView, error, Snackbar.LENGTH_LONG).show();
            } else if (!ReplyTiming.isInitialized()) {
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
    protected void onResume() {
        super.onResume();
        final FloatingActionButton fileButton = findViewById(R.id.fileFAB);
        if (FileProcessing.isInitialized() && FileProcessing.getUploadedFileURI() != null) {
            Log.d(TAG, "triggered colour change in onResume()");
            ColorStateList c = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                    R.color.fileFABActive));
            fileButton.setBackgroundTintList(c);
        } else {
            ColorStateList c = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                    R.color.fileFABDormant));
            fileButton.setBackgroundTintList(c);
        }
    }

    /**
     * CUSTOM LOGIN SNACKBAR LISTENER (FOR FAILED VIEW HISTORY)
     */
    public class loginBarListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            signIn();
        }
    }

    /**
     * CUSTOM UPLOAD SNACKBAR LISTENER (FOR FILE FAB)
     */
    public class uploadBarListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Button uploadButton = findViewById(R.id.uploadButton);
            uploadButton.performClick();
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
        String fileName = FileProcessing.getFileName(getApplicationContext(), uri);

        // GET CURRENT USER'S USERNAME
        String currentUserName = "GuestUser";
        String timeStamp = LocalDateTime.now().toString();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) currentUserName = user.getDisplayName();

        // SET CLOUD PATH AND PERFORM UPLOAD
        StorageReference filePath = mStorageRef.child(currentUserName).child(fileName + "_" + timeStamp);
        UploadTask uploadTask = filePath.putFile(uri);

        // ADD SUCCESS AND FAILURE LISTENERS TO FILE UPLOAD PROCESS
        uploadTask.addOnFailureListener(exception ->
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot ->
                        Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show());
    }


    private void signIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, LOGIN_REQUEST_CODE);
        } else {
            String msg = "LONG HOLD TO SIGN OUT";
            View contextView = findViewById(R.id.activity_main);
            Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
        }
    }

    private void signOut() {
        if (mAuth.getCurrentUser() == null) {
            String msg = "SHORT TAP TO SIGN IN";
            View contextView = findViewById(R.id.activity_main);
            Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                String msg = "Goodbye! But do return soon with more chats for me!";
                View contextView = findViewById(R.id.activity_main);
                Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
                // CHANGE THE UI OF LOGIN FAB TO GUEST USER
                final FloatingActionButton loginButton = findViewById(R.id.loginFAB);
                loginButton.setImageResource(R.drawable.google_plus_icon);
            });
            mAuth.signOut();
        }
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
                    FileProcessing.setFileName(FileProcessing.getFileName(getApplicationContext()
                            , data.getData()));
                    FileProcessing.setInitialized(true);

                    // TURN FILE FAB GREEN
                    FloatingActionButton fileButton = findViewById(R.id.fileFAB);
                    ColorStateList c = ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),
                            R.color.fileFABActive));
                    fileButton.setBackgroundTintList(c);

                    // UPLOAD THE LOCAL FILE TO FIREBASE STORAGE
                    uploadToFB(data);
                    break;

                case LOGIN_REQUEST_CODE:
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
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
     * Helper function to process connection failure to Google
     * @param connectionResult : indicates the result of the Google sign-in
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed : " + connectionResult);
    }

    /**
     * Performs Firebase server-side authentication using the user's Google account
     * @param acct : the GoogleAccount object used by the user to sign in
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
                        String msg = "Welcome, " + mCurrentUser.getDisplayName() + " I've " +
                                "securely linked you up with Google and our servers!";
                        View contextView = findViewById(R.id.activity_main);
                        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
                        Log.d(TAG, "signInWithCredential:success");

                        // CHANGE THE UI OF LOGIN FAB TO SIGNED IN GOOGLE USER
                        final FloatingActionButton loginButton = findViewById(R.id.loginFAB);
                        loginButton.setImageResource(R.drawable.black_connected_icon);

                    } else {
                        String msg = "This is embarrassing, we didn't manage to sign you in to " +
                                "Google...";
                        View contextView = findViewById(R.id.activity_main);
                        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
