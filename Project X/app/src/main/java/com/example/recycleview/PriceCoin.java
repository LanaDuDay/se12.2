package com.example.recycleview;


public class PriceCoin {
    private String CoinName;
    private double Price;
    private double Quantity;
    private int InmageId;

    public PriceCoin(String coinName, double price, int inmageId, double quantity) {
        CoinName = coinName;
        Price = price;
        InmageId = inmageId;
        Quantity = quantity;
    }

    public PriceCoin() {

    }
    public  double getPriceB() {
        return Price * Quantity;
    }
    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public String getCoinName() {
        return CoinName;
    }

    public void setCoinName(String coinName) {
        CoinName = coinName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getInmageId() {
        return InmageId;
    }

    public void setInmageId(int inmageId) {
        InmageId = inmageId;
    }
}
