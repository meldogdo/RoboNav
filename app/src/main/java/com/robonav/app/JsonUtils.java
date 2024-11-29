package com.robonav.app;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtils {

    // Reads JSON file from the assets folder
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static void saveJSONToFile(Context context, String fileName, String jsonData) throws IOException {
        // Open a file in the app's internal storage
        FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        try {
            // Write the JSON string to the file
            fileOutputStream.write(jsonData.getBytes());
        } finally {
            // Close the file output stream
            fileOutputStream.close();
        }
    }
    // Loads JSON data from a file in the app's internal storage
    public static String loadJSONFromFile(Context context, String fileName) throws IOException {
        FileInputStream fileInputStream = context.openFileInput(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        reader.close();
        return jsonBuilder.toString();
    }
}