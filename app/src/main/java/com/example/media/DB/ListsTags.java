package com.example.media.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// https://www.digitalocean.com/community/tutorials/android-sqlite-database-example-tutorial
// https://developer.android.com/training/data-storage/sqlite
public class ListsTags extends SQLiteOpenHelper {

    static final String DB_NAME = "media.db";
    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = new StringBuilder()
            .append("CREATE TABLE liststags (")
            .append("  name TEXT PRIMARY KEY NOT NULL")
            .append("  typ TEXT")  // e.g. "Album", "Mix", etc. (can be null)
            .append(");").toString();

    public ListsTags(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS liststags");
        onCreate(db);
    }
}
