package com.example.storemanagement;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class LoadingScreen extends AppCompatActivity {
    // Set the duration of the splash screen
    private static final long SPLASH_DELAY = 5000; // seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);


        // Delay the start of the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    //check if the application is already login in with the help of get userID if exist jump to main activity elase to the login page
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("user", "onCreate: " + user);
                    if (user != null) {
                        Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(LoadingScreen.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    }


            }

        }, SPLASH_DELAY);
    }
}

