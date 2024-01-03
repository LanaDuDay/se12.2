package com.example.recycleview;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/get_account_info") // Điền đúng đường dẫn của API Flask của bạn
    Call<MarketData> getMarketData();
}

