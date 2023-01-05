package com.example.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.media.databinding.FragmentListsBinding;
import java.util.ArrayList;

public class ListsFragment extends Fragment {

    private MainReceiver receiver;
    private DBManager dbManager;
    public ArrayAdapter arrayAdapter;
    private FragmentListsBinding binding;
    private ArrayList<String> listsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentListsBinding.inflate(inflater, container, false);
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String query = "SELECT name FROM lists ORDER BY name ASC";
        ArrayList<Object> listsList = dbManager.fetch(query, null, "name");

        // set the lists list
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item, listsList);
        binding.listslist.setAdapter(arrayAdapter);

        // add
        binding.addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ListsFragment.this)
                        .navigate(R.id.action_ListsFragment_to_AddListFragment);
            }
        });

        // edit
        binding.listslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("listName", listsList.get(pos).toString());
                NavHostFragment.findNavController(ListsFragment.this)
                        .navigate(R.id.action_ListsFragment_to_EditListFragment, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}