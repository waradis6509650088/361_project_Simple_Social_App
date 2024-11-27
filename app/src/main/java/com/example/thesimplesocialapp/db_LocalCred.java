package com.example.thesimplesocialapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class db_LocalCred extends SQLiteOpenHelper {
    private final String CREATE_TABLE;
    public db_LocalCred(Context ctx){
        super(ctx, "app.db", null, 1);
        this.CREATE_TABLE =
            "CREATE TABLE " + LocalCred.TABLE_NAME + "("
            + LocalCred._ID + " INTEGER PRIMARY KEY, "
            + LocalCred.COLUMN_SERVERNAME + " TEXT,"
            + LocalCred.COLUMN_PROFIMGURL + " TEXT,"
            + LocalCred.COLUMN_USERNAME + " TEXT,"
            + LocalCred.COLUMN_TOKEN + " TEXT)";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LocalCred.TABLE_NAME");
        onCreate(db);

    }

    public void addNewAccount(String img, String user, String server, String token){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("username",user);
        value.put("servername",server);
        value.put("usertoken",token);
        value.put("profimgurl",img);
        db.insert(LocalCred.TABLE_NAME,null, value);
        db.close();

    }
}
