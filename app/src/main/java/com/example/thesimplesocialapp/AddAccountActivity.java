package com.example.thesimplesocialapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentResultListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class AddAccountActivity extends AppCompatActivity{
    private ActivityResultLauncher<Intent> imageChooserLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_account_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView addAccount_back_btn = findViewById(R.id.add_account_back_btn);
        Button regis_btn = findViewById(R.id.regis_btn);
        Button login_btn = findViewById(R.id.login_btn);
        UploadImageDialogFragment upload_regis_image_dialogFragment = new UploadImageDialogFragment();
        EditText add_acc_username = findViewById(R.id.login_user);
        EditText add_acc_password = findViewById(R.id.login_pass);
        EditText add_acc_servername = findViewById(R.id.login_serv);

        // init db
        try{
            AppDatabase appDatabase = new AppDatabase(AddAccountActivity.this);
            SQLiteDatabase db = appDatabase.getReadableDatabase();
        }
        catch (Exception e){
            Log.e("db error", e.getMessage() );
        }

        // fragment result listener from dialog fragment
        // use when choosing picture when registering
        getSupportFragmentManager().setFragmentResultListener("regImgChoice", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(result.getInt("resCode") > 0){
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imageChooserLauncher.launch(galleryIntent);
                }
            }
        });

        // init image picker for when choosing pic when registering
        imageChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            try{
                                Uri uri = o.getData().getData();
                                String imgPath = getPath(uri);
                                String view_username = add_acc_username.getText().toString();
                                String view_password = add_acc_password.getText().toString();
                                String view_servername = add_acc_servername.getText().toString();
                                String uploadedImageUrl = Utils.uploadImage(uri);
                                registerNewAccount(view_servername, view_username, view_password, uploadedImageUrl);
                            } catch (Exception e) {
                                Log.e("regImgUpload", "Error when getting image path: " + e.getMessage());
                            }
                        }
                    }
                });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String view_username = add_acc_username.getText().toString();
                    String view_password = add_acc_password.getText().toString();
                    String view_servername = add_acc_servername.getText().toString();
                    login(view_servername, view_username, view_password);
                    finish();
                } catch (Exception e) {
                    Log.e("regImgUpload", "Error when login: " + e);
                    Toast fail = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    fail.show();
                }
            }
        });

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_regis_image_dialogFragment.show(getSupportFragmentManager(),"getImageUpload");
            }
        });


        addAccount_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void login(String domain, String usr, String pwd) {
        try{
            JSONObject loginRequest = new JSONObject();
            loginRequest.put("username",usr);
            loginRequest.put("password",pwd);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://" + domain + "/login", loginRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        Log.i("login response", response.toString());
                        AppDatabase dbCon = new AppDatabase(getApplicationContext());
                        JSONObject loginCred = response;
                        dbCon.updateCurrentCred(domain
                                , loginCred.getJSONObject("user").getString("prof_img")
                                , loginCred.getJSONObject("user").getString("username")
                                , loginCred.getString("token")
                        );
                        dbCon.addNewLocalCredential(dbCon.getCurrentProfileImageUrl(), dbCon.getCurrentUsername(), dbCon.getCurrentServername(), dbCon.getCurrentToken());
                        Toast success = Toast.makeText(getApplicationContext(), "Login successfully!", Toast.LENGTH_SHORT);
                        success.show();
                    }
                    catch (Exception e){
                        Log.e("login error", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("login network error", String.valueOf((error.networkResponse == null)? 0 : error.networkResponse.statusCode));
                    Toast errorToast = Toast.makeText(getApplicationContext(), "unknow error, failed to login" + error.toString(), Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            });
            requestQueue.add(req);
        }
        catch (Exception e){
            Log.e("login error", e.toString());
        }
    }
    private void registerNewAccount(String domain, String usr, String pwd, String profUrl){
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject reqData = new JSONObject();
            reqData.put("username", usr);
            reqData.put("prof_img", profUrl);
            reqData.put("password", pwd);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://" + domain + "/register-user", reqData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        AppDatabase dbCon = new AppDatabase(getApplicationContext());
                        dbCon.updateCurrentCred(domain, profUrl, usr, response.getString("token"));
                        dbCon.addNewLocalCredential(dbCon.getCurrentProfileImageUrl(), dbCon.getCurrentUsername(), dbCon.getCurrentServername(), dbCon.getCurrentToken());
                        Toast success = Toast.makeText(getApplicationContext(), "Account registered successfully!", Toast.LENGTH_SHORT);
                        success.show();
                        Log.i("response", response.toString());
                        finish();
                    }
                    catch (Exception e){
                        Log.e("create new user error", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int netRes = (error.networkResponse == null)? 0 : error.networkResponse.statusCode;
                    Log.e("create new user network error", String.valueOf(netRes));
                    if(netRes == 409){
                        Toast errorToast = Toast.makeText(getApplicationContext(), "Account already exists on server", Toast.LENGTH_SHORT);
                        errorToast.show();
                    }
                    else{
                        Toast errorToast = Toast.makeText(getApplicationContext(), "unknow error" + error.toString(), Toast.LENGTH_SHORT);
                        errorToast.show();
                    }
                }
            });
            requestQueue.add(req);
        } catch (Exception e) {
            Log.e("get token error", e.getMessage());
        }
    }
    private String getPath(Uri uri){
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
}