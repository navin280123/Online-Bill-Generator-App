package com.example.storemanagement;

public class Bill {
    private String billId;
    private String customerName;
    private String totalAmount;
    // Add more fields as needed

    public Bill() {
        // Default constructor required for Firebase
    }

    public Bill(String billId, String customerName, String totalAmount) {
        this.billId = billId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Add getter and setter methods for additional fields if needed
}
