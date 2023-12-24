package com.example.myapplication;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.Call;


public interface BotApi {
    @POST("/start_auto_trade")
    Call<Void> startAutoTrade();

    @GET("/get_account_info")
    Call<AccountInfo> getAccountInfo();

    // Thêm các phương thức tương tự cho các hành động khác
}


