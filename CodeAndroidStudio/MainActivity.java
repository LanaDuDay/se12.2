package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView textView, textView1;
    Button button, button1;
    BotApi botApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView2);
        button = findViewById(R.id.button2);
        button1 =findViewById(R.id.button6);
        botApi = RetrofitClient.getBotApi();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<AccountInfo> callGetAccountInfo = botApi.getAccountInfo();
                callGetAccountInfo.enqueue(new Callback<AccountInfo>() {
                    @Override
                    public void onResponse(Call<AccountInfo> call, Response<AccountInfo> response) {
                        if (response.isSuccessful()) {
                            // Xử lý khi nhận được thông tin tài khoản
                            AccountInfo accountInfo = response.body();
                            if (accountInfo.getBalances() != null) {
                                Toast.makeText(MainActivity.this, "not null", Toast.LENGTH_SHORT).show();
                                textView.setText(accountInfo.getBalances().get(0).getAsset());
                            } else {
                                Toast.makeText(MainActivity.this, "Tên người dùng không khả dụng", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // Xử lý khi có lỗi từ bot
                            Toast.makeText(MainActivity.this, " thông tin tài khoản", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountInfo> call, Throwable t) {
                        // Xử lý khi gọi API thất bại
                        Toast.makeText(MainActivity.this, "Lỗi khi  yêu cầu", Toast.LENGTH_SHORT).show();
                        textView1.setText(t.getMessage());
                    }
                });
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "HELLO", Toast.LENGTH_SHORT).show();
            }
        });
    }
}