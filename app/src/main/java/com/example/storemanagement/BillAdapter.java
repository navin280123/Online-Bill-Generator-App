package com.example.storemanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private Context context;
    private List<Bill> billList;

    public BillAdapter(Context context, List<Bill> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bill_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.bind(bill);
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public class BillViewHolder extends RecyclerView.ViewHolder {

        private TextView billIdTextView;
        private TextView customerNameTextView;
        private TextView totalAmountTextView;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            billIdTextView = itemView.findViewById(R.id.bill_id_text_view);
            customerNameTextView = itemView.findViewById(R.id.customer_name_text_view);
            totalAmountTextView = itemView.findViewById(R.id.total_amount_text_view);
        }

        public void bind(Bill bill) {
            billIdTextView.setText("Bill ID: " + bill.getBillId());
            customerNameTextView.setText("Customer: " + bill.getCustomerName());
            totalAmountTextView.setText("Total Amount: $" + bill.getTotalAmount());
            // You can bind more fields here if needed
        }
    }
}

