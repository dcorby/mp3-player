package com.example.media;

import java.io.File;

public class MyFile extends File {
    public Boolean is_new;
    public MyFile(String pathname, Boolean is_n) {
        super(pathname);
        is_new = is_n;
    }

    @Override
    public String toString() {
        //return this.getAbsolutePath();
        return this.getName();
    }
}
