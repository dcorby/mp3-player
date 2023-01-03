package com.example.media;

import android.database.SQLException;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Environment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.media.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements MainReceiver {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public File newFiles[];
    public ArrayList<MyFile> newMyFiles;
    public DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get a list of new files
        String sdRoot = Environment.getExternalStorageDirectory().toString();
        String appRoot = sdRoot + "/Android/data/com.example.media/files";
        File mediaFolder = new File(appRoot);
        File tmp[] = mediaFolder.listFiles();
        newFiles = mediaFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        newMyFiles = new ArrayList<MyFile>();
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

        try {
            initDBManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDBManager() throws SQLException {
        dbManager = new DBManager(this);
        dbManager.open();
    }

    // interface methods
    @Override
    public ArrayList<MyFile> getNewMyFiles() {
        return newMyFiles;
    }
    @Override
    public DBManager getDBManager() {
        return dbManager;
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
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        //return super.onOptionsItemSelected(item);

        // https://developer.android.com/guide/navigation/navigation-ui
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);

        //NavHostFragment.findNavController(ProcessFragment.this)
        //        .navigate(R.id.action_ProcessFragment_to_HomeFragment);
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