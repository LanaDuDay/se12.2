package com.example.recycleview;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class ImageUploadHelper {

    public interface UploadCallback {
        void onResponse(String result);

        void onFailure(IOException e);
    }

    public void uploadImage(File imageFile, UploadCallback callback) {
        String apiUrl = "http://192.168.18.100:5000/imagin";
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "image",
                        imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile)
                )
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        // Thực hiện yêu cầu bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                callback.onResponse(responseBody);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
        });
    }
}



