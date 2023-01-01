package com.example.media;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.media.databinding.FragmentHomeBinding;

import java.io.File;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public MainReceiver receiver;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        receiver = (MainReceiver) getActivity();

        File newList[] = receiver.getNewList();

        Log.v("YYY", String.valueOf(newList.length));
        if (newList.length > 0) {
            View tag = inflater.inflate(R.layout.tag, null);
            TextView textView = tag.findViewById(R.id.text);
            textView.setText("New");
            binding.tagholder.addView(tag);
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //binding.tagholder



//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}