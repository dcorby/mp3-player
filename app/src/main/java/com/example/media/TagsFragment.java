package com.example.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.media.databinding.FragmentTagsBinding;

public class TagsFragment extends Fragment {

    private FragmentTagsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTagsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "Tags!", Toast.LENGTH_SHORT).show();
                //NavHostFragment.findNavController(TagsFragment.this)
                //        .navigate(R.id.action_TagsFragment_to_AddTagFragment);
                //Navigation.findNavController(view).navigate(R.id.viewTransactionsAction);
                NavHostFragment.findNavController(TagsFragment.this)
                        .navigate(R.id.action_TagsFragment_to_AddTagFragment);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}