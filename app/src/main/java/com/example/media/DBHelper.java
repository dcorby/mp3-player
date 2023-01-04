package com.example.media;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// https://www.digitalocean.com/community/tutorials/android-sqlite-database-example-tutorial
// https://developer.android.com/training/data-storage/sqlite
public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_FILES = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS files (")
            .append("  id INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append("  name TEXT NOT NULL,")
            .append("  creator TEXT")
            .append(");").toString();

    private static final String CREATE_FILESLISTS = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS fileslists (")
            .append("  file TEXT PRIMARY KEY NOT NULL,")
            .append("  list TEXT NOT NULL")
            .append(");").toString();

    private static final String CREATE_FILESTAGS = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS filestags (")
            .append("  file TEXT PRIMARY KEY NOT NULL,")
            .append("  tag TEXT NOT NULL")
            .append(");").toString();

    private static final String CREATE_LISTS = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS lists (")
            .append("  name TEXT PRIMARY KEY NOT NULL")
            .append(");").toString();

    private static final String CREATE_LISTSTAGS = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS liststags (")
            .append("  list TEXT PRIMARY KEY NOT NULL,")
            .append("  tag TEXT NOT NULL")
            .append(");").toString();

    private static final String CREATE_TAGS = new StringBuilder()
            .append("CREATE TABLE IF NOT EXISTS tags (")
            .append("  name TEXT PRIMARY KEY NOT NULL")
            .append(");").toString();

    public DBHelper(Context context) {
        super(context, "media.db", null, 1);
    }

    private void createTables(SQLiteDatabase db) {
        String createTable[] = {
                CREATE_FILES,
                CREATE_FILESLISTS,
                CREATE_FILESTAGS,
                CREATE_LISTS,
                CREATE_LISTSTAGS,
                CREATE_TAGS
        };
        for (int i = 0; i < createTable.length; i++) {
            db.execSQL(createTable[i]);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tables[] = {
                "files",
                "fileslists",
                "filestags",
                "lists",
                "liststags",
                "tags"
        };
        for (int i = 0; i < tables.length; i++) {
            db.execSQL("DROP TABLE IF EXISTS " + tables[i]);
        }
        createTables(db);
    }
}