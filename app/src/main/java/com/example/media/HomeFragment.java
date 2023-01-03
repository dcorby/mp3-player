package com.example.media;

import android.os.Bundle;
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
    public ArrayAdapter arrayAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        receiver = (MainReceiver) getActivity();

        final String[] options = { "Foo", "Bar", "Baz" };
        Spinner spinner = binding.spinner;
        //Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);

        ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            SpinnerItem spinnerItem = new SpinnerItem();
            spinnerItem.setTitle(options[i]);
            spinnerItem.setSelected(false);
            spinnerItems.add(spinnerItem);
        }
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), 0, spinnerItems);
        spinner.setAdapter(customAdapter);

        ArrayList<MyFile> newMyFiles = receiver.getNewMyFiles();

        if (newMyFiles.size() > 0) {
            View tag = inflater.inflate(R.layout.tag, null);
            TextView textView = tag.findViewById(R.id.text);
            textView.setText("New");
            binding.tagholder.addView(tag);
            arrayAdapter = new ArrayAdapter<MyFile>(getActivity(), R.layout.media_item, newMyFiles);
            binding.medialist.setAdapter(arrayAdapter);
        }

        binding.medialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyFile myFile = newMyFiles.get(i);

                if (myFile.is_new) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", i);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_ProcessFragment, bundle);
                } else {
                    String s = "is not new";
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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