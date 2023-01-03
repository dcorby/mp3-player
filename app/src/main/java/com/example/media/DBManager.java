package com.example.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.media.DB.Files;
import com.example.media.DB.FilesLists;
import com.example.media.DB.FilesTags;
import com.example.media.DB.Lists;
import com.example.media.DB.ListsTags;
import com.example.media.DB.Tags;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        database = helper.getWritableDatabase(); // this will call instances' onopen()
        return this;
    }

    public void close() {
        helper.close();
    }

    public void insert(ContentValues contentValues) throws SQLiteConstraintException {
        // https://stackoverflow.com/questions/3421577/sqliteconstraintexception-not-caught
        //database.insert(table, null, contentValues);
        database.insertOrThrow(table, null, contentValues);
    }

    // https://stackoverflow.com/questions/6293063/identifying-datatype-of-a-column-in-an-sqlite-android-cursor
    public ArrayList<HashMap> fetch(String query, String[] args) {
        Cursor cursor = database.rawQuery(query, args);
        int columns = cursor.getColumnCount();
        ArrayList<HashMap> arrayList = new ArrayList();
        while (cursor.moveToNext()) {
            HashMap row = new HashMap(columns);
            for (int i = 0; i <= columns; i++) {
                switch (cursor.getType(i))  {
                    case Cursor.FIELD_TYPE_FLOAT:
                        row.put(cursor.getColumnName(i), cursor.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        row.put(cursor.getColumnName(i), cursor.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        row.put(cursor.getColumnName(i), cursor.getString(i));
                        break;
                }
            }
            arrayList.add(row);
        }
        return arrayList;
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