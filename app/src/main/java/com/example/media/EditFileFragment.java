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
import com.example.media.databinding.FragmentProcessBinding;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EditFileFragment extends Fragment {

    private FragmentProcessBinding binding;
    public ArrayAdapter tagsAdapter;
    public ArrayAdapter listsAdapter;
    private MainReceiver receiver;
    private DBManager dbManager;
    private LayoutInflater layoutInflater;
    private MyFile myFile;
    private FileManager fileManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        layoutInflater = inflater;
        receiver = (MainReceiver) getActivity();
        fileManager = receiver.getFileManager();
        binding = FragmentProcessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Integer idx = getArguments().getInt("myIdx");
        myFile = fileManager.getAll(true).get(idx);
        binding.processName.setText(myFile.getName());
        binding.processName.setEnabled(false);

        if (dbManager == null) {
            dbManager = receiver.getDBManager();
        }

        ArrayList<Object> tagsList = dbManager.fetch("SELECT * FROM tags", null, "name");
        tagsList.add(0, "Tags");
        ArrayList<Object> listsList = dbManager.fetch("SELECT * FROM lists", null, "name");
        listsList.add(0, "Lists");

        tagsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, tagsList);
        binding.processTags.setAdapter(tagsAdapter);

        listsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, listsList);
        binding.processLists.setAdapter(listsAdapter);

        // get name, tags, and lists
        String dataName = binding.processName.getText().toString();
        ArrayList<String> dataTags = new ArrayList();
        ArrayList<String> dataLists = new ArrayList();

        // add tags and lists to holders
        binding.processTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                View tag = layoutInflater.inflate(R.layout.tag, null);
                TextView textView = tag.findViewById(R.id.text);
                String name = tagsList.get(position).toString();
                textView.setText(name);
                binding.tagholder.addView(tag);
                dataTags.add(name);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.processLists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                View list = layoutInflater.inflate(R.layout.tag, null);
                TextView textView = list.findViewById(R.id.text);
                String name = listsList.get(position).toString();
                textView.setText(name);
                binding.listholder.addView(list);
                dataLists.add(name);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.processSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // begin transactions
                    dbManager.beginTransaction();

                    // insert the file
                    ContentValues filesValues = new ContentValues();
                    filesValues.put("name", dataName);
                    long id = dbManager.insert("files", filesValues);

                    // insert filestags
                    if (dataTags.size() > 0) {
                        for (int i = 0; i < dataTags.size(); i++) {
                            ContentValues filesTagsValues = new ContentValues();
                            filesTagsValues.put("file", id);
                            filesTagsValues.put("tag", dataTags.get(i));
                            dbManager.insert("filestags", filesTagsValues);
                        }
                    }

                    // insert fileslists
                    if (dataLists.size() > 0) {
                        for (int i = 0; i < dataLists.size(); i++) {
                            ContentValues filesListsValues = new ContentValues();
                            filesListsValues.put("file", id);
                            filesListsValues.put("list", dataLists.get(i));
                            dbManager.insert("fileslists", filesListsValues);
                        }
                    }

                    String newPath = myFile.getParent() + "/processed/" + id + "." + myFile.ext;
                    myFile.renameTo(new File(newPath));
                    dbManager.commitTransaction();
                    fileManager.setNew();
                    fileManager.setProcessed(dbManager, null);
                    Toast.makeText(getContext(), "File processed!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(EditFileFragment.this).popBackStack();
                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error processing file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
