package com.example.storemanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button ScanBarCode,AddProduct,ViewProductList,ViewBillHistroy,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScanBarCode = findViewById(R.id.scan_barcode);
        AddProduct = findViewById(R.id.add_product);
        ViewProductList = findViewById(R.id.product_list);
        ViewBillHistroy = findViewById(R.id.bill_history);
        logout = findViewById(R.id.logoutuser);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getEmail();
        Log.d("user", "onCreate: " + user);
        //create addactionlistner for each button to start a new activity
        ScanBarCode.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanBarCode.class);
            startActivity(intent);
        });
        AddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProduct.class);
            startActivity(intent);
        });
        ViewProductList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewProductList.class);
            startActivity(intent);
        });
        ViewBillHistroy.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewBillHistroy.class);
            startActivity(intent);
        });
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        });
    }
}