package com.example.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.media.databinding.FragmentListBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private FileManager fileManager;
    public ArrayAdapter arrayAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        fileManager = receiver.getFileManager();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Integer idx = getArguments().getInt("myIdx");
        MyFile myFile = fileManager.getAll(true).get(idx);
        String name = myFile.getName();

        String query = "SELECT t1.file AS file, t2.name AS name FROM fileslists AS t1 INNER JOIN files AS t2 ON(t1.file = t2.id) WHERE list = ?";
        String args[] = { name };
        ArrayList<HashMap> files = dbManager.fetch(query, args);
        ArrayList<Object> fileNames = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            HashMap foo = files.get(i);
            fileNames.add(files.get(i).get("name"));
        }

        // set the files/folders list
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.media_item, fileNames);
        binding.medialist.setAdapter(arrayAdapter);

        binding.medialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                MyFile myFile = fileManager.getAll(true).get(pos);

                // only have files. play it...
            }
        });

        binding.medialist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

//                MyFile myFile = fileManager.getAll(true).get(pos);
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("myIdx", pos);
//                NavHostFragment.findNavController(ListFragment.this)
//                        .navigate(R.id.action_HomeFragment_to_EditFileFragment, bundle);
//
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}