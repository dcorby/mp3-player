package com.example.media;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    private ArrayList<MyFile> filesNew;
    private ArrayList<MyFile> filesProcessed;
    private ArrayList<MyFile> filesAll;
    private ArrayList<MyFile> filesFiltered;

    public FileManager(DBManager dbManager) {
        setNew();
        setProcessed(dbManager);
    }

    // getFilesRoot()
    public String getFilesRoot() {
        String sdRoot = Environment.getExternalStorageDirectory().toString();
        String filesRoot = sdRoot + "/Android/data/com.example.media/files";
        return filesRoot;
    }

    // getNew()
    public ArrayList<MyFile> getNew() {
        return filesNew;
    }

    // setNew()
    public void setNew() {
        filesNew = new ArrayList<MyFile>();
        File mediaFolder = new File(getFilesRoot());
        File tmp[] = mediaFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        for (int i = 0; i < tmp.length; i++) {
            filesNew.add(new MyFile(tmp[i].getAbsolutePath(), true));
        }
    }

    // getProcessed()
    public ArrayList<MyFile> getProcessed() {
        return filesProcessed;
    }

    // setProcessed()
    public void setProcessed(DBManager dbManager) {
        filesProcessed = new ArrayList<MyFile>();
        String query = "SELECT * FROM files";
        ArrayList<HashMap> tmp = dbManager.fetch(query, null);
        for (int i = 0; i < tmp.size(); i++) {
            String pathname = getFilesRoot() + "/" + tmp.get(i).get("name");
            MyFile processed = new MyFile(pathname, false);
            filesProcessed.add(processed);
        }
    }

    public ArrayList<MyFile> getAll() {
        filesAll = new ArrayList<MyFile>();
        for (int i = 0; i < filesNew.size(); i++) {
            filesAll.add(filesNew.get(i));
        }
        for (int i = 0; i < filesProcessed.size(); i++) {
            filesAll.add(filesProcessed.get(i));
        }
        return filesAll;
    }
}
