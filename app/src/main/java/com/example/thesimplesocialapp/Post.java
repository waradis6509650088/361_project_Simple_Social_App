package com.example.thesimplesocialapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Post {

    // data in a single post
    private String username;
    private String datePosted;
    private String textContent;
    private String profImg;
    private String postImg;

    // flags
    private boolean hasPostImg;

    Post(String jsonString){
        Random rand = new Random();
        try {
            JSONObject data = new JSONObject(jsonString);
            this.profImg = data.getString("prof-img");
            this.postImg = data.getString("post-img");
            this.username = data.getString("username");
            this.datePosted = data.getString("date-posted");
            this.textContent = data.getString("text-content");
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    // Getters
    public String getUsername() {
        return username;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public String getTextContent() {
        return textContent;
    }
    public String getPostImg() {
        return postImg;
    }

    public String getProfImg() {
        return profImg;
    }
    public boolean isHasPostImg() {
        return hasPostImg;
    }

    public void setHasPostImg(boolean hasPostImg) {
        this.hasPostImg = hasPostImg;
    }


}
