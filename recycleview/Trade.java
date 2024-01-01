package com.example.recycleview;

import com.google.gson.annotations.SerializedName;

public class Trade {

    @SerializedName("commission")
    private double commission;

    @SerializedName("isBuyer")
    private boolean isBuyer;

    @SerializedName("orderId")
    private long orderId;

    @SerializedName("price")
    private double price;

    @SerializedName("quantity")
    private double quantity;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("time")
    private long time;

    // Thêm các trường khác cần thiết

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

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
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

    @Override
    public String toString() {
        return "Trade{" +
                "commission=" + commission +
                ", isBuyer=" + isBuyer +
                ", orderId=" + orderId +
                ", price=" + price +
                ", quantity=" + quantity +
                ", symbol='" + symbol + '\'' +
                ", time=" + time +
                // Thêm các trường khác vào đây nếu cần
                '}';
    }
}

