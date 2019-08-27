package com.microbotic.temperature.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "database_name";

    static final String DB_TABLE = "table_image";

    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_CLASS = "class";
    static final String KEY_IMAGE = "image";

    private static final String CREATE_TABLE_STUDENT = "CREATE TABLE " + DB_TABLE + "("+
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_NAME + " TEXT," +
            KEY_CLASS + " TEXT," +
            KEY_IMAGE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        onCreate(db);
    }


}
