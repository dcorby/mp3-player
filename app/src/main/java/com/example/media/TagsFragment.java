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

import com.example.media.databinding.FragmentTagsBinding;
import java.util.ArrayList;

public class TagsFragment extends Fragment {

    private MainReceiver receiver;
    private DBManager dbManager;
    public ArrayAdapter arrayAdapter;
    private FragmentTagsBinding binding;
    private ArrayList<String> listsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTagsBinding.inflate(inflater, container, false);
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String query = "SELECT name FROM tags ORDER BY name ASC";
        ArrayList<Object> tagsList = dbManager.fetch(query, null, "name");

        // set the lists list
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item, tagsList);
        binding.tagslist.setAdapter(arrayAdapter);

        // add
        binding.addtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TagsFragment.this)
                        .navigate(R.id.action_TagsFragment_to_AddTagFragment);
            }
        });

        // edit
        binding.tagslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("tagName", tagsList.get(pos).toString());
                NavHostFragment.findNavController(TagsFragment.this)
                        .navigate(R.id.action_TagsFragment_to_EditTagFragment, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}