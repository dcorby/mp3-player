package com.example.media;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.media.databinding.FragmentAddlistBinding;
import com.google.android.flexbox.FlexboxLayout;
import java.util.ArrayList;

public class AddListFragment extends Fragment {

    private FragmentAddlistBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private LayoutInflater layoutInflater;
    private ArrayList<Object> tagsList;
    private ArrayAdapter tagsAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        layoutInflater = inflater;
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        binding = FragmentAddlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (dbManager == null) {
            dbManager = receiver.getDBManager();
        }

        // add the spinners with all tags
        tagsList = dbManager.fetch("SELECT * FROM tags", null, "name");
        tagsList.add(0, "Add Tag...");
        tagsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, tagsList);
        binding.addlistTags.setAdapter(tagsAdapter);

        // and/remove to/from holder
        ArrayList<Object> dataTags = new ArrayList<>();
        binding.addlistTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String name = tagsList.get(position).toString();
                if (dataTags.contains(name)) {
                    dataTags.remove(name);
                } else {
                    dataTags.add(name);
                }
                binding.addlistTags.setSelection(0);
                updateTags(layoutInflater, binding.tagholder, dataTags);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // add
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String name = binding.name.getText().toString().trim();
                    if (name.length() == 0) {
                        Toast.makeText(getContext(), "Add a value", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        dbManager.beginTransaction();
                        // insert to lists
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", name);
                        dbManager.insert("lists", contentValues);

                        // insert to liststags
                        for (int i = 0; i < dataTags.size(); i++) {
                            ContentValues values = new ContentValues();
                            values.put("list", name);
                            values.put("tag", dataTags.get(i).toString());
                            dbManager.insert("liststags", values);
                        }

                        dbManager.commitTransaction();
                        Toast.makeText(getContext(), "List added!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddListFragment.this).popBackStack();
                    } catch(SQLiteConstraintException e) {
                        Toast.makeText(getContext(), "List already exists!", Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error adding list", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });
    }

    private void updateTags(LayoutInflater inflater, FlexboxLayout holder, ArrayList<Object> tags) {
        holder.removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            TextView tag = (TextView)inflater.inflate(R.layout.tag, null);
            String name = tags.get(i).toString();
            if (name != "Add Tag...") {
                tag.setText(name);
                holder.addView(tag);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}