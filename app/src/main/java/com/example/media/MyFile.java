package com.example.media;

import java.io.File;

public class MyFile extends File {
    public Boolean is_new;
    public String ext;
    public MyFile(String pathname, Boolean is_n) {
        super(pathname);
        is_new = is_n;
        ext = pathname.substring(pathname.lastIndexOf(".") + 1).trim();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
