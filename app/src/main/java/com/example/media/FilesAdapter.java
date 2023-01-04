package com.example.media;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.media.MyFile;
import java.util.ArrayList;

public class FilesAdapter extends ArrayAdapter {

    public FilesAdapter(Context context, ArrayList<MyFile> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.media_item, parent, false);
        }

        MyFile myFile = (MyFile)getItem(position);

        TextView label = convertView.findViewById(R.id.label);
        label.setText(myFile.toString());
        if (myFile.getIsNew()) {
            label.setTextColor(Color.parseColor("#cccccc"));
        } else {
            if (myFile.getIsFolder()) {
                ImageView imageView = convertView.findViewById(R.id.folder);
                imageView.setVisibility(View.VISIBLE);
            } else {
                ImageView imageView = convertView.findViewById(R.id.play);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }
}