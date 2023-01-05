package com.example.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.media.databinding.FragmentEditlistBinding;

import java.io.File;

public class EditListFragment extends Fragment {

    private FragmentEditlistBinding binding;
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
        binding = FragmentEditlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        curName = getArguments().getString("listName");
        binding.name.setText(curName);

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
                    ((MainActivity)getActivity()).setActionBarTitle("Edit List - " + name);
                    Toast.makeText(getContext(), "List renamed!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error renaming list", Toast.LENGTH_SHORT).show();
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
                    String query1 = "DELETE FROM lists WHERE name = ?";
                    dbManager.exec(query1, args);

                    // update fileslists
                    String query2 = "UPDATE fileslists SET list = NULL WHERE list = ?";
                    dbManager.exec(query2, args);

                    // update liststags
                    String query3 = "UPDATE liststags SET list = NULL WHERE list = ?";
                    dbManager.exec(query3, args);

                    dbManager.commitTransaction();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}