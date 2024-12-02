package com.example.thesimplesocialapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class AppDatabase extends SQLiteOpenHelper {
    private final String CREATE_LOCALCRED_TABLE;
    private final String CREATE_CURRENTCRED_TABLE;
    public AppDatabase(Context ctx){
        super(ctx, "app.db", null, 1);
        this.CREATE_CURRENTCRED_TABLE =
                "CREATE TABLE IF NOT EXISTS " + ICurrentCred.TABLE_NAME + "("
                        + ICurrentCred._ID + " INTEGER PRIMARY KEY CHECK(_ID = 1), "
                        + ICurrentCred.COLUMN_SERVERNAME + " TEXT,"
                        + ICurrentCred.COLUMN_PROFIMGURL + " TEXT,"
                        + ICurrentCred.COLUMN_USERNAME + " TEXT,"
                        + ICurrentCred.COLUMN_TOKEN + " TEXT)";
        this.CREATE_LOCALCRED_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ILocalCred.TABLE_NAME + "("
            + ILocalCred._ID + " INTEGER PRIMARY KEY, "
            + ILocalCred.COLUMN_SERVERNAME + " TEXT,"
            + ILocalCred.COLUMN_PROFIMGURL + " TEXT,"
            + ILocalCred.COLUMN_USERNAME + " TEXT,"
            + ILocalCred.COLUMN_TOKEN + " TEXT)";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCALCRED_TABLE);
        db.execSQL(CREATE_CURRENTCRED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ILocalCred.TABLE_NAME");
        db.execSQL("DROP TABLE IF EXISTS ICurrentCred.TABLE_NAME");
        onCreate(db);

    }

    public void addNewLocalCredential(String img, String user, String server, String token){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("username",user);
        value.put("servername",server);
        value.put("usertoken",token);
        value.put("profimgurl",img);
        db.insert(ILocalCred.TABLE_NAME,null, value);
        db.close();

    }
    // Method to query Server Name
    public String getCurrentServername() {
        return querySingleField(ICurrentCred.COLUMN_SERVERNAME);
    }

    // Method to query Profile Image URL
    public String getCurrentProfileImageUrl() {
        return querySingleField(ICurrentCred.COLUMN_PROFIMGURL);
    }

    // Method to query Username
    public String getCurrentUsername() {
        return querySingleField(ICurrentCred.COLUMN_USERNAME);
    }

    // Method to query Token
    public String getCurrentToken() {
        return querySingleField(ICurrentCred.COLUMN_TOKEN);
    }

    // Helper method to query a single field
    private String querySingleField(String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = null;
        Cursor cursor = db.query(
                ICurrentCred.TABLE_NAME,
                new String[]{columnName},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return result;
    }
    public ArrayList<JSONObject> getAllData(String tableName) {
        ArrayList<JSONObject> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                tableName,
                null, // null means select all columns
                null, null, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    try {
                        String columnName = cursor.getColumnName(i);
                        String value = cursor.getString(i);
                        jsonObject.put(columnName, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dataList.add(jsonObject);
            }
            cursor.close();
        }

        db.close();
        return dataList;
    }
    public int updateCurrentCred(String serverName, String profileImgUrl, String username, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare values for update
        ContentValues values = new ContentValues();
        values.put(ICurrentCred.COLUMN_SERVERNAME, serverName);
        values.put(ICurrentCred.COLUMN_PROFIMGURL, profileImgUrl);
        values.put(ICurrentCred.COLUMN_USERNAME, username);
        values.put(ICurrentCred.COLUMN_TOKEN, token);

        // Attempt to update the record with _ID = 1
        int rowsUpdated = db.update(
                ICurrentCred.TABLE_NAME,
                values,
                ICurrentCred._ID + " = ?",
                new String[]{"1"}
        );

        // If no rows were updated, insert a new record with _ID = 1
        if (rowsUpdated == 0) {
            values.put(ICurrentCred._ID, 1); // Ensure _ID is set to 1
            db.insert(ICurrentCred.TABLE_NAME, null, values);
        }

        db.close();
        return rowsUpdated;
    }
    public boolean setCurrentCredFromLocalCred(String username, String serverName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Query the record from ILocalCred based on username and servername
        Cursor cursor = db.query(
                ILocalCred.TABLE_NAME,
                null,
                ILocalCred.COLUMN_USERNAME + " = ? AND " + ILocalCred.COLUMN_SERVERNAME + " = ?",
                new String[]{username, serverName},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(ICurrentCred._ID, 1);  // Enforce _ID = 1 for ICurrentCred
            values.put(ICurrentCred.COLUMN_SERVERNAME, cursor.getString(cursor.getColumnIndexOrThrow(ILocalCred.COLUMN_SERVERNAME)));
            values.put(ICurrentCred.COLUMN_PROFIMGURL, cursor.getString(cursor.getColumnIndexOrThrow(ILocalCred.COLUMN_PROFIMGURL)));
            values.put(ICurrentCred.COLUMN_USERNAME, cursor.getString(cursor.getColumnIndexOrThrow(ILocalCred.COLUMN_USERNAME)));
            values.put(ICurrentCred.COLUMN_TOKEN, cursor.getString(cursor.getColumnIndexOrThrow(ILocalCred.COLUMN_TOKEN)));

            cursor.close();

            // Replace the existing record in ICurrentCred (if any)
            long result = db.replace(ICurrentCred.TABLE_NAME, null, values);
            db.close();

            return result != -1;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return false;
    }
}
