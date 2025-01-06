package com.robonav.app.utilities;


import static com.robonav.app.utilities.FragmentUtils.showMessage;

import android.content.Context;

import com.robonav.app.models.Robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static void saveJSONToFile(Context context, String fileName, String jsonData) throws IOException {
        // Open a file in the app's internal storage
        try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            // Write the JSON string to the file
            fileOutputStream.write(jsonData.getBytes());
        }
        // Close the file output stream
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

    public static List<String> loadRobotNames(Context context) {
        List<String> robotNames = new ArrayList<>();
        String robotJson = JsonUtils.loadJSONFromAsset(context, "robots.json");
        try {
            JSONArray robots = new JSONArray(robotJson);
            for (int i = 0; i < robots.length(); i++) {
                Robot robot = new Robot(robots.getJSONObject(i));
                robotNames.add(robot.getName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading robots: " + e.getMessage(),context);
        }
        return robotNames;
    }
    // Helper method to convert JSON array of tasks to List<String>
    public static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> taskList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                taskList.add(jsonArray.getString(i));
            }
        }
        return taskList;
    }

    public static List<Robot> loadRobotsWithLocations(Context context) {
        List<Robot> robotsWithLocations = new ArrayList<>();
        String robotJson = loadJSONFromAsset(context, "robots.json");
        try {
            JSONArray robots = new JSONArray(robotJson);
            for (int i = 0; i < robots.length(); i++) {
                Robot robot = new Robot(robots.getJSONObject(i));
                if (!robot.getLocationName().isEmpty()) {
                    robotsWithLocations.add(robot);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading robots: " + e.getMessage(),context);
        }
        return robotsWithLocations;
    }



    public static List<Robot> loadAllRobots(Context context) {
        List<Robot> robots = new ArrayList<>();
        try {
            String robotsJson = JsonUtils.loadJSONFromAsset(context, "robots.json");
            JSONArray robotsArray = new JSONArray(robotsJson);
            for (int i = 0; i < robotsArray.length(); i++) {
                robots.add(new Robot(robotsArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error loading robots: " + e.getMessage(),context);
        }
        return robots;
    }
    public static List<String> loadLocationNames(Context context) {
        List<String> locationNames = new ArrayList<>();
        try {
            String locationJson = JsonUtils.loadJSONFromAsset(context, "locations.json");
            JSONArray locationsArray = new JSONArray(locationJson);
            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject location = locationsArray.getJSONObject(i);
                locationNames.add(location.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading locations: " + e.getMessage(),context);
        }
        return locationNames;
    }

    public static String getCoordinatesForLocation(String locationName, Context context) {
        try {
            String locationJson = JsonUtils.loadJSONFromAsset(context, "locations.json");
            JSONArray locationsArray = new JSONArray(locationJson);
            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject location = locationsArray.getJSONObject(i);
                if (location.getString("name").equals(locationName)) {
                    return location.getString("coordinates");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static List<String> getAllLocations(Context context) {
        List<String> locations = new ArrayList<>();
        try {
            String locationJson = JsonUtils.loadJSONFromAsset(context, "locations.json");
            JSONArray locationsArray = new JSONArray(locationJson);
            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject location = locationsArray.getJSONObject(i);
                String name = location.getString("name");
                String coordinates = location.getString("coordinates");
                locations.add(name + " (Coordinates: " + coordinates + ")");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error retrieving locations: " + e.getMessage(),context);
        }
        return locations;
    }

    public static List<String> getAvailableMapFiles() {
        // Simulate a list of map files
        List<String> mapFiles = new ArrayList<>();
        mapFiles.add("map_file_1.json");
        mapFiles.add("map_file_2.json");
        mapFiles.add("map_file_3.json");
        return mapFiles;

        // You can replace this with actual file browsing logic if needed
    }
}