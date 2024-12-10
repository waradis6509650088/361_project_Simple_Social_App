package com.example.thesimplesocialapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DBListViewAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<JSONObject> data;
    private final LayoutInflater inflater;
    public DBListViewAdapter(Context ctx){
        this.context = ctx;
        this.inflater = LayoutInflater.from(this.context);
        try{
            AppDatabase db = new AppDatabase(this.context);
            this.data = db.getAllData(ILocalCred.TABLE_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void populateList() {
        try{
            AppDatabase db = new AppDatabase(this.context);
            this.data = db.getAllData(ILocalCred.TABLE_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.each_accountselect, parent, false);
        }

        ImageView pic = convertView.findViewById(R.id.acc_select_img);
        TextView server = convertView.findViewById(R.id.acc_select_servername);
        TextView name = convertView.findViewById(R.id.acc_select_name);

        JSONObject jsonObject = data.get(position);
        try {
            Glide.with(this.context).load(MainActivity.PROTOCOL + MainActivity.CURRENT_DOMAIN + "/res/" + jsonObject.getString("profimgurl")).into(pic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            name.setText(jsonObject.getString("username"));
            server.setText(jsonObject.getString("servername"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
