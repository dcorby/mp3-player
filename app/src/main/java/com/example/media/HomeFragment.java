package com.example.media;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.media.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import android.media.MediaPlayer;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private FileManager fileManager;
    public ArrayAdapter arrayAdapter;
    private MediaPlayer mediaPlayer;

    // for the clock
    Handler handler = new Handler();
    Runnable runnable;

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
            tags.add(1, "New");
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
            TextView tag = (TextView)inflater.inflate(R.layout.tag, null);
            tag.setText("New");
            binding.tagholder.addView(tag);
        }

        // set the files/folders list
        arrayAdapter = new FilesAdapter(getActivity(), fileManager.getAll(true));
        //arrayAdapter = new ArrayAdapter<MyFile>(getActivity(), R.layout.media_item, fileManager.getNew());
        binding.medialist.setAdapter(arrayAdapter);

        binding.medialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                MyFile myFile = fileManager.getAll(true).get(pos);

                if (myFile.getIsNew()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", pos);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_ProcessFragment, bundle);
                } else if (myFile.getIsFolder()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", pos);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_ListFragment, bundle);
                } else {
                    Uri uri = Uri.parse(myFile.getAbsolutePath());
                    mediaPlayer = MediaPlayer.create(getActivity(), uri);
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        int duration = mediaPlayer.getDuration();
                        binding.player.setVisibility(View.VISIBLE);
                        binding.name.setText(myFile.getName());
                        binding.length.setText(mediaPlayer.getDuration());

                        // https://www.tutorialspoint.com/how-to-run-a-method-every-10-seconds-in-android
                        handler.postDelayed(runnable = new Runnable() {
                            public void run() {
                                handler.postDelayed(runnable, 1000);
                                binding.current.setText(mediaPlayer.getCurrentPosition());
                            }
                        }, 1000);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error playing file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.medialist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

                MyFile myFile = fileManager.getAll(true).get(pos);

                if (!myFile.getIsFolder()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", pos);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_EditFileFragment, bundle);
                }

                return true;
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