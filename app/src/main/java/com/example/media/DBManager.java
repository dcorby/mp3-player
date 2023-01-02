package com.example.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import com.example.media.DB.*;
import com.example.media.DB.Files;
import com.example.media.DB.FilesLists;
import com.example.media.DB.FilesTags;
import com.example.media.DB.Lists;
import com.example.media.DB.ListsTags;
import com.example.media.DB.Tags;

import java.lang.reflect.InvocationTargetException;

public class DBManager {

    private SQLiteOpenHelper helper;

    private Context context;

    private String table;
    private SQLiteDatabase database;

    public DBManager(Context c, String t) {
        context = c;
        table = t;
    }

    public DBManager open() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class cls = Class.forName("com.example.media.DB." + table);
        helper = (SQLiteOpenHelper) cls.getDeclaredConstructor(Context.class).newInstance(context);
        database = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    //public void insert(String name, String desc) {
    public void insert() {
        ContentValues contentValue = new ContentValues();


        contentValue.put("creator", "some creator");
        contentValue.put("name", "some name");
        database.insert(table, null, contentValue);
    }

    //public Cursor fetch() {
        //String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.SUBJECT, DatabaseHelper.DESC };
        //Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        //if (cursor != null) {
        //    cursor.moveToFirst();
        //}
        //return cursor;
    //}

    //public int update(long _id, String name, String desc) {
        //ContentValues contentValues = new ContentValues();
        //contentValues.put(DatabaseHelper.SUBJECT, name);
        //contentValues.put(DatabaseHelper.DESC, desc);
        //int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        //return i;
    //}

    public void delete(long _id) {
        //database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}