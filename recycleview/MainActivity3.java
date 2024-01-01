package com.example.recycleview;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        ImageView imageViewButton1 = findViewById(R.id.imageViewButton1);
        imageViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Định nghĩa Intent cho Activity 1
                Intent intent = new Intent(MainActivity3.this, MainActivity3.class);

                // Chạy Intent
                startActivity(intent);
            }
        });

        // ImageView cho Button 2
        ImageView imageViewButton2 = findViewById(R.id.imageViewButton2);
        imageViewButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Định nghĩa Intent cho Activity 2
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);

                // Chạy Intent
                startActivity(intent);
            }
        });

        Button btnOpenPopup = findViewById(R.id.button);
        Button btnBuyMarket = findViewById(R.id.button3);
        Button btnSellLimit = findViewById(R.id.button2);
        Button btnSellMarket = findViewById(R.id.button4);
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
                                        .url("http://192.168.18.100:5000/place_order")
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
                                        .url("http://192.168.18.100:5000/send_limit")
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
                                        .url("http://192.168.18.100:5000/buy_market")
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
                                        .url("http://192.168.18.100:5000/send_market")
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

    }


}
