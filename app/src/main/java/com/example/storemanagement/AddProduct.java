package com.example.storemanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productExpiryEditText;
    private EditText productBarcodeEditText;
    private Button addButton;

    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);



        productNameEditText = findViewById(R.id.editTextTextProductName);
        productPriceEditText = findViewById(R.id.editTextTextProductPrice);
        productExpiryEditText = findViewById(R.id.editTextTextProductExpiry);
        productBarcodeEditText = findViewById(R.id.editTextTextProductBarcodeValue);
        addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToDatabase();
            }
        });
    }

    private void addProductToDatabase() {
        String productName = productNameEditText.getText().toString();
        String productPrice = productPriceEditText.getText().toString();
        String productExpiry = productExpiryEditText.getText().toString();
        String productBarcode = productBarcodeEditText.getText().toString();
        productsRef = FirebaseDatabase.getInstance().getReference().child("7004394490").child("product");
        // Create a new product map
        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("price", productPrice);
        product.put("expiry", productExpiry);
        product.put("barcode", productBarcode);

        // Generate a new key for the product

        // Add the product to the database under the generated key
        productsRef.child(productBarcode).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddProduct.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                        // Clear input fields after successful addition
                        productNameEditText.setText("");
                        productPriceEditText.setText("");
                        productExpiryEditText.setText("");
                        productBarcodeEditText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProduct.this, "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
