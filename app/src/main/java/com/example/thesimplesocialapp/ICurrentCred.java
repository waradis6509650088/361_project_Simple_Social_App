package com.example.thesimplesocialapp;

import android.provider.BaseColumns;

public interface ICurrentCred extends BaseColumns {
    public static final String TABLE_NAME = "currentCredential";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PROFIMGURL = "profimgurl";
    public static final String COLUMN_SERVERNAME = "servername";
    public static final String COLUMN_TOKEN = "usertoken";
}
