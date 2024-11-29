package com.example.thesimplesocialapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.ReturnThis;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentResultListener;

import java.io.File;
import java.net.URI;

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
        UploadImageDialogFragment upload_regis_image_dialogFragment = new UploadImageDialogFragment();
        ImageView regis_img = findViewById(R.id.regis_image);

        // init db
        try{
            db_LocalCred database = new db_LocalCred(AddAccountActivity.this);
            SQLiteDatabase db = database.getReadableDatabase();
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
                                Uri data = o.getData().getData();
                                String path = data.getPath();
                                File img = new File(path);
                                Bitmap imgBit = MediaStore.Images.Media.getBitmap(getContentResolver(), data);
                                regis_img.setImageBitmap(imgBit);
                            } catch (Exception e) {
                                Log.e("regImgUpload", "Error when getting image path: " + e.getMessage());
                            }
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

    private Cursor getEvents(SQLiteDatabase db){
        String SELECT = ILocalCred.TABLE_NAME;
        String[] FROM = {"username", "servername", "token"};
        Cursor cursor = db.query(SELECT,
                FROM,
                null,
                null,
                null,
                null,
                "_ID"
        );
        return cursor;

    }

}