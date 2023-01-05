package com.example.media;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
        setProcessed(dbManager, new String[]{});
        setFolders(dbManager, new String[]{});
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
        if (filesNew == null) {
            filesNew = new ArrayList<MyFile>();
        } else {
            filesNew.clear();
        }
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
    public void setProcessed(DBManager dbManager, String[] tags) {
        if (filesProcessed == null) {
            filesProcessed = new ArrayList<MyFile>();
        } else {
            filesProcessed.clear();
        }
        String query;
        if (tags == null) {
            query = "SELECT * FROM files";
        } else {
            String tmp[] = new String[tags.length];
            Arrays.fill(tmp, "?");
            String placeholders = String.join(",", tmp);
            query = "SELECT t1.id AS id, t1.name AS name FROM files AS t1 JOIN filestags AS t2 ON t1.id = t2.file WHERE t2.tag IN (" + placeholders +")";
        }

        ArrayList<HashMap> tmp = dbManager.fetch(query, tags);
        for (int i = 0; i < tmp.size(); i++) {
            String pathname = getFilesRoot() + "/processed/" + tmp.get(i).get("id") + ".mp3";
            String name = tmp.get(i).get("name").toString() + ".mp3";
            MyFile processed = new MyFile(pathname, name, false, false);
            filesProcessed.add(processed);
        }
    }

    // setFolders()
    public void setFolders(DBManager dbManager, String[] tags) {
        if (folders == null) {
            folders = new ArrayList<MyFile>();
        } else {
            folders.clear();
        }

        String query;
        if (tags == null) {
            query = "SELECT * FROM lists ORDER BY name ASC";
        } else {
            String tmp[] = new String[tags.length];
            Arrays.fill(tmp, "?");
            String placeholders = String.join(",", tmp);
            query = "SELECT t1.name AS name FROM lists AS t1 JOIN liststags AS t2 ON t1.name = t2.list WHERE t2.tag IN (" + placeholders +")";
        }

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
    public ArrayList<MyFile> getAll(Boolean withFolders, Boolean withNew) {
        if (filesAll == null) {
            filesAll = new ArrayList<MyFile>();
        } else {
            filesAll.clear();
        }
        if (withNew) {
            filesAll.addAll(filesNew);
        }
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
}
