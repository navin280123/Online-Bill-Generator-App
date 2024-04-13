package com.example.storemanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Enter_Pin extends AppCompatActivity  {

    TextView textView5, textView4;
    TextView num1, num2, num3, num4, num5, num6, num7, num8, num9, num0, cancel, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        // Initialize TextViews
        textView5 = findViewById(R.id.textView5);
        textView4 = findViewById(R.id.textView4);

        // Initialize Number TextViews
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);
        num7 = findViewById(R.id.num7);
        num8 = findViewById(R.id.num8);
        num9 = findViewById(R.id.num9);
        num0 = findViewById(R.id.num0);

        // Initialize Action TextViews
        cancel = findViewById(R.id.cancel);
        check = findViewById(R.id.check);

        num1.setOnClickListener(v->
        {
            appendText("1");
        });
        num2.setOnClickListener(v->
        {
            appendText("2");
        });
        num3.setOnClickListener(v->
        {
            appendText("3");
        });
        num4.setOnClickListener(v->
        {
            appendText("4");
        });
        num5.setOnClickListener(v->
        {
            appendText("5");
        });
        num6.setOnClickListener(v->
        {
            appendText("6");
        });
        num7.setOnClickListener(v->
        {
            appendText("7");
        });
        num8.setOnClickListener(v->
        {
            appendText("8");
        });
        num9.setOnClickListener(v->
        {
            appendText("9");
        });
        num0.setOnClickListener(v->
        {
            appendText("0");
        });
        cancel.setOnClickListener(v->
        {
            clearText();
        });
        check.setOnClickListener(v->
        {
            // Add your code here
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String user = mAuth.getCurrentUser().getEmail();
            processUser(user,textView4.getText().toString());
        });

    }



    private void appendText(String text) {
        String currentText = textView4.getText().toString();
        textView4.setText(currentText + text);
    }
    public void processUser(String userEmail,String Billno) {
        // 1. Get the email of the user
        String email = userEmail;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference emailAndPhoneNode = database.child("EmailAndPhone");
        emailAndPhoneNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userPhone;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if(childSnapshot.getValue().equals(email)){
                        if(childSnapshot.getValue().equals(email)){
                            Log.d("Email", "Email found in database.");
                            userPhone = childSnapshot.getKey();
                            DatabaseReference billsNode = database.child(userPhone).child("Bills");
                            Log.d("Test",userPhone);
                            Log.d("Test",Billno);
                            Log.d("Test",billsNode.toString());

                            String finalUserPhone = userPhone;
                            Intent intent = new Intent(Enter_Pin.this, AddProduct.class);
                            intent.putExtra("billNumber", Billno);
                            intent.putExtra("ID", finalUserPhone);
                            startActivity(intent);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void clearText() {
        textView4.setText(textView4.getText().toString().substring(0, textView4.getText().length() - 1));
    }
}
