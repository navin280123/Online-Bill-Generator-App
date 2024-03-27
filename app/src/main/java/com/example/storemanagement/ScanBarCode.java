package com.example.storemanagement;

import android.Manifest;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanBarCode extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = ScanBarCode.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DatabaseReference productsRef;
    private Camera camera;
    private SurfaceView surfaceView;
    private RecyclerView.Adapter<MyViewHolder> adapter;
    private SurfaceHolder surfaceHolder;
    private Vibrator vibrator;
    private RecyclerView recyclerView;
    private List<String> barcodes;
    private boolean isCameraPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        recyclerView = findViewById(R.id.recycler_view);
        barcodes = new ArrayList<>();

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and assign it to the RecyclerView
        adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                String item = barcodes.get(position);
                holder.bind(item);
            }

            @Override
            public int getItemCount() {
                return barcodes.size();
            }
        };

        recyclerView.setAdapter(adapter);

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
                // Vibrate to provide feedback
                if (!barcodes.contains(barcodeText)) {
                    vibrator.vibrate(100);
                    productsRef = FirebaseDatabase.getInstance().getReference().child("7004394490").child("product");
                    productsRef.child(barcodeText).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String name = task.getResult().child("name").getValue().toString();
                            String price = task.getResult().child("price").getValue().toString();
                            String expiry = task.getResult().child("expiry").getValue().toString();
                            String barcode = task.getResult().child("barcode").getValue().toString();
                            barcodes.add(name + "     " +price);
                            adapter.notifyItemInserted(barcodes.size() - 1);
                        } else {
                            Log.d("Barcode", "Error getting data: ", task.getException());
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.d("NO barcode", "No Barcode detected: ");
            // No barcode found in the frame, ignore
        }
    }

    // View Holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public void bind(String item) {
            textView.setText(item);
        }
    }
}
