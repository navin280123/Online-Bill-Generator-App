package com.example.storemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class LoginPage extends AppCompatActivity {

    private EditText etPhoneNumber, etOTP;
    private Button btnGenerateOTP, btnVerifyOTP;
    private View EnterPhone,Enterotp;
    private LottieAnimationView loadingAnimation;
    private String phoneNumber, verificationId;

    Spinner countrySpinner ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        FirebaseApp.initializeApp(this);

        loadingAnimation = findViewById(R.id.otploadingAnimation);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etOTP = findViewById(R.id.etOTP);
        btnGenerateOTP = findViewById(R.id.btnGenerateOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        countrySpinner = findViewById(R.id.spCountryCode);
        EnterPhone = findViewById(R.id.llLogin);
        Enterotp = findViewById(R.id.llotp);

        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                phoneNumber = countrySpinner.getSelectedItem().toString().split("\\s+")[0] + etPhoneNumber.getText().toString().trim();
                phoneNumber = etPhoneNumber.getText().toString().trim();
                if (etPhoneNumber.getText().toString().length()!=10) {
                    FancyToast.makeText(LoginPage.this,"Please Enter a Valid Phone Number",FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
                } else {
                    LoginPage.this.verificationId = verificationId;
                    loadingAnimation.setVisibility(View.GONE);
                    EnterPhone.setVisibility(View.GONE);
                    Enterotp.setVisibility(View.VISIBLE);
                    FancyToast.makeText(LoginPage.this,"OTP SENT",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                }
            }
        });

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = etOTP.getText().toString().trim();
                if (pass.isEmpty()) {
                    FancyToast.makeText(LoginPage.this,"Please Enter The Password",FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
                } else {
                    verifyPassword(pass,phoneNumber);
                }
            }
        });


    }

    private void verifyPassword(String password,String mobile) {
        Log.d("mobile",mobile);
        Log.d("password",password);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("credential").child(mobile);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // node with mobile number exists
                    String storedPassword = dataSnapshot.getValue(String.class);
                    Log.d("Password",storedPassword);
                    if (password.equals(storedPassword)) {
                        // password is correct, navigate to MainActivity
                        // write on config.properties file to save the creadential of the user .
                        Properties properties = new Properties();
                        try {
                            properties.load(getBaseContext().getAssets().open("config.properties"));
                            properties.setProperty("Login.Status","true");
                            properties.setProperty("Login.Mobile",mobile);
                            properties.setProperty("Login.Password",password);
                            properties.store(getBaseContext().openFileOutput("config.properties",MODE_PRIVATE),null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch ( IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // password is incorrect, show toast message
                        FancyToast.makeText(LoginPage.this,"Invalid Password",FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
                    }
                } else {
                    // node with mobile number does not exist, show toast message
                    FancyToast.makeText(LoginPage.this,"No User Found",FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });
    }

}
