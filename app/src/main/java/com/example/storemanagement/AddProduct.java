package com.example.storemanagement;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Camera;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProduct extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = ScanCode.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DatabaseReference productsRef;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Vibrator vibrator;
    private TextView message;
    private RecyclerView recyclerView;
    private boolean isCameraPermissionGranted = false;
    private TextView productName;
    private TextView productQuantity;
    private ImageView decrementButton;
    private ImageView incrementButton;
    private Button addProductButton;
    private HashMap<String,Product> product = new HashMap<>();
    private List<String> list = new ArrayList<>();
    private String ID,billNumber;
    String barcodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        surfaceView = findViewById(R.id.camera_preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        productName = findViewById(R.id.productname);
        productQuantity = findViewById(R.id.productqnt);
        decrementButton = findViewById(R.id.decrement);
        incrementButton = findViewById(R.id.increment);
        addProductButton = findViewById(R.id.addProduct);
        //get the intent value
        ID = getIntent().getStringExtra("ID");
        billNumber = getIntent().getStringExtra("billNumber");

        getProductList(ID);
        // Set onClick listeners
        decrementButton.setOnClickListener(view -> decrementProductQuantity());
        incrementButton.setOnClickListener(view -> incrementProductQuantity());

        // Set onClick listener for add product button
        addProductButton.setOnClickListener(view -> addProduct());
    }

    private void decrementProductQuantity() {
        int quantity = Integer.parseInt(productQuantity.getText().toString());
        if (quantity > 1) {
            quantity--;
            productQuantity.setText(String.valueOf(quantity));
        }
    }

    private void incrementProductQuantity() {
        int quantity = Integer.parseInt(productQuantity.getText().toString());
        quantity++;
        productQuantity.setText(String.valueOf(quantity));
    }

    private void addProduct() {
        // Add your logic here for adding the product
        // For example, you can get the product name and quantity
        String name = productName.getText().toString();
        int quantity = Integer.parseInt(productQuantity.getText().toString());
        Product selectedProduct =product.get(barcodeText);
        addProductToBillNode(quantity, billNumber, selectedProduct);
        // Perform actions to add the product
    }
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
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
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
                barcodeText = result.getText();
                Log.d("Barcode", "Barcode detected: " + barcodeText);
                vibrator.vibrate(100);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String user = mAuth.getCurrentUser().getEmail();
                productName.setText(barcodeText);
            }
        } catch (Exception e) {
            Log.d("NO barcode", "No Barcode detected: ");
            // No barcode found in the frame, ignore
        }
    }
    private void getProductList(String ID) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(ID).child("product");
        System.out.println(databaseReference);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getChildrenCount());
                System.out.println(dataSnapshot);
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String productId = productSnapshot.getKey();
//                    System.out.println("Product ID: " + productId);
                    String barcode = productSnapshot.child("barcode").getValue(String.class);
//                    System.out.println("Barcode: " + barcode);
                    String category = productSnapshot.child("category").getValue(String.class);
//                    System.out.println("Category: " + category);
                    String expiryDate = productSnapshot.child("expiryDate").getValue(String.class);
//                    System.out.println("Expiry Date: " + expiryDate);
                    String hsn = productSnapshot.child("hsn").getValue(String.class);
//                    System.out.println("HSN: " + hsn);
                    double markedPrice = productSnapshot.child("markedPrice").getValue(Double.class);
//                    System.out.println("Marked Price: " + markedPrice);
                    String name = productSnapshot.child("name").getValue(String.class);
//                    System.out.println("Name: " + name);
                    double purchasedPrice = productSnapshot.child("purchasedPrice").getValue(Double.class);
//                    System.out.println("Purchased Price: " + purchasedPrice);
                    int quantity = productSnapshot.child("quantity").getValue(Integer.class);
//                    System.out.println("Quantity: " + quantity);
                    double sellingPrice = productSnapshot.child("sellingPrice").getValue(Double.class);
//                    System.out.println("Selling Price: " + sellingPrice);
                    String subcategory = productSnapshot.child("subcategory").getValue(String.class);
//                    System.out.println("Subcategory: " + subcategory);
                    double tax = productSnapshot.child("tax").getValue(Double.class);
//                    System.out.println("Tax: " + tax);
//                    System.out.println("--------------------------------------");
                    list.add(barcode);
                    product.put(barcode,new Product(barcode,category,expiryDate,hsn,name,subcategory,markedPrice,purchasedPrice,sellingPrice,tax,quantity));

                }
                Log.d("Product", "Product list: " + list);
                Log.d("Product", "Product details: " + product);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error fetching product details: " + databaseError.getMessage());
            }
        });


    }
    private void addProductToBillNode(Integer quantity, String billNumber, Product selectedProduct) {
        // Initialize Firebase database reference
        DatabaseReference billref = FirebaseDatabase.getInstance().getReference().child(ID).child("Bills");

        // Create a new product map
        Map<String, Object> product = new HashMap<>();
        double tax = ((selectedProduct.tax / selectedProduct.sellingPrice) * 100);
        double total = (selectedProduct.sellingPrice + ((selectedProduct.tax / selectedProduct.sellingPrice) * 100)) * quantity;
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedValuetax = df.format(tax);
        String formattedValuetotal = df.format(total);
        double roundedtax = Double.parseDouble(formattedValuetax);
        double roundedTotal = Double.parseDouble(formattedValuetotal);

        // Add product details to the map
        product.put("name", selectedProduct.name);
        product.put("MRP", selectedProduct.markedPrice);
        product.put("discount", selectedProduct.markedPrice - selectedProduct.sellingPrice);
        product.put("sellingPrice", selectedProduct.sellingPrice);
        product.put("tax", roundedtax);
        product.put("quantity", quantity);
        product.put("total", roundedTotal);

        // Set the values in the Firebase database
        billref.child(billNumber).child(selectedProduct.barcode).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Product details added successfully
                        Log.d(TAG, "Product added to bill node successfully");
                        // Optionally, you can perform any UI updates or show a toast message here
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add product details
                        Log.e(TAG, "Error adding product to bill node: " + e.getMessage());
                        // Optionally, you can show an error message to the user
                    }
                });
    }

}
