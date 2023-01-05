package com.example.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.media.databinding.FragmentEdittagBinding;

public class EditTagFragment extends Fragment {

    private FragmentEdittagBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;
    private FileManager fileManager;
    private String curName;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        receiver = (MainReceiver) getActivity();
        dbManager = receiver.getDBManager();
        fileManager = receiver.getFileManager();
        binding = FragmentEdittagBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        curName = getArguments().getString("tagName");

        binding.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbManager.beginTransaction();
                    // get the name and args
                    String name = binding.name.getText().toString().trim();
                    String args[] = { name, curName };

                    // update tags
                    String query1 = "UPDATE tags SET name = ? WHERE name = ?";
                    dbManager.exec(query1, args);

                    // update filestags
                    String query2 = "UPDATE fileslists SET tag = ? WHERE tag = ?";
                    dbManager.exec(query2, args);

                    // update liststags
                    String query3 = "UPDATE liststags SET tag = ? WHERE tag = ?";
                    dbManager.exec(query3, args);

                    dbManager.commitTransaction();
                    getActivity().setTitle("Edit Tag - " + name);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error renaming tag", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbManager.beginTransaction();
                    // get the args
                    String args[] = { curName };

                    // delete from lists
                    String query1 = "DELETE FROM tags WHERE name = ?";
                    dbManager.exec(query1, args);

                    // update filestags
                    String query2 = "UPDATE fileslists SET tag = NULL WHERE tag = ?";
                    dbManager.exec(query2, args);

                    // update liststags
                    String query3 = "UPDATE liststags SET tag = NULL WHERE tag = ?";
                    dbManager.exec(query3, args);

                    dbManager.commitTransaction();
                    NavHostFragment.findNavController(EditTagFragment.this).popBackStack();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error deleting list", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    dbManager.endTransaction();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}