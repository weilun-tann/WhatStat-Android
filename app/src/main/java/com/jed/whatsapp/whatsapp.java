package com.jed.whatsapp;

import android.app.Application;

import com.firebase.client.Firebase;

public class whatsapp extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();

        // SET THE CONTEXT FOR OUR APPLICATION CLASS
        Firebase.setAndroidContext(this);
    }
}
