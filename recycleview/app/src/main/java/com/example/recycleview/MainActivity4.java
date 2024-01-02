package com.example.recycleview;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import okhttp3.Call;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        OkHttpClient okHttpClient = new OkHttpClient();

        // Tạo dữ liệu form
        RequestBody formBody = new FormBody.Builder()
                .add("symbol", "BTCUSDT")
                .build();

        // Tạo request POST
        Request request = new Request.Builder()
                .url("http://192.168.18.100:5000/get_hourly_data")
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                MainActivity4.this.runOnUiThread(() -> {
                    Toast.makeText(MainActivity4.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    saveDataToFile(responseData);
                } else {
                    MainActivity4.this.runOnUiThread(() -> {
                        Toast.makeText(MainActivity4.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void saveDataToFile(String data) {
        try {
            // Tạo đối tượng File và FileWriter
            File file = new File(getFilesDir(), "bieudo.txt");
            FileWriter writer = new FileWriter(file);

            // Ghi dữ liệu vào tệp tin
            writer.write(data);

            // Đóng FileWriter để hoàn tất quá trình ghi
            writer.close();

            MainActivity4.this.runOnUiThread(() -> {
                Toast.makeText(MainActivity4.this, "Data saved to bieudo.txt", Toast.LENGTH_LONG).show();
            });

            String filePath = file.getAbsolutePath();

            MainActivity4.this.runOnUiThread(() -> {
                // Hiển thị địa chỉ sử dụng Toast
                Toast.makeText(MainActivity4.this, "Data saved to: " + filePath, Toast.LENGTH_LONG).show();
            });

        } catch (IOException e) {
            MainActivity4.this.runOnUiThread(() -> {
                Toast.makeText(MainActivity4.this, "Error saving data to file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
            e.printStackTrace();
        }
    }
}
