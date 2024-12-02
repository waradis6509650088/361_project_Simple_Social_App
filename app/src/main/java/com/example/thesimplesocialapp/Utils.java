package com.example.thesimplesocialapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils  {
    public static String uploadImage(Uri uri){
        return "https://picsum.photos/500/300";
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
