package com.example.thesimplesocialapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class NewPostActivity extends AppCompatActivity {
    Uri imgUri;

    private String getPath(Uri uri){
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_post_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgUri = null;
        ActivityResultLauncher<Intent> imageChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            try {
                                Uri uri = o.getData().getData();
                                String imgPath = getPath(uri);
                                ImageView imgV = findViewById(R.id.new_post_attached_image);
                                imgV.setImageURI(uri);
                                imgUri = uri;
                            } catch (Exception e) {
                                Log.e("regImgUpload", "Error when getting image path: " + e.getMessage());
                            }
                        }
                    }
                });

        // back button pressed confirmation
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        ImageView back_btn = findViewById(R.id.new_post_back_btn);
        Button postBtn = findViewById(R.id.new_post_post_btn);
        EditText text = findViewById(R.id.new_post_text);
        LinearLayout img = findViewById(R.id.new_post_add_image);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageChooserLauncher.launch(galleryIntent);
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_post();
            }
        });
    }

    private void create_post() {
        JSONObject postobj = new JSONObject();
        AppDatabase db = new AppDatabase(getApplicationContext());
        try{
            EditText text = findViewById(R.id.new_post_text);
            postobj.put("username", db.getCurrentUsername());
            postobj.put("text_content", text.getText().toString());
            postobj.put("token", db.getCurrentToken());
            if(imgUri != null){
                Utils.uploadImage(getPath(imgUri), getApplicationContext(), MainActivity.CURRENT_DOMAIN, new Utils.UploadImageCallback() {
                    @Override
                    public void onUploadComplete(String response) {
                        try{
                            postobj.put("post_img", response);

                            try{
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST
                                        , "https://" + db.getCurrentServername() + "/create-post"
                                        , postobj
                                        , new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast success = Toast.makeText(getApplicationContext(), "post success", Toast.LENGTH_SHORT);
                                        success.show();
                                        finish();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("post network error", String.valueOf(error.networkResponse.statusCode));
                                        Toast errorToast = Toast.makeText(getApplicationContext(), "unknow error, failed to post" + error.toString(), Toast.LENGTH_SHORT);
                                        errorToast.show();
                                        finish();
                                    }
                                });
                                requestQueue.add(req);

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            Log.i("saved obj", postobj.toString());
                        }
                        catch (Exception e){
                        }
                    }

                    @Override
                    public void onUploadFailed(Exception e) {
                        Log.e("upload image error", "onUploadFailed: " + e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}