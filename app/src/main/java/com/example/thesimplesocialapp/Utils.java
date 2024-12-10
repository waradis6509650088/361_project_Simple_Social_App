package com.example.thesimplesocialapp;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface UploadImageCallback {
        void onUploadComplete(String response);

        void onUploadFailed(Exception e);
    }

    public static void uploadImage(String fPath, Context ctx, String servername, UploadImageCallback callback) {
//        executorService.execute(() -> {
//        });
        OkHttpClient client = new OkHttpClient();
        File file = new File(fPath);


        if (!file.exists() || !file.canRead()) {
            Log.e("FileAccess", "File not found or cannot be read: " + file.getAbsolutePath());
            return;
        }

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/jpeg"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(MainActivity.PROTOCOL + servername + "/upload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onUploadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject imgResponse = new JSONObject(responseBody);
                        String retUrl = imgResponse.getString("url");
                        callback.onUploadComplete(retUrl);
                    } catch (JSONException e) {
                        callback.onUploadFailed(e);
                    }
                } else {
                    callback.onUploadFailed(new IOException("Upload failed: " + response.code()));
                }
            }
        });

    }
    public static Drawable LoadImageFromUrl(String url, Context context) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);
            is.close();
            return new BitmapDrawable(Resources.getSystem(), image);
        } catch (Exception e) {
            return null;
        }
    }

}
