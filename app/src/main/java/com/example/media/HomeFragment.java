package com.example.media;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private ArrayList<MyFile> filesList;

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
        // get the tags
        ArrayList<Object> tags = dbManager.fetch("SELECT * FROM tags", null, "name");
        tags.add(0, "Tags");
        if (fileManager.getNew().size() > 0) {
            tags.add(1, "New");
        }

        // get the spinner items
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            String name = tags.get(i).toString();
            SpinnerItem spinnerItem = new SpinnerItem(name, true);
            spinnerItems.add(spinnerItem);
        }

        // track changes to tags
        Callback callback = new Callback() {
            public void run(String tag, Boolean isChecked) {
                if (isChecked) {
                    tags.add(tag);
                } else {
                    tags.remove(tag);
                }
                String[] array = tags.toArray(new String[tags.size()]);
                updateTags(inflater, tags);
                fileManager.setProcessed(dbManager, array);
                fileManager.setFolders(dbManager, array);
                filesList = fileManager.getAll(true, true);
                arrayAdapter.notifyDataSetChanged();
            }
        };

        CustomAdapter customAdapter = new CustomAdapter(getActivity(), 0, spinnerItems, callback);
        binding.spinner.setAdapter(customAdapter);

        // add the "New" tag
        updateTags(inflater, tags);

        // set the files/folders list
        String[] array = tags.toArray(new String[tags.size()]);
        fileManager.setProcessed(dbManager, array);
        fileManager.setFolders(dbManager, array);
        filesList = fileManager.getAll(true, true);
        arrayAdapter = new FilesAdapter(getActivity(), filesList);
        binding.medialist.setAdapter(arrayAdapter);
        binding.medialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                MyFile myFile = fileManager.getAll(true, true).get(pos);

                if (myFile.getIsNew()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", pos);
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_ProcessFragment, bundle);
                } else if (myFile.getIsFolder()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("myIdx", pos);
                    bundle.putString("listName", myFile.toString());
                    NavHostFragment.findNavController(HomeFragment.this)
                            .navigate(R.id.action_HomeFragment_to_ListFragment, bundle);
                } else {
                    Uri uri = Uri.parse(myFile.getAbsolutePath());
                    mediaPlayer = MediaPlayer.create(getActivity(), uri);
                    try {
                        // no need to call prepare
                        // https://stackoverflow.com/questions/35079310/mediaplayer-prepare-showing-illegal-state-exception
                        // mediaPlayer.prepare();
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
            }
        });

        binding.medialist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                MyFile myFile = fileManager.getAll(true, true).get(pos);

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

    public void updateTags(LayoutInflater inflater, ArrayList<Object> tags) {
        binding.tagholder.removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            TextView tag = (TextView)inflater.inflate(R.layout.tag, null);
            String name = tags.get(i).toString();
            if (name != "Tags") {
                tag.setText(name);
                binding.tagholder.addView(tag);
            }
        }
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