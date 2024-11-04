package com.example.thesimplesocialapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    ArrayList<Post> postList;
    RecycleViewAdapter(ArrayList<Post> post){
        this.postList = post;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView post_username;
        TextView post_undername;
        TextView post_text;
        ImageView profImg;
        ImageView postImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //post_username
            //post_undername
            //post_text
            this.post_username = itemView.findViewById(R.id.post_username);
            this.post_undername = itemView.findViewById(R.id.post_undername);
            this.post_text = itemView.findViewById(R.id.post_text);
            this.postImg = itemView.findViewById(R.id.post_image);
            this.profImg = itemView.findViewById(R.id.post_prof_img);
        }
        public TextView getPostUsername() {
            return post_username;
        }
        public TextView getPostUndername() {
            return post_undername;
        }
        public TextView getPostText() {
            return post_text;
        }
        public ImageView getPostImg() {
            return postImg;
        }
        public ImageView getProfImg() {
            return profImg;
        }
    }
    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        //post_username
        //post_undername
        //post_text
        String logtag = "recyview";
        Post pos = this.postList.get(position);
        holder.getPostUsername().setText(pos.getUsername());
        holder.getPostUndername().setText(pos.getDatePosted());
        holder.getPostText().setText(pos.getTextContent());
        ImageView postImg = holder.getPostImg();
        ImageView profImg = holder.getProfImg();
        postImg.setVisibility(View.INVISIBLE);
        Glide.with(holder.itemView.getContext()).load(pos.getPostImg()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                postImg.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                postImg.setVisibility(View.VISIBLE);
                return false;
            }
        }).into(postImg);
        Glide.with(holder.itemView.getContext()).load(pos.getProfImg()).into(profImg);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

