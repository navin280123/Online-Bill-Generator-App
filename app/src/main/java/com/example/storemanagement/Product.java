package com.example.storemanagement;


public class Product {
    String barcode,category,expiryDate,hsn,name,subcategory;
    double markedPrice,purchasedPrice,sellingPrice,tax;
    int quantity;
    Product(String barcode,String category,String expiryDate,String hsn,String name,String subcategory,double markedPrice,double purchasedPrice,
            double sellingPrice,double tax,int quantity){
        this.barcode = barcode;
        this.category=category;
        this.expiryDate=expiryDate;
        this.hsn=hsn;
        this.name= name;
        this.subcategory=subcategory;
        this.markedPrice=markedPrice;
        this.purchasedPrice=purchasedPrice;
        this.sellingPrice=sellingPrice;
        this.tax=tax;
        this.quantity=quantity;

    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getHsn() {
        return hsn;
    }
    public void setHsn(String hsn) {
        this.hsn = hsn;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSubcategory() {
        return subcategory;
    }
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    public double getMarkedPrice() {
        return markedPrice;
    }
    public void setMarkedPrice(double markedPrice) {
        this.markedPrice = markedPrice;
    }
    public double getPurchasedPrice() {
        return purchasedPrice;
    }
    public void setPurchasedPrice(double purchasedPrice) {
        this.purchasedPrice = purchasedPrice;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public double getTax() {
        return tax;
    }
    public void setTax(double tax) {
        this.tax = tax;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
