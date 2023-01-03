package com.example.media.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// https://www.digitalocean.com/community/tutorials/android-sqlite-database-example-tutorial
// https://developer.android.com/training/data-storage/sqlite
public class FilesLists extends SQLiteOpenHelper {

    static final String DB_NAME = "media.db";
    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS fileslists (")
            .append("  file TEXT PRIMARY KEY NOT NULL,")
            .append("  list TEXT NOT NULL")
            .append(");").toString();

    public FilesLists(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS fileslists");
        onCreate(db);
    }
}

