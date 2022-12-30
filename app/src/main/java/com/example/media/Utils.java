package com.example.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import java.lang.reflect.Field;

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

    public static Map<String, String> parseJson(String jsonStr) throws NoSuchFieldException, IllegalAccessException  {
        class Data {
            public String type;
            public String artist;
            public String title;
        }
        Gson gson = new Gson();
        Data dateObj = gson.fromJson(jsonStr, Data.class);
        Field fields[] = Data.class.getDeclaredFields();
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
             // https://stackoverflow.com/questions/18044978/java-get-property-value-by-property-name
            String val = dateObj.getClass().getField(map.get(key)).get(dateObj).toString();
            map.put(key, val);
        }

        return map;
    }
}
