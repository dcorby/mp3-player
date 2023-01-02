package com.example.media;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.media.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.media.Utils;
import com.example.media.MainReceiver;

//import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.media.MyFile;


public class MainActivity extends AppCompatActivity implements MainReceiver {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public File newFiles[];
    public ArrayList<MyFile> newMyFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get a list of new files
        //mediaList = new ArrayList<String>();
        String sdRoot = Environment.getExternalStorageDirectory().toString();
        String appRoot = sdRoot + "/Android/data/com.example.media/files";
        File mediaFolder = new File(appRoot);
        File tmp[] = mediaFolder.listFiles();
        newFiles = mediaFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        for (int i = 0; i < newFiles.length; i++) {
            newMyFiles.add(new MyFile(newFiles[i].getAbsolutePath(), true));
        }
        newFiles = null;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



        //setListAdapter(new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, myList ));

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // interface methods
    @Override
    public ArrayList<MyFile> getNewMyFiles() {
        return newMyFiles;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void Toast(String errorStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "Error: " + errorStr,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}