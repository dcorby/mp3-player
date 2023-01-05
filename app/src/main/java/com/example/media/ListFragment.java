package com.example.media;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.media.databinding.FragmentListBinding;
import java.util.ArrayList;
import java.util.HashMap;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private FileManager fileManager;
    public ArrayAdapter arrayAdapter;
    public ArrayList<MyFile> filesList;
    private MediaPlayer mediaPlayer;

    // for the clock
    Handler handler = new Handler();
    Runnable runnable;

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

        // get the folder name
        Integer idx = getArguments().getInt("myIdx");
        MyFile myFolder = fileManager.getAll(true, true).get(idx);
        String folder = myFolder.toString();

        // get the files in this folder
        // this block could go in file manager but it only gets used here
        String query = "SELECT t1.file AS id, t2.name AS name FROM fileslists AS t1 INNER JOIN files AS t2 ON(t1.file = t2.id) WHERE list = ?";
        String args[] = { folder };
        ArrayList<HashMap> files = dbManager.fetch(query, args);
        filesList = new ArrayList<MyFile>();
        for (int i = 0; i < files.size(); i++) {
            String filesRoot = receiver.getFileManager().getFilesRoot();
            String id = String.valueOf(files.get(i).get("id"));
            String name = (String)files.get(i).get("name");
            String pathname = filesRoot + "/processed/" + id + ".mp3";
            MyFile myFile = new MyFile(pathname, name, false, false);
            filesList.add(myFile);
        }

        // set the files/folders list
        arrayAdapter = new FilesAdapter(getActivity(), filesList);
        binding.medialist.setAdapter(arrayAdapter);

        binding.medialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                MyFile myFile = filesList.get(pos);
                Uri uri = Uri.parse(myFile.getAbsolutePath());
                mediaPlayer = MediaPlayer.create(getActivity(), uri);
                try {
                    mediaPlayer.start();
                    int duration = mediaPlayer.getDuration();
                    binding.player.player.setVisibility(View.VISIBLE);
                    binding.player.name.setText(myFile.toString());
                    binding.player.length.setText(String.valueOf(mediaPlayer.getDuration()));

                    // https://www.tutorialspoint.com/how-to-run-a-method-every-10-seconds-in-android
                    handler.postDelayed(runnable = new Runnable() {
                        public void run() {
                            if (handler != null) {
                                handler.postDelayed(runnable, 1000);
                                binding.player.current.setText(String.valueOf(mediaPlayer.getCurrentPosition()));
                            }
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error playing file", Toast.LENGTH_SHORT).show();
                }
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
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        handler = null;
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