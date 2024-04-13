package com.example.storemanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storemanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;

public class ScanCode extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = ScanCode.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DatabaseReference productsRef;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Vibrator vibrator;

    private boolean isCameraPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        surfaceView = findViewById(R.id.camera_preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCameraPermissionGranted) {
            openCamera();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(surfaceHolder);
                camera.setDisplayOrientation(90);
                camera.setPreviewCallback(this);
                camera.startPreview();
            } catch (IOException e) {
                Log.e(TAG, "Error opening camera: " + e.getMessage());
            }
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (checkCameraPermission()) {
            isCameraPermissionGranted = true;
            openCamera();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Surface changed, no action needed
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        int width = previewSize.width;
        int height = previewSize.height;

        // Convert preview frame data to BinaryBitmap for ZXing library
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)));

        // Use MultiFormatReader to decode barcode
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            if (result != null) {
                String barcodeText = result.getText();
                Log.d("Barcode", "Barcode detected: " + barcodeText);
                vibrator.vibrate(100);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String user = mAuth.getCurrentUser().getEmail();
                processUser(user,barcodeText);
            }
        } catch (Exception e) {
            Log.d("NO barcode", "No Barcode detected: ");
            // No barcode found in the frame, ignore
        }
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
                        Log.d("Email", "Email found in database.");
                        userPhone = childSnapshot.getKey();
                        DatabaseReference billsNode = database.child(userPhone).child("Bills");
                        Log.d("Test",userPhone);
                        Log.d("Test",Billno);
                        Log.d("Test",billsNode.toString());

                        String finalUserPhone = userPhone;
                        Intent intent = new Intent(ScanCode.this, AddProduct.class);
                        intent.putExtra("billNumber", Billno);
                        intent.putExtra("ID", finalUserPhone);
                        startActivity(intent);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    // View Holder

}