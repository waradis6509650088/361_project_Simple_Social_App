package com.example.thesimplesocialapp;

import static android.app.PendingIntent.getActivity;
import static androidx.compose.ui.text.SaversKt.save;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ArrayList<Post> postData;
    long backPressedTime; // Time of the last back press
    private String REQUEST_IP = "https://668e-124-122-136-37.ngrok-free.app";//"668e-124-122-136-37.ngrok-free.app";
    private void getData(){
        // get data from somewhere
        ArrayList<String> resPostJsonString = new ArrayList<String>();
        ArrayList<Post> data = new ArrayList<Post>();

        // --response format--
        // each post:
        // {
        //     username:xxx,
        //     prof-img:xxx,
        //     post-img:xxx,
        //     date-posted:unixtime,
        //     text-content:xxx
        // }
        // all post:
        // {
        //     data:{
        //         eachpost{},
        //         eachpost{},
        //         eachpost{],
        //         ...
        //     }
        // }

        StringRequest req = new StringRequest(Request.Method.GET, this.REQUEST_IP + "/posts",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray resArray = new JSONArray("["+response+"]");
                            JSONObject resJson = new JSONObject(resArray.getJSONObject(0).toString());
                            JSONArray data = new JSONArray(resJson.getJSONArray("data").toString());
                            for(int i = 0; i < data.length(); i++){
                                String postString = data.getJSONObject(i).toString();
                                resPostJsonString.add(postString);
                            }
                        }
                        catch (Error e){
                            Log.e("err", Objects.requireNonNull(e.getMessage()));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        //add example data to array
                        Log.i("exdat", resPostJsonString.toString());
                        for(String post : resPostJsonString){
                            data.add(new Post(post));
                        }

                        // visible post should be in postData array
                        postData.addAll(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err", Objects.requireNonNull(error.getMessage()));
                    }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);

    }

    private void openAccountMenu(){
        LinearLayout accBlockingLayout = findViewById(R.id.homepageBlocking_layout);
        RelativeLayout accMenu = findViewById(R.id.account_relative_layout);

        accBlockingLayout.setVisibility(View.VISIBLE);
        accBlockingLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        accBlockingLayout.setAlpha(.2f);
        accBlockingLayout.setClickable(true);
        accMenu.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left));
        accMenu.setVisibility(View.VISIBLE);
        accMenu.setClickable(true);
    }

    private void closeAccountMenu(){
        LinearLayout accBlockingLayout = findViewById(R.id.homepageBlocking_layout);
        RelativeLayout accMenu = findViewById(R.id.account_relative_layout);

        accBlockingLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
        accBlockingLayout.setAlpha(1f);
        accBlockingLayout.setVisibility(View.GONE);
        accBlockingLayout.setClickable(false);
        accMenu.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left));
        accMenu.setVisibility(View.GONE);
        accMenu.setClickable(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        // homepage elements
        RecyclerView recycleview = findViewById(R.id.recycle_view);
        SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_refresh);
        RelativeLayout mainLayout = findViewById(R.id.main_layout);
        LinearLayout menuBtn = findViewById(R.id.menu_btn);
        LinearLayout newPostBtn = findViewById(R.id.new_post_btn);
        LinearLayout accountBtn = findViewById(R.id.account_btn);

        // account menu elements
        ImageView accBackBtn = findViewById(R.id.account_back_btn);
        LinearLayout accBlockingLayout = findViewById(R.id.homepageBlocking_layout);
        RelativeLayout accMenu = findViewById(R.id.account_relative_layout);


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

        // back button pressed confirmation
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Toast backToast; // Toast message
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                } else {
                    // Show the toast message
                    backToast = Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                    backToast.show();
                }

                // Save the current time of back press
                backPressedTime = System.currentTimeMillis();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        accBlockingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAccountMenu();
            }
        });

        accBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAccountMenu();
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountMenu();
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
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