package com.example.recycleview;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderBook {
    @SerializedName("asks")
    private List<List<String>> asks;

    @SerializedName("bids")
    private List<List<String>> bids;

    public List<List<String>> getAsks() {
        return asks;
    }

    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }

    public List<List<String>> getBids() {
        return bids;
    }

    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }
// Constructors, getters, and setters
}

