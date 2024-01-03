package com.example.recycleview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setupButtonClickListeners();

        List<HistoryCardView> historicDataList = new ArrayList<>();
        List<Double> commission = new ArrayList<>();
        List<Boolean> isBuyer = new ArrayList<>();
        List<Long> orderID = new ArrayList<>();
        List<Double> price = new ArrayList<>();
        List<Double> quantity = new ArrayList<>();
        List<String> symbol = new ArrayList<>();
        List<Long> time = new ArrayList<>();

        ApiHistoryService apiHistoryService = RetrofitClient.getApiHistoryService();
        Call<TradeHistoryResponse> call = apiHistoryService.getTradeHistory();
        call.enqueue(new Callback<TradeHistoryResponse>() {
            @Override
            public void onResponse(Call<TradeHistoryResponse> call, Response<TradeHistoryResponse> response) {
                if (response.isSuccessful()) {
                    TradeHistoryResponse tradeHistoryResponse = response.body();
                    for (int i = tradeHistoryResponse.getTradeHistory().size() -1; i >= 0; i--) {
                        orderID.add(tradeHistoryResponse.getTradeHistory().get(i).getOrderId());
                        commission.add(tradeHistoryResponse.getTradeHistory().get(i).getCommission());
                        price.add(tradeHistoryResponse.getTradeHistory().get(i).getPrice());
                        symbol.add(tradeHistoryResponse.getTradeHistory().get(i).getSymbol());
                        quantity.add(tradeHistoryResponse.getTradeHistory().get(i).getQuantity());
                        time.add(tradeHistoryResponse.getTradeHistory().get(i).getTime());
                        isBuyer.add(tradeHistoryResponse.getTradeHistory().get(i).isBuyer());
                    }
                    if(tradeHistoryResponse.getTradeHistory() != null) {

                        List<HistoryCardView> historyCardViewList = new ArrayList<>();
                        for (int i = 0; i < 10; i++) {
                            historyCardViewList.add(new HistoryCardView(commission.get(i),isBuyer.get(i),orderID.get(i),price.get(i),symbol.get(i),time.get(i),quantity.get(i),R.drawable.img));
                        }
                        recyclerView = findViewById(R.id.recyclerView1);
                        MyHistoryTradeAdapter myHistoryTradeAdapter = new MyHistoryTradeAdapter(historyCardViewList);
                        recyclerView.setAdapter(myHistoryTradeAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity2.this, 1));
                    } else {
                        Toast.makeText(MainActivity2.this, "null", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity2.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<TradeHistoryResponse> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupButtonClickListeners() {
        setOnClickListener(R.id.imageViewButton1, MainActivity3.class);
        setOnClickListener(R.id.imageViewButton2, MainActivity.class);
        setOnClickListener(R.id.imageViewButton3, MainActivity2.class);
        setOnClickListener(R.id.imageViewButton4, UserProfileActivity.class);
    }

    private void setOnClickListener(int imageViewId, final Class<?> targetActivity) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define Intent for the target activity
                Intent intent = new Intent(MainActivity2.this, targetActivity);

                // Run the Intent
                startActivity(intent);

                // Add transition animation (optional)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}