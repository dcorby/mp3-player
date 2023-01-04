package com.example.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DBManager {

    private SQLiteOpenHelper helper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        helper = new DBHelper(context);
        // this will call oncreate()/onopen()
        database = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    public long insert(String table, ContentValues contentValues) throws SQLiteConstraintException {
        // https://stackoverflow.com/questions/3421577/sqliteconstraintexception-not-caught
        long id = database.insertOrThrow(table, null, contentValues);
        return id;
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
    public ArrayList<Object> fetch(String query, String[] args, String flatten) {
        ArrayList<HashMap> tmp = fetch(query, args);
        ArrayList<Object> flattened = new ArrayList<>();
        for (int i = 0; i < tmp.size(); i++) {
            flattened.add(String.valueOf(tmp.get(i).get(flatten)));
        }
        return flattened;
    }

    public void beginTransaction() {
        database.beginTransaction();
    }
    public void commitTransaction() {
        database.setTransactionSuccessful();
    }
    public void endTransaction() {
        database.endTransaction();
    }

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