package com.example.media;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.media.databinding.FragmentAddtagBinding;

public class AddTagFragment extends Fragment {

    private FragmentAddtagBinding binding;
    private MainReceiver receiver;
    private DBManager dbManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        receiver = (MainReceiver) getActivity();
        binding = FragmentAddtagBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    insertName();
                    return true;
                }
                return false;
            }
        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertName();
            }
        });
    }

    private void insertName() {
        try {
            String name = binding.name.getText().toString().trim();
            if (name.length() == 0) {
                Toast.makeText(getContext(), "Add a value", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbManager == null) {
                dbManager = receiver.getDBManager("Tags");
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            try {
                dbManager.insert(contentValues);
                Toast.makeText(getContext(), "Tag added!", Toast.LENGTH_SHORT).show();
            } catch(SQLiteConstraintException e) {
                Toast.makeText(getContext(), "Tag already exists!", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            Toast.makeText(getContext(), "Error adding tag", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}