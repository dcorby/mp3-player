package com.example.media;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.media.databinding.FragmentEditfileBinding;
import com.google.android.flexbox.FlexboxLayout;
import java.util.ArrayList;

public class EditFileFragment extends Fragment {

    private FragmentEditfileBinding binding;
    public ArrayAdapter tagsAdapter;
    public ArrayAdapter listsAdapter;
    private MainReceiver receiver;
    private DBManager dbManager;
    private LayoutInflater layoutInflater;
    private MyFile myFile;
    private FileManager fileManager;
    private ArrayList<Object> listsList;
    private ArrayList<Object> tagsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        layoutInflater = inflater;
        receiver = (MainReceiver) getActivity();
        fileManager = receiver.getFileManager();
        binding = FragmentEditfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Integer idx = getArguments().getInt("myIdx");
        myFile = fileManager.getAll(true, true).get(idx);
        binding.editfileName.setText(myFile.toString());

        if (dbManager == null) {
            dbManager = receiver.getDBManager();
        }

        // add the spinners with all tags and lists
        tagsList = dbManager.fetch("SELECT * FROM tags", null, "name");
        tagsList.add(0, "Add Tag...");
        listsList = dbManager.fetch("SELECT * FROM lists", null, "name");
        listsList.add(0, "Add List...");

        tagsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, tagsList);
        binding.editfileTags.setAdapter(tagsAdapter);
        listsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, listsList);
        binding.editfileLists.setAdapter(listsAdapter);

        // get the data for the holders
        String args[] = { myFile.getName().replace("." + myFile.ext, "") };
        ArrayList<Object> dataTags = dbManager.fetch("SELECT * FROM filestags WHERE file = ?", args, "tag");
        ArrayList<Object> dataLists = dbManager.fetch("SELECT * FROM fileslists WHERE file = ?", args, "list");

        // update the holders
        updateTags(layoutInflater, binding.tagholder, dataTags);
        updateTags(layoutInflater, binding.listholder, dataLists);

        // and/remove to/from holders
        binding.editfileTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                binding.editfileTags.setSelection(0);
                updateTags(layoutInflater, binding.tagholder, dataTags);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.editfileLists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String name = listsList.get(position).toString();
                if (dataLists.contains(name)) {
                    dataLists.remove(name);
                } else {
                    dataLists.add(name);
                }
                binding.editfileLists.setSelection(0);
                updateTags(layoutInflater, binding.listholder, dataLists);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.editfileSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    // begin transactions
                    dbManager.beginTransaction();

                    // get a reference to the file
                    String curName = myFile.toString();
                    String args1[] = { curName };
                    ArrayList<Object> results = dbManager.fetch("SELECT * FROM files WHERE name = ?", args1, "id");
                    String id = (String)results.get(0);

                    // update the name
                    String newName = binding.editfileName.getText().toString().trim();
                    if (curName != newName) {
                        String[] args2 = { newName, curName };
                        dbManager.exec("UPDATE files SET name = ? WHERE name = ?", args2);
                    }

                    // delete the tags and lists associations
                    String[] args3 = { id };
                    dbManager.exec("DELETE FROM filestags WHERE file = ?", args3);
                    dbManager.exec("DELETE FROM fileslists WHERE file = ?", args3);

                    // update the tags and lists associations
                    for (int i = 0; i < dataTags.size(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("file", id);
                        values.put("tag", dataTags.get(i).toString());
                        dbManager.insert("filestags", values);
                    }
                    for (int i = 0; i < dataLists.size(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("file", id);
                        values.put("list", dataLists.get(i).toString());
                        dbManager.insert("fileslists", values);
                    }

                    dbManager.commitTransaction();

                    // if the name was changed or tags were modified, recompile files
                    if (curName != newName) {
                        String[] array = dataTags.toArray(new String[dataTags.size()]);
                        fileManager.setProcessed(dbManager, array);
                        fileManager.setNew();
                    }

                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error editing file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });
    }

    public void updateTags(LayoutInflater inflater, FlexboxLayout holder, ArrayList<Object> tags) {
        holder.removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            TextView tag = (TextView)inflater.inflate(R.layout.tag, null);
            String name = tags.get(i).toString();
            if (name != "Add Tags..." && name != "Add Lists..." ) {
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
