package com.example.media;

import android.content.ContentValues;
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
import com.example.media.databinding.FragmentEditlistBinding;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class EditListFragment extends Fragment {

    private FragmentEditlistBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private LayoutInflater layoutInflater;
    private FileManager fileManager;
    private String curName;
    private ArrayList<Object> tagsList;
    private ArrayAdapter tagsAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        layoutInflater = inflater;
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        fileManager = receiver.getFileManager();
        binding = FragmentEditlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        curName = getArguments().getString("listName");
        binding.name.setText(curName);

        if (dbManager == null) {
            dbManager = receiver.getDBManager();
        }

        // add the spinners with all tags
        tagsList = dbManager.fetch("SELECT * FROM tags", null, "name");
        tagsList.add(0, "Add Tag...");
        tagsAdapter = new ArrayAdapter<>(getActivity(), R.layout.text_item, tagsList);
        binding.editlistTags.setAdapter(tagsAdapter);

        // get the data for the holder
        String args[] = { curName };
        ArrayList<Object> dataTags = dbManager.fetch("SELECT * FROM liststags WHERE list = ?", args, "list");

        // update the holder
        updateTags(layoutInflater, binding.tagholder, dataTags);

        // and/remove to/from holder
        binding.editlistTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                binding.editlistTags.setSelection(0);
                updateTags(layoutInflater, binding.tagholder, dataTags);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // rename
        binding.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbManager.beginTransaction();
                    // get the name and args
                    String name = binding.name.getText().toString().trim();
                    String args[] = { name, curName };

                    // update lists
                    String query1 = "UPDATE lists SET name = ? WHERE name = ?";
                    dbManager.exec(query1, args);

                    // update fileslists
                    String query2 = "UPDATE fileslists SET list = ? WHERE list = ?";
                    dbManager.exec(query2, args);

                    // update liststags
                    String query3 = "UPDATE liststags SET list = ? WHERE list = ?";
                    dbManager.exec(query3, args);

                    dbManager.commitTransaction();
                    curName = name;
                    // https://stackoverflow.com/questions/36461022/settitle-doesnt-work-in-fragment
                    // should probably just use the receiver
                    ((MainActivity)getActivity()).setActionBarTitle("Edit List - " + curName);
                    Toast.makeText(getContext(), "List renamed!", Toast.LENGTH_SHORT).show();
                    String[] array = dataTags.toArray(new String[dataTags.size()]);
                    fileManager.setFolders(dbManager, array);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error renaming list", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });

        // save tags
        binding.editlistAddtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbManager.beginTransaction();

                    // delete the rows from liststags
                    String[] args = { curName };
                    dbManager.exec("DELETE FROM liststags WHERE list = ?", args);

                    // update the tags and lists associations
                    for (int i = 0; i < dataTags.size(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("list", curName);
                        values.put("tag", dataTags.get(i).toString());
                        dbManager.insert("liststags", values);
                    }

                    dbManager.commitTransaction();
                    Toast.makeText(getContext(), "Tags saved!", Toast.LENGTH_SHORT).show();
                    String[] array = dataTags.toArray(new String[dataTags.size()]);
                    fileManager.setFolders(dbManager, array);
                } catch(Exception e) {
                    Toast.makeText(getContext(), "Error editing list", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });

        // delete
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbManager.beginTransaction();
                    // get the args
                    String args[] = { curName };

                    // delete from lists
                    String query1 = "DELETE FROM lists WHERE name = ?";
                    dbManager.exec(query1, args);

                    // update fileslists
                    String query2 = "UPDATE fileslists SET list = NULL WHERE list = ?";
                    dbManager.exec(query2, args);

                    // update liststags
                    String query3 = "UPDATE liststags SET list = NULL WHERE list = ?";
                    dbManager.exec(query3, args);

                    dbManager.commitTransaction();
                    String[] array = dataTags.toArray(new String[dataTags.size()]);
                    fileManager.setFolders(dbManager, array);
                    NavHostFragment.findNavController(EditListFragment.this).popBackStack();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error deleting list", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });
    }

    private void updateTags(LayoutInflater inflater, FlexboxLayout holder, ArrayList<Object> tags) {
        holder.removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            TextView tag = (TextView)inflater.inflate(R.layout.tag, null);
            String name = tags.get(i).toString();
            if (name != "Add Tag...") {
                tag.setText(name);
                holder.addView(tag);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}