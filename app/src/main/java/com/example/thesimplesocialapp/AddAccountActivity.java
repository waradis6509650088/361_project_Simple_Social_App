package com.example.thesimplesocialapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddAccountActivity extends AppCompatActivity implements IRegisImagePath{
    private String regis_image_path;
    ActivityResultLauncher<Intent> imageChooserLauncher;
    // ======================= DELETE ===========================
    public void setRegis_image_path(String regis_image_path) {
        this.regis_image_path = regis_image_path;
    }


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
        UploadImageDialogFragment upload_regis_image_dialog = new UploadImageDialogFragment();

        // ======================= DELETE ===========================
        imageChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        String filePath;
                        Log.i("h", "hello world");
//                        mCallback.returnDataFromUploadImage("hello world");
                        if (o.getResultCode() == Activity.RESULT_OK) {
//                        Uri data = o.getData().getData();
//                        filePath = getPath(data);
//                        Log.i("image path", filePath);
                        }
                    }
                });

        // init db
        try{
            db_LocalCred database = new db_LocalCred(AddAccountActivity.this);
            SQLiteDatabase db = database.getReadableDatabase();
        }
        catch (Exception e){
            Log.e("db error", e.getMessage() );
        }

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ActivityResultLauncher<Intent> dialog = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult o) {

                            }
                        }
                );
                upload_regis_image_dialog.show(getSupportFragmentManager(),"getImageUpload");

                // ======================= DELETE ===========================
                imageChooserLauncher.launch(galleryIntent);
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

    @Override
    public void returnDataFromUploadImage(String str) {
        TextView imgUrl = findViewById(R.id.regis_image_fpath);
        Log.i("img path", str);

    }
}