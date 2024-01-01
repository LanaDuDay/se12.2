package com.example.recycleview;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MarketData {
    @SerializedName("PriceCoin")
    private List<PriceCoin> priceCoin;

    public List<PriceCoin> getPriceCoin() {
        return priceCoin;
    }

    public void setPriceCoin(List<PriceCoin> priceCoin) {
        this.priceCoin = priceCoin;
    }

    public static class PriceCoin {
        @SerializedName("symbol")
        private String symbol;

        @SerializedName("price")
        private double price;

        @SerializedName("quantity")
        private double quantity;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
