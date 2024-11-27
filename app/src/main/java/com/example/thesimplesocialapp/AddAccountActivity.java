package com.example.thesimplesocialapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddAccountActivity extends AppCompatActivity implements IRegisImagePath{
    private String regis_image_path;
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
                upload_regis_image_dialog.show(getSupportFragmentManager(),"getImageUpload");
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