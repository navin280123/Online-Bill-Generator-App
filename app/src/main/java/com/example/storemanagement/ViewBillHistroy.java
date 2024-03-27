package com.example.storemanagement;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewBillHistroy extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillAdapter adapter;
    private List<Bill> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill_histroy);

        recyclerView = findViewById(R.id.billrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        billList = new ArrayList<>();
        adapter = new BillAdapter(this, billList);
        recyclerView.setAdapter(adapter);

        // Retrieve bills from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Store 1").child("bills");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                billList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //create three string with the value inside the snapshot and add it to the billList
                    String billId = snapshot.child("billId").getValue(String.class);
                    String customerName = snapshot.child("CustomerName").getValue(String.class);
                    String totalAmount = snapshot.child("TotalAmount").getValue(String.class);
                    billList.add(new Bill(billId, customerName, totalAmount));

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
