package com.example.media;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.media.databinding.FragmentProcessBinding;

import java.util.ArrayList;

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
        if (getArguments() != null) {
            Integer idx = getArguments().getInt("myIdx");
            Log.v("ARG", String.valueOf(idx));
        }

        if (tagsManager == null) {
            tagsManager = receiver.getDBManager("Tags");
        }
        if (listsManager == null) {
            listsManager = receiver.getDBManager("Lists");
        }

        ArrayList<String> tagsList = new ArrayList<>();
        ArrayList<String> listsList = new ArrayList<>();

        tagsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.text_item, tagsList);
        binding.processTags.setAdapter(tagsAdapter);

        listsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.text_item, listsList);
        binding.processLists.setAdapter(listsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}