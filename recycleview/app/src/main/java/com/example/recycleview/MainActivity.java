package com.example.recycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Handler;
import android.os.Bundle;
import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;
import android.content.Intent;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.ViewPropertyAnimator;



public class MainActivity extends AppCompatActivity {
    TextView textView;
    ApiService apiService;
    private RecyclerView recyclerView;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtonClickListeners();
        handler = new Handler(Looper.getMainLooper());
        fetchDataAndUpdateUI();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchDataAndUpdateUI();
                handler.postDelayed(this, 60000); // 1 phút = 60,000 milliseconds
            }
        }, 60000);
    }

    private void setupButtonClickListeners() {
        setOnClickListener(R.id.imageViewButton1, MainActivity3.class);
        setOnClickListener(R.id.imageViewButton2, MainActivity.class);
        setOnClickListener(R.id.imageViewButton3, MainActivity2.class);
        setOnClickListener(R.id.imageViewButton4, MainActivity4.class);
    }

    private void setOnClickListener(int imageViewId, final Class<?> targetActivity) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a ViewPropertyAnimator for the clicked ImageView
                ViewPropertyAnimator animator = v.animate();

                // Add the desired animation effects
                animator.alpha(0.5f)  // Example: Reduce opacity
                        .scaleX(1.2f)  // Example: Zoom in horizontally
                        .scaleY(1.2f)  // Example: Zoom in vertically
                        .setDuration(200)  // Set the duration of the animation (in milliseconds)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Define Intent for the target activity
                                Intent intent = new Intent(MainActivity.this, targetActivity);

                                // Run the Intent
                                startActivity(intent);

                                // Add transition animation (optional)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                // Reset the properties after the animation ends
                                v.setAlpha(1.0f);
                                v.setScaleX(1.0f);
                                v.setScaleY(1.0f);
                            }
                        });

                // Start the animation
                animator.start();
            }
        });
    }


    private void fetchDataAndUpdateUI() {
        List<PriceCoin> priceCoinList = new ArrayList<>();
        List<String> symbolList = new ArrayList<>();
        List<Double> priceList = new ArrayList<>();
        List<Double> quantityList = new ArrayList<>();
        apiService = RetrofitClient.getApiService();

        Call<MarketData> call = apiService.getMarketData();
        call.enqueue(new Callback<MarketData>() {
            @Override
            public void onResponse(Call<MarketData> call, Response<MarketData> response) {
                if (response.isSuccessful()) {
                    MarketData marketData = response.body();
                    for (int i = 0; i < marketData.getPriceCoin().size(); i++) {
                        symbolList.add(marketData.getPriceCoin().get(i).getSymbol());
                        priceList.add(marketData.getPriceCoin().get(i).getPrice());
                        quantityList.add(marketData.getPriceCoin().get(i).getQuantity());
                    }
                    if (marketData.getPriceCoin() != null) {
                        Toast.makeText(MainActivity.this, String.valueOf(symbolList.size()), Toast.LENGTH_SHORT).show();

                        List<PriceCoin> priceCoinList = new ArrayList<>();
                        for (int i = 0; i < symbolList.size(); i++) {
                            priceCoinList.add(new PriceCoin(symbolList.get(i), priceList.get(i), R.drawable.img, quantityList.get(i)));
                        }

                        recyclerView = findViewById(R.id.recyclerView2);
                        MyAdapter myAdapter = new MyAdapter(priceCoinList);
                        myAdapter.setItemClickListener(new MyAdapter.OnMyItemClickListener() {
                            @Override
                            public void doSomeThing(int position) {
                                final Dialog dialog = new Dialog(MainActivity.this);
                                // Set layout cho Dialog
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.orderbook);
                                dialog.show();

                                Button newBtnClosePopup = dialog.findViewById(R.id.btClosePopup);
                                newBtnClosePopup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Đóng Dialog khi nhấn nút "Close"
                                        dialog.dismiss();
                                    }
                                });
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Your UI update code here
                                        List<String> price = new ArrayList<>();
                                        List<String> quantity = new ArrayList<>();
                                        List<String> priceTotal = new ArrayList<>();
                                        try {
                                            OkHttpClient okHttpClient = new OkHttpClient();

                                            // Tạo dữ liệu form
                                            RequestBody formBody = new FormBody.Builder()
                                                    .add("symbol", priceCoinList.get(position).getCoinName())
                                                    .build();

                                            // Tạo request POST
                                            Request request = new Request.Builder()
                                                    .url("http://192.168.18.100:5000/order_book")
                                                    .post(formBody)
                                                    .build();

                                            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                                    MainActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(MainActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        String jsonResponse = response.body().string();
                                                        Gson gson = new Gson();

                                                        // Chuyển đổi JSON thành đối tượng OrderBook
                                                        OrderBook orderBook = gson.fromJson(jsonResponse, OrderBook.class);
                                                        MainActivity.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                List<List<String>> asks = orderBook.getAsks();
                                                                List<List<String>> bids = orderBook.getBids();
                                                                for (int i = 0; i < asks.size(); i++) {
                                                                    price.add(asks.get(i).get(0));
                                                                    quantity.add(asks.get(i).get(1));
                                                                    priceTotal.add(String.valueOf(Double.valueOf(asks.get(i).get(1)) * Double.valueOf(asks.get(i).get(0))));
                                                                }
                                                                for (int i = 0; i < bids.size(); i++) {
                                                                    price.add(bids.get(i).get(0));
                                                                    quantity.add(bids.get(i).get(1));
                                                                    priceTotal.add(String.valueOf(Double.valueOf(bids.get(i).get(1)) * Double.valueOf(bids.get(i).get(0))));
                                                                }


                                                                TextView[] textViews = new TextView[14];
                                                                for (int i = 0; i < 10; i++) {
                                                                    int resId = getResources().getIdentifier("super" + (i + 1), "id", getPackageName());
                                                                    textViews[i] = dialog.findViewById(resId);
                                                                }

                                                                TextView[] textQuantity = new TextView[14];
                                                                for (int i = 0; i < 10; i++) {
                                                                    int resId = getResources().getIdentifier("superQuantity" + (i + 1), "id", getPackageName());
                                                                    textQuantity[i] = dialog.findViewById(resId);

                                                                }

                                                                TextView[] textTotal = new TextView[14];
                                                                for (int i = 0; i < 10; i++) {
                                                                    int resId = getResources().getIdentifier("superTotal" + (i + 1), "id", getPackageName());
                                                                    textTotal[i] = dialog.findViewById(resId);
                                                                }

                                                                for (int i = 0; i < 10 ; i++) {
                                                                    textViews[i].setText(price.get(i));
                                                                    textQuantity[i].setText(quantity.get(i));
                                                                    textTotal[i].setText(priceTotal.get(i));
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        MainActivity.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(MainActivity.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            });//HERE
                                        } catch (Exception e) {
                                            // Xử lý khi có lỗi xảy ra
                                            Toast.makeText(MainActivity.this, "Errorrr: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        // For example, updating the first TextView after 3 seconds
                                        handler.postDelayed(this, 1000);
                                    }
                                }, 1000);
                            }//dosomethingend
                        });
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                    }
                } else {
                    // Xử lý lỗi
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<MarketData> call, Throwable t) {
                // Xử lý khi yêu cầu thất bại
                Toast.makeText(MainActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    protected void onDestroy() {
        // Hủy bỏ lặp lại khi Activity bị hủy
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }


}