package com.example.media;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.media.databinding.FragmentHomeBinding;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private FileManager fileManager;
    public ArrayAdapter arrayAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        fileManager = receiver.getFileManager();

        // set the tags spinner
        ArrayList<Object> tags = dbManager.fetch("SELECT * FROM tags", null, "name");
        tags.add(0, "Tags");
        if (fileManager.getNew().size() > 0) {
            tags.add(0, "New");
        }
        Spinner spinner = binding.spinner;

        ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            String name = tags.get(i).toString();
            Boolean selected = (name == "New") ? true : false;
            SpinnerItem spinnerItem = new SpinnerItem(name, selected);
            spinnerItems.add(spinnerItem);
        }
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), 0, spinnerItems);
        spinner.setAdapter(customAdapter);

        // add the "New" tag
        if (fileManager.getNew().size() > 0) {
            View tag = inflater.inflate(R.layout.tag, null);
            TextView textView = tag.findViewById(R.id.text);
            textView.setText("New");
            binding.tagholder.addView(tag);
        }

        // set the files/folders list
        // arrayAdapter = new ArrayAdapter<MyFile>(getActivity(), R.layout.media_item, fileManager.getAll());
        arrayAdapter = new ArrayAdapter<MyFile>(getActivity(), R.layout.media_item, fileManager.getNew());
        binding.medialist.setAdapter(arrayAdapter);

        binding.medialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyFile myFile = fileManager.getAll().get(i);

                if (myFile.is_new) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", i);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_ProcessFragment, bundle);
                }
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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