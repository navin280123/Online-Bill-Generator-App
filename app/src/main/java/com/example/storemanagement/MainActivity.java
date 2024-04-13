package com.example.storemanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ImageView ScanBarCode,EnterCode;
    Button logout;
    private TextView scan,code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.logoutuser);
        ScanBarCode = findViewById(R.id.scanimg);
        EnterCode = findViewById(R.id.pinimg);
        code = findViewById(R.id.pin);
        scan = findViewById(R.id.scan);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getEmail();
        Log.d("user", "onCreate: " + user);
        //create addactionlistner for each button to start a new activity
        ScanBarCode.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanCode.class);
            startActivity(intent);
        });
        scan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanCode.class);
            startActivity(intent);
        });
        EnterCode.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Enter_Pin.class);
            startActivity(intent);
        });
        code.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Enter_Pin.class);
            startActivity(intent);
        });
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        });
    }
}