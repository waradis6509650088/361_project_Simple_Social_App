package com.example.thesimplesocialapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Post> postData;
    private void getData(){
        // get data from somewhere idk
        ArrayList<String> exampleData = new ArrayList<String>();
        ArrayList<Post> data = new ArrayList<Post>();

        //add example data to array
        exampleData.add("{\"username\":\"user123\",\"date-posted\":\"2024-11-01T10:30:00Z\",\"text-content\":\"Hello, world! This is my first post.\"}");
        exampleData.add("{\"username\":\"jane_doe\",\"date-posted\":\"2024-11-02T14:15:00Z\",\"text-content\":\"Loving the new features in this app!\"}");
        exampleData.add("{\"username\":\"mark_smith\",\"date-posted\":\"2024-11-03T09:45:00Z\",\"text-content\":\"Can't wait for the weekend! Any plans?\"}");
        exampleData.add("{\"username\":\"alice_wonder\",\"date-posted\":\"2024-11-04T08:00:00Z\",\"text-content\":\"Just finished a great book! Highly recommend it.\"}");
        exampleData.add("{\"username\":\"bob_builder\",\"date-posted\":\"2024-11-05T12:30:00Z\",\"text-content\":\"Working on a new project. Excited to share updates!\"}");
        exampleData.add("{\"username\":\"carol_conner\",\"date-posted\":\"2024-11-06T11:15:00Z\",\"text-content\":\"Had a fantastic day out with friends!\"}");
        exampleData.add("{\"username\":\"dave_doe\",\"date-posted\":\"2024-11-07T17:00:00Z\",\"text-content\":\"Trying out a new recipe tonight. Wish me luck!\"}");
        exampleData.add("{\"username\":\"eve_online\",\"date-posted\":\"2024-11-08T15:45:00Z\",\"text-content\":\"Just got back from vacation. Miss it already!\"}");
        exampleData.add("{\"username\":\"frank_the_tank\",\"date-posted\":\"2024-11-09T09:00:00Z\",\"text-content\":\"Caught up on some much-needed sleep this weekend.\"}");
        exampleData.add("{\"username\":\"grace_hopper\",\"date-posted\":\"2024-11-10T14:20:00Z\",\"text-content\":\"Looking forward to the upcoming tech conference!\"}");
        exampleData.add("{\"username\":\"carol_conner\",\"date-posted\":\"2024-11-06T11:15:00Z\",\"text-content\":\"Had a fantastic day out with friends!\"}");
        exampleData.add("{\"username\":\"dave_doe\",\"date-posted\":\"2024-11-07T17:00:00Z\",\"text-content\":\"Trying out a new recipe tonight. Wish me luck!\"}");
        exampleData.add("{\"username\":\"eve_online\",\"date-posted\":\"2024-11-08T15:45:00Z\",\"text-content\":\"Just got back from vacation. Miss it already!\"}");
        exampleData.add("{\"username\":\"frank_the_tank\",\"date-posted\":\"2024-11-09T09:00:00Z\",\"text-content\":\"Caught up on some much-needed sleep this weekend.\"}");
        exampleData.add("{\"username\":\"grace_hopper\",\"date-posted\":\"2024-11-10T14:20:00Z\",\"text-content\":\"Looking forward to the upcoming tech conference!\"}");

        for(String post : exampleData){
            data.add(new Post(post));
        }
        postData.addAll(data);

    }

    protected void changeToMenuPage() {
        Intent page = new Intent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);

        // get relevant elements
        setContentView(R.layout.activity_main);
        RecyclerView recycleview = findViewById(R.id.recycle_view);
        SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_refresh);
        RelativeLayout mainLayout = findViewById(R.id.main_layout);
        LinearLayout menuBtn = findViewById(R.id.menu_btn);
        LinearLayout newPostBtn = findViewById(R.id.new_post_btn);

        // main data to display to homepage
        postData = new ArrayList<Post>();

        // attaching data to card
        getData();
        RecycleViewAdapter adapter = new RecycleViewAdapter(postData);

        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.menu_page);
            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                //==============================================================================
                // if change without this the, app will throw a null pointer exception

                // create empty tmp array and tmp adapter
                ArrayList<Post> tmp = new ArrayList<Post>();
                RecycleViewAdapter tmpAdapter = new RecycleViewAdapter(tmp);
                recycleview.setAdapter(tmpAdapter);

                // clear postData and set the adapter back
                postData.clear();
                getData();
                adapter.notifyDataSetChanged();
                recycleview.setAdapter(adapter);
                //==============================================================================

                Log.i("current data",postData.toString());
                swipeLayout.setRefreshing(false);
            }
        });
    }
}