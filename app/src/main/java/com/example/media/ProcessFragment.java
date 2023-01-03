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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProcessFragment extends Fragment {

    private FragmentProcessBinding binding;
    public ArrayAdapter tagsAdapter;
    public ArrayAdapter listsAdapter;
    private MainReceiver receiver;
    private DBManager tagsManager;
    private DBManager listsManager;
    private DBManager filesManager;
    private DBManager filesListsManager;
    private DBManager filesTagsManager;
    private LayoutInflater layoutInflater;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        receiver = (MainReceiver) getActivity();
        layoutInflater = inflater;
        binding = FragmentProcessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Integer idx = getArguments().getInt("myIdx");
        MyFile myFile = receiver.getNewMyFiles().get(idx);
        binding.processName.setText(myFile.getName());
        binding.processName.setEnabled(false);

        if (tagsManager == null) {
            tagsManager = receiver.getDBManager("Tags");
        }
        if (listsManager == null) {
            listsManager = receiver.getDBManager("Lists");
        }

        ArrayList<HashMap> tagsList = tagsManager.fetch("SELECT * FROM tags", null);
        ArrayList<HashMap> listsList = listsManager.fetch("SELECT * FROM lists", null);

        ArrayList<String> tagsNames = new ArrayList<>(Arrays.asList("Tags"));
        ArrayList<String> listsNames = new ArrayList<>(Arrays.asList("Lists"));

        for (int i = 0; i < tagsList.size(); i++) {
            tagsNames.add(String.valueOf(tagsList.get(i).get("name")));
        }
        for (int i = 0; i < listsList.size(); i++) {
            listsNames.add(String.valueOf(listsList.get(i).get("name")));
        }

        tagsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, tagsNames);
        binding.processTags.setAdapter(tagsAdapter);

        listsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, listsNames);
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
                String name = tagsNames.get(position);
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
                String name = listsNames.get(position);
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
                    // get the db managers
                    filesManager = receiver.getDBManager("Files");
                    filesTagsManager = receiver.getDBManager("FilesTags");
                    filesListsManager = receiver.getDBManager("FilesLists");

                    // begin transactions
                    filesManager.beginTransaction();
                    filesTagsManager.beginTransaction();
                    //filesListsManager.beginTransaction();

                    // insert the file
                    ContentValues filesValues = new ContentValues();
                    filesValues.put("name", dataName);
                    long id = filesManager.insert(filesValues);

                    // insert filestags
                    if (dataTags.size() > 0) {
                        for (int i = 0; i < dataTags.size(); i++) {
                            ContentValues filesTagsValues = new ContentValues();
                            filesTagsValues.put("file", id);
                            filesTagsValues.put("tag", dataTags.get(i));
                            filesTagsManager.insert(filesTagsValues);
                        }
                    }

                    // insert fileslists
                    if (dataLists.size() > 0) {
                        for (int i = 0; i < dataLists.size(); i++) {
                            ContentValues filesListsValues = new ContentValues();
                            filesListsValues.put("file", id);
                            filesListsValues.put("list", dataLists.get(i));
                            filesTagsManager.insert(filesListsValues);
                        }
                    }

                    filesManager.commitTransaction();
                    //filesTagsManager.commitTransaction();
                    //filesListsManager.commitTransaction();
                    Toast.makeText(getContext(), "File processed!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(ProcessFragment.this).popBackStack();

                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error processing file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    filesManager.endTransaction();
                    //filesTagsManager.endTransaction();
                    //filesListsManager.endTransaction();
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