package com.example.media;

import java.io.File;

public class MyFile extends File {
    public Boolean _isNew;
    public Boolean _isFolder;
    public String _folderName;
    public String ext;
    public MyFile(String pathname, Boolean isNew, Boolean isFolder) {
        super((isFolder) ? "/dev/null" : pathname);
        _isNew = isNew;
        _isFolder = isFolder;
        _folderName = (isFolder) ? pathname : null;
        ext = pathname.substring(pathname.lastIndexOf(".") + 1).trim();
    }

    @Override
    public String toString() {
        if (_folderName != null) {
            return _folderName;
        }
        return this.getName();
    }

    public boolean getIsNew() {
        return _isNew;
    }

    public boolean getIsFolder() {
        return _isFolder;
    }
}
