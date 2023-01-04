package com.example.media;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FileManager {

    public ArrayList<MyFile> filesNew;
    public ArrayList<MyFile> filesProcessed;
    public ArrayList<MyFile> folders;
    public ArrayList<MyFile> filesAll;
    public ArrayList<MyFile> filesFiltered;
    public ArrayList<MyFile> filesList;

    public FileManager(DBManager dbManager) {
        setNew();
        setProcessed(dbManager);
        setFolders(dbManager);
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
            filesNew.add(new MyFile(tmp[i].getAbsolutePath(), true, false));
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
            String pathname = getFilesRoot() + "/processed/" + tmp.get(i).get("id") + ".mp3";
            String name = tmp.get(i).get("name").toString() + ".mp3";
            MyFile processed = new MyFile(pathname, name, false, false);
            filesProcessed.add(processed);
        }
    }

    // setFolders()
    public void setFolders(DBManager dbManager) {
        folders = new ArrayList<MyFile>();
        String query = "SELECT * FROM lists";
        ArrayList<HashMap> tmp = dbManager.fetch(query, null);
        for (int i = 0; i < tmp.size(); i++) {
            MyFile processed = new MyFile(tmp.get(i).get("name").toString(), false, true);
            folders.add(processed);
        }
    }

    // getFolders()
    public ArrayList<MyFile> getFolders() {
        return folders;
    }

    // getAll()
    public ArrayList<MyFile> getAll(Boolean withFolders) {

        filesAll = new ArrayList<MyFile>();
        filesAll.addAll(filesNew);
        filesAll.addAll(filesProcessed);
        if (withFolders) {
            filesAll.addAll(folders);
        }
        Collections.sort(filesAll, new Comparator<MyFile>() {
            @Override
            public int compare(MyFile f1, MyFile f2) {
                if (f1.getIsNew() != f2.getIsNew()) {
                    return Boolean.compare(f2.getIsNew(), f1.getIsNew());
                }
                return f1.getName().compareTo(f2.getName());
            }
        });
        return filesAll;
    }

//    // getFilesList()
//    public ArrayList<MyFile> getFilesList() {
//        return filesList;
//    }
//
//    // setFilesList()
//    public void setFilesList(DBManager dbManager) {
//        filesList  = new ArrayList<MyFile>();
//    }
}
