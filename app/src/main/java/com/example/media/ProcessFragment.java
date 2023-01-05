package com.example.media;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.media.databinding.FragmentProcessBinding;
import com.google.android.flexbox.FlexboxLayout;
import java.io.File;
import java.util.ArrayList;


public class ProcessFragment extends Fragment {

    private FragmentProcessBinding binding;
    public ArrayAdapter tagsAdapter;
    public ArrayAdapter listsAdapter;
    private MainReceiver receiver;
    private DBManager dbManager;
    private LayoutInflater layoutInflater;
    private MyFile myFile;
    private FileManager fileManager;
    private ArrayList<Object> listsList;
    private ArrayList<Object> tagsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        layoutInflater = inflater;
        receiver = (MainReceiver) getActivity();
        fileManager = receiver.getFileManager();
        binding = FragmentProcessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }

        Integer idx = getArguments().getInt("myIdx");
        myFile = fileManager.getAll(true, true).get(idx);
        binding.processName.setText(myFile.getName());
        binding.processName.setEnabled(false);

        if (dbManager == null) {
            dbManager = receiver.getDBManager();
        }

        tagsList = dbManager.fetch("SELECT * FROM tags", null, "name");
        tagsList.add(0, "Add Tags...");
        listsList = dbManager.fetch("SELECT * FROM lists", null, "name");
        listsList.add(0, "Add Lists...");

        // make first spinner item a hint
        // https://stackoverflow.com/questions/37019941/how-to-add-a-hint-in-spinner-in-xml
        // set tags adapter
        tagsAdapter = new ArrayAdapter<Object>(getActivity(), R.layout.text_item, tagsList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                }
                return true;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView)super.getDropDownView(position, convertView, parent);
                textView.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return textView;
            }
        };
        binding.processTags.setAdapter(tagsAdapter);

        // set lists adapter
        listsAdapter = new ArrayAdapter<Object>(getActivity(), R.layout.text_item, listsList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                }
                return true;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView)super.getDropDownView(position, convertView, parent);
                textView.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return textView;
            }
        };
        binding.processLists.setAdapter(listsAdapter);

        // get name, tags, and lists
        String dataName = binding.processName.getText().toString();
        ArrayList<String> dataTags = new ArrayList();
        ArrayList<String> dataLists = new ArrayList();

        // add tags and lists to holders
        binding.processTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String name = tagsList.get(position).toString();
                if (dataTags.contains(name)) {
                    dataTags.remove(name);
                } else {
                    dataTags.add(name);
                }
                binding.processTags.setSelection(0);
                updateTags(dataTags, binding.tagholder);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.processLists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String name = listsList.get(position).toString();
                if (dataLists.contains(name)) {
                    dataLists.remove(name);
                } else {
                    dataLists.add(name);
                }
                binding.processLists.setSelection(0);
                updateTags(dataLists, binding.listholder);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // add
        binding.processSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // begin transactions
                    dbManager.beginTransaction();

                    // insert the file
                    ContentValues filesValues = new ContentValues();
                    filesValues.put("name", dataName);
                    long id = dbManager.insert("files", filesValues);

                    // insert filestags
                    if (dataTags.size() > 0) {
                        for (int i = 0; i < dataTags.size(); i++) {
                            ContentValues filesTagsValues = new ContentValues();
                            filesTagsValues.put("file", id);
                            filesTagsValues.put("tag", dataTags.get(i));
                            dbManager.insert("filestags", filesTagsValues);
                        }
                    }

                    // insert fileslists
                    if (dataLists.size() > 0) {
                        for (int i = 0; i < dataLists.size(); i++) {
                            ContentValues filesListsValues = new ContentValues();
                            filesListsValues.put("file", id);
                            filesListsValues.put("list", dataLists.get(i));
                            dbManager.insert("fileslists", filesListsValues);
                        }
                    }

                    String newPath = myFile.getParent() + "/processed/" + id + "." + myFile.ext;
                    myFile.renameTo(new File(newPath));
                    dbManager.commitTransaction();
                    fileManager.setNew();
                    fileManager.setProcessed(dbManager, null);
                    Toast.makeText(getContext(), "File processed!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(ProcessFragment.this).popBackStack();
                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error processing file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });
    }

    public void updateTags(ArrayList<String> list, FlexboxLayout holder) {
        holder.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            TextView tag = (TextView)layoutInflater.inflate(R.layout.tag, null);
            tag.setText(list.get(i));
            holder.addView(tag);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}