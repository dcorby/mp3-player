package com.example.media;

import android.app.Activity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static String readJson(File jsonFile) throws IOException {
        FileReader fileReader = new FileReader(jsonFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        String jsonStr = stringBuilder.toString();
        return jsonStr;
    }

    public static Map<String, String> parseJson(String jsonStr) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("foo", "bar");
        return map;
    }

    /*
    public static void Toast(String errorStr) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(),
                        "Error: " + errorStr,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    */
}
