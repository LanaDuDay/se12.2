package com.example.recycleview;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import java.text.DecimalFormat;



import android.view.ViewPropertyAnimator;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity3 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Handler autoTradeHandler;
    private Handler accountHandler;
    ApiService apiService;
    TextView textView;
    private static final String BASE_URL = "http://192.168.18.104:5000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setupButtonClickListeners();
        Button btnOpenPopup = findViewById(R.id.button);
        Button btnBuyMarket = findViewById(R.id.button3);
        Button btnSellLimit = findViewById(R.id.button2);
        Button btnSellMarket = findViewById(R.id.button4);
        Button btnQueue = findViewById(R.id.button8);
        Button btnAutoTrade = findViewById(R.id.button7);

        apiService = RetrofitClient.getApiService();
        List<Double> priceList = new ArrayList<>();
        List<Double> quantityList = new ArrayList<>();

        accountHandler = new Handler();
        accountHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Thực hiện công việc ở đây sau khoảng thời gian đã đặt
                // Nếu muốn cập nhật giao diện người dùng, sử dụng runOnUiThread hoặc post trực tiếp vào Handler
                retrofit2.Call<MarketData> call = apiService.getMarketData();
                call.enqueue(new retrofit2.Callback<MarketData>() {
                    @Override
                    public void onResponse(retrofit2.Call<MarketData> call, retrofit2.Response<MarketData> response) {
                        if (response.isSuccessful()) {
                            MarketData marketData = response.body();
                            double account = 1;
                            for (int i = 0; i < marketData.getPriceCoin().size(); i++) {
                                account += marketData.getPriceCoin().get(i).getPrice() * marketData.getPriceCoin().get(i).getQuantity();
                            }
                            DecimalFormat decimalFormat = new DecimalFormat("#.##");
                            String formattedNumber = decimalFormat.format(account);
                            textView = findViewById(R.id.largeTextView);
                            textView.setText("$"+String.valueOf(formattedNumber));
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MarketData> call, Throwable t) {
                        Toast.makeText(MainActivity3.this,"Error Account", Toast.LENGTH_SHORT).show();
                    }
                });
                accountHandler.postDelayed(this, 2000);
            }
        }, 2000);


        btnAutoTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity3.this,"Đang tự động trade theo giờ", Toast.LENGTH_SHORT).show();
                autoTradeHandler = new Handler();
                autoTradeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient okHttpClient = new OkHttpClient();

                            // Tạo dữ liệu form
                            RequestBody formBody = new FormBody.Builder()

                                    .build();

                            // Tạo request POST
                            Request request = new Request.Builder()
                                    .url( BASE_URL + "start_auto_trade")
                                    .post(formBody)
                                    .build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    MainActivity3.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity3.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        final String responseData = response.body().string();

                                        try {
                                            JSONObject jsonObject = new JSONObject(responseData);
                                            final String status = jsonObject.getString("status");
                                            final String message = jsonObject.getString("message");

                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if ("success".equals(status)) {
                                                        Toast.makeText(MainActivity3.this, message, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Lỗi phân tích JSON", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        MainActivity3.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        autoTradeHandler.postDelayed(this, 3600000); // 1 giờ = 3,600,000 milliseconds
                    }
                }, 3600000);

            }
        });
        btnOpenPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Dialog
                final Dialog dialog = new Dialog(MainActivity3.this);

                // Set layout cho Dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup_layout); //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

                // Hiển thị Dialog
                dialog.show();

                // Xử lý sự kiện khi nhấn nút "Close"
                Button btnClosePopup = dialog.findViewById(R.id.btnClosePopup);
                btnClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đóng Dialog khi nhấn nút "Close"
                        dialog.dismiss();
                    }
                });

                // Xử lý sự kiện khi nhấn nút "Trade"
                Button btnTrade = dialog.findViewById(R.id.btnTrade);
                btnTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Kiểm tra xem cả hai EditText đã được điền đầy đủ hay không
                        EditText edtQuantity = dialog.findViewById(R.id.edtQuantity);
                        EditText edtTransactionType = dialog.findViewById(R.id.edtTransactionType);
                        EditText editPrice = dialog.findViewById(R.id.edtPrice);
                        String quantity = edtQuantity.getText().toString();
                        String symbol = edtTransactionType.getText().toString();
                        String price = editPrice.getText().toString();

                        if (!quantity.isEmpty() && !symbol.isEmpty()) {
                            // Cả hai EditText đã được điền đầy đủ
                            // Gửi dữ liệu lên server Flask
                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();

                                // Tạo dữ liệu form
                                RequestBody formBody = new FormBody.Builder()
                                        .add("symbol", symbol)
                                        .add("quantity", quantity)
                                        .add("price", price)
                                        .build();

                                // Tạo request POST
                                Request request = new Request.Builder()
                                        .url(BASE_URL + "place_order")
                                        .post(formBody)
                                        .build();
                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        MainActivity3.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity3.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Phản hồi", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                // Xử lý khi có lỗi xảy ra
                                Toast.makeText(MainActivity3.this, "Error: " , Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            // Có ít nhất một EditText chưa được điền đầy đủ
                            Toast.makeText(MainActivity3.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnSellLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity3.this);

                // Set layout cho Dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.selllimit);
                dialog.show();
                Button newBtnClosePopup = dialog.findViewById(R.id.newBtnClosePopup);
                newBtnClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đóng Dialog khi nhấn nút "Close"
                        dialog.dismiss();
                    }
                });
                Button newBtnTrade = dialog.findViewById(R.id.newBtnTrade);
                newBtnTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edtQuantity = dialog.findViewById(R.id.newEdtQuantity);
                        EditText edtTransactionType = dialog.findViewById(R.id.newEdtTransactionType);
                        EditText editPrice = dialog.findViewById(R.id.newEdtPrice);
                        String quantity = edtQuantity.getText().toString();
                        String symbol = edtTransactionType.getText().toString();
                        String price = editPrice.getText().toString();
                        if (!quantity.isEmpty() && !symbol.isEmpty()) {
                            // Cả hai EditText đã được điền đầy đủ
                            // Gửi dữ liệu lên server Flask
                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();

                                // Tạo dữ liệu form
                                RequestBody formBody = new FormBody.Builder()
                                        .add("symbol", symbol)
                                        .add("quantity", quantity)
                                        .add("price", price)
                                        .build();

                                // Tạo request POST
                                Request request = new Request.Builder()
                                        .url(BASE_URL + "send_limit")
                                        .post(formBody)
                                        .build();
                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        MainActivity3.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity3.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Phản hồi", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                // Xử lý khi có lỗi xảy ra
                                Toast.makeText(MainActivity3.this, "Error: " , Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            // Có ít nhất một EditText chưa được điền đầy đủ
                            Toast.makeText(MainActivity3.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnBuyMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity3.this);

                // Set layout cho Dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.buymarket);
                dialog.show();

                Button newwBtnClosePopup = dialog.findViewById(R.id.newwBtnClosePopup);
                newwBtnClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đóng Dialog khi nhấn nút "Close"
                        dialog.dismiss();
                    }
                });

                Button newwBtnTrade = dialog.findViewById(R.id.newwBtnTrade);
                newwBtnTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edtQuantity = dialog.findViewById(R.id.newwEdtQuantity);
                        EditText edtTransactionType = dialog.findViewById(R.id.newwEdtTransactionType);
                        String quantity = edtQuantity.getText().toString();
                        String symbol = edtTransactionType.getText().toString();
                        if (!quantity.isEmpty() && !symbol.isEmpty()) {
                            // Cả hai EditText đã được điền đầy đủ
                            // Gửi dữ liệu lên server Flask
                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();

                                // Tạo dữ liệu form
                                RequestBody formBody = new FormBody.Builder()
                                        .add("symbol", symbol)
                                        .add("quantity", quantity)
                                        .build();

                                // Tạo request POST
                                Request request = new Request.Builder()
                                        .url(BASE_URL + "buy_market")
                                        .post(formBody)
                                        .build();
                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        MainActivity3.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity3.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Phản hồi", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                // Xử lý khi có lỗi xảy ra
                                Toast.makeText(MainActivity3.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Có ít nhất một EditText chưa được điền đầy đủ
                            Toast.makeText(MainActivity3.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        /////
        btnSellMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity3.this);

                // Set layout cho Dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.sellmarket);
                dialog.show();

                Button newBtnClosePopup = dialog.findViewById(R.id.newwwBtnClosePopup);
                newBtnClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đóng Dialog khi nhấn nút "Close"
                        dialog.dismiss();
                    }
                });

                Button newBtnTrade = dialog.findViewById(R.id.newwwBtnTrade);
                newBtnTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edtQuantity = dialog.findViewById(R.id.newwwEdtQuantity);
                        EditText edtTransactionType = dialog.findViewById(R.id.newwwEdtTransactionType);
                        String quantity = edtQuantity.getText().toString();
                        String symbol = edtTransactionType.getText().toString();

                        if (!quantity.isEmpty() && !symbol.isEmpty()) {
                            // Cả hai EditText đã được điền đầy đủ
                            // Gửi dữ liệu lên server Flask
                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();

                                // Tạo dữ liệu form
                                RequestBody formBody = new FormBody.Builder()
                                        .add("symbol", symbol)
                                        .add("quantity", quantity)
                                        .build();

                                // Tạo request POST
                                Request request = new Request.Builder()
                                        .url(BASE_URL + "send_market")
                                        .post(formBody)
                                        .build();
                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        MainActivity3.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity3.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Phản hồi", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                // Xử lý khi có lỗi xảy ra
                                Toast.makeText(MainActivity3.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Có ít nhất một EditText chưa được điền đầy đủ
                            Toast.makeText(MainActivity3.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity3.this);

                // Set layout cho Dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_queue);
                dialog.show();
                Button SupernewBtnClosePopup = dialog.findViewById(R.id.superbtnClosePopup);
                SupernewBtnClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đóng Dialog khi nhấn nút "Close"
                        dialog.dismiss();
                    }
                });
                try {
                    List<QueueItem> queueList = new ArrayList<>();
                    OkHttpClient okHttpClient = new OkHttpClient();

                    // Tạo dữ liệu form
                    RequestBody formBody = new FormBody.Builder()
                            .build();

                    // Tạo request POST
                    Request request = new Request.Builder()
                            .url("http://192.168.18.104:5000/open_orders_json")
                            .post(formBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            MainActivity3.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity3.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    // Lấy nội dung response dưới dạng String
                                    String responseData = response.body().string();

                                    // Chuyển đổi String thành đối tượng JSONObject
                                    JSONObject jsonObject = new JSONObject(responseData);

                                    // Lấy danh sách các đối tượng BTCUSDT
                                    JSONArray btcusdtArray = jsonObject.getJSONArray("BTCUSDT");

                                    // Tạo các danh sách để lưu giá trị
                                    List<String> symbols = new ArrayList<>();
                                    List<String> quantities = new ArrayList<>();
                                    List<String> prices = new ArrayList<>();
                                    List<Long> times = new ArrayList<>();

                                    // Duyệt qua mỗi đối tượng BTCUSDT
                                    for (int i = 0; i < btcusdtArray.length(); i++) {
                                        JSONObject btcusdtObject = btcusdtArray.getJSONObject(i);

                                        // Lấy giá trị từ đối tượng BTCUSDT
                                        String symbol = btcusdtObject.getString("symbol");
                                        String quantity = btcusdtObject.getString("origQty");
                                        String price = btcusdtObject.getString("price");
                                        long time = btcusdtObject.getLong("time");

                                        // Thêm giá trị vào danh sách
                                        symbols.add(symbol);
                                        quantities.add(quantity);
                                        prices.add(price);
                                        times.add(time);
                                        queueList.add(new QueueItem(symbol,Double.valueOf(price),Double.valueOf(quantity),String.valueOf(time)));

                                    }
                                    if(queueList.size() > 0) {
                                        // Ở đây bạn có thể sử dụng symbols, quantities, prices, times theo nhu cầu của bạn.
                                        if (!isFinishing()) {
                                            // Hiển thị Dialog ở đây
                                            recyclerView = dialog.findViewById(R.id.buyLimitQueueRecyclerView);
                                            QueueAdapter queueAdapter = new QueueAdapter(queueList);
                                            MainActivity3.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Cập nhật UI ở đây
                                                    recyclerView.setAdapter(queueAdapter);
                                                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity3.this, 1));
                                                }
                                            });
                                        }
                                    }
                                } catch (JSONException e) {

                                }
                            } else {
                                MainActivity3.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity3.this, "Không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    // Xử lý khi có lỗi xảy ra
                    Toast.makeText(MainActivity3.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                                Intent intent = new Intent(MainActivity3.this, targetActivity);

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
}
