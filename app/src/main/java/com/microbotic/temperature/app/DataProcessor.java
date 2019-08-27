package com.microbotic.temperature.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.microbotic.temperature.model.Student;
import com.microbotic.temperature.model.StudentTest;

import java.util.ArrayList;

import static com.microbotic.temperature.app.DatabaseHelper.DB_TABLE;
import static com.microbotic.temperature.app.DatabaseHelper.KEY_CLASS;
import static com.microbotic.temperature.app.DatabaseHelper.KEY_ID;
import static com.microbotic.temperature.app.DatabaseHelper.KEY_IMAGE;
import static com.microbotic.temperature.app.DatabaseHelper.KEY_NAME;

public class DataProcessor {

    private DatabaseHelper database;

    public DataProcessor(Context context){
        database = new DatabaseHelper(context);
    }


    public void registerFace(StudentTest student) throws SQLiteException {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,    student.getName());
        cv.put(KEY_CLASS,    student.getClassName());
        cv.put(KEY_IMAGE,   student.getImageName());
        db.insert( DB_TABLE, null, cv );
    }

    public synchronized ArrayList<StudentTest> getAllStudents(){
        ArrayList<StudentTest> list = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(DB_TABLE, null, null, null, null, null, null);

        while (cursor.moveToNext()){

            list.add(new StudentTest(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_CLASS)),
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE))
            ));
        }

        cursor.close();
        db.close();

        return list;
    }

    public synchronized void removeFace(String id){
        SQLiteDatabase db = database.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE + " WHERE " + KEY_ID + " = '" + id + "'");
        db.close();
    }

}
