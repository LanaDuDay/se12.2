package com.example.recycleview;

public class QueueItem {
    private String symbol;
    private double price;
    private double quantity;
    private String time;

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getTime() {
        return time;
    }

    public QueueItem(String symbol, double price, double quantity, String time) {
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
    }
}

