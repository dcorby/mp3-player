package com.example.media;

public class SpinnerItem {
    private String title;
    private boolean selected;

    public SpinnerItem(String t, Boolean s) {
        title = t;
        selected = s;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}