package com.example.recycleview;

import androidx.annotation.NonNull;

public class HistoryCardView implements CharSequence {
    private double commission;
    private boolean isBuyer;
    private long orderID;
    private double price;
    private String symbol;
    private long time;
    private double quantity;
    private int ImageId;

    public HistoryCardView(double commission, boolean isBuyer, long orderID, double price, String symbol, long time, double quantity, int imageId) {
        this.commission = commission;
        this.isBuyer = isBuyer;
        this.orderID = orderID;
        this.price = price;
        this.symbol = symbol;
        this.time = time;
        this.quantity = quantity;
        ImageId = imageId;
    }



    public HistoryCardView() {}

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public boolean isBuyer() {
        return isBuyer;
    }

    public void setBuyer(boolean buyer) {
        isBuyer = buyer;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return null;
    }
}
