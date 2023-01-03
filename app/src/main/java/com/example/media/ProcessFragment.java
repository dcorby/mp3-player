package com.example.media;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.media.databinding.FragmentProcessBinding;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ProcessFragment extends Fragment {

    private FragmentProcessBinding binding;
    public ArrayAdapter tagsAdapter;
    public ArrayAdapter listsAdapter;
    private MainReceiver receiver;
    private DBManager tagsManager;
    private DBManager listsManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        receiver = (MainReceiver) getActivity();
        binding = FragmentProcessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Integer idx = getArguments().getInt("myIdx");
        //Log.v("ARG", String.valueOf(idx));
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

        ArrayList<String> tagsNames = new ArrayList<>();
        ArrayList<String> listsNames = new ArrayList<>();

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
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.processLists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });





    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}