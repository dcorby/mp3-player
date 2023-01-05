package com.example.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<SpinnerItem> {
    private Context mContext;
    private ArrayList<SpinnerItem> listState;
    private CustomAdapter myAdapter;
    private boolean isFromView = false;
    private Callback callback;

    public CustomAdapter(Context context, int resource, ArrayList<SpinnerItem> objects, Callback callback) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<SpinnerItem>) objects;
        this.myAdapter = this;
        this.callback = callback;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // check whether checked event is from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if (position == 0) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                    callback.run(listState.get(position).getTitle(), isChecked);
                }

            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}