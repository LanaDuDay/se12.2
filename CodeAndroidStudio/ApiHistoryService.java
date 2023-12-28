package com.example.recycleview;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
public interface ApiHistoryService {
    @POST("/trade_history")
    Call<TradeHistoryResponse> getTradeHistory();
}

