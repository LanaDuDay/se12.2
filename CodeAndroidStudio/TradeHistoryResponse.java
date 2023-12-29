package com.example.recycleview;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TradeHistoryResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("trade_history")
    private List<Trade> tradeHistory;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Trade> getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(List<Trade> tradeHistory) {
        this.tradeHistory = tradeHistory;
    }
}

