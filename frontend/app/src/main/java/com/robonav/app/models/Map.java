package com.robonav.app.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private final String id;                     // Unique ID for the map
    private final String name;                   // Map's name
    private final Coordinates coordinates;       // Coordinates of the map (top-left, top-right, etc.)
    private final String url;                    // URL of the map image

    // Constructor to initialize Map object from a JSON object
    public Map(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");                               // Retrieve map ID
        this.name = jsonObject.getString("name");                           // Retrieve map name
        this.coordinates = new Coordinates(jsonObject.getJSONObject("coordinates"));  // Parse coordinates
        this.url = jsonObject.getString("url");                             // Retrieve map image URL
    }

    // Helper class to represent coordinates for better clarity
    public static class Coordinates {
        private final String topLeft;
        private final String topRight;
        private final String bottomLeft;
        private final String bottomRight;

        // Constructor to initialize coordinates from JSON object
        public Coordinates(JSONObject coordinatesJson) throws JSONException {
            this.topLeft = coordinatesJson.getString("top_left");
            this.topRight = coordinatesJson.getString("top_right");
            this.bottomLeft = coordinatesJson.getString("bottom_left");
            this.bottomRight = coordinatesJson.getString("bottom_right");
        }

        // Getters for coordinates
        public String getTopLeft() {
            return topLeft;
        }

        public String getTopRight() {
            return topRight;
        }

        public String getBottomLeft() {
            return bottomLeft;
        }

        public String getBottomRight() {
            return bottomRight;
        }

        // Convert coordinates to double values for bounds calculations
        public double getXMin() {
            return Double.parseDouble(bottomLeft.split(",")[0]);
        }

        public double getXMax() {
            return Double.parseDouble(topRight.split(",")[0]);
        }

        public double getYMin() {
            return Double.parseDouble(bottomLeft.split(",")[1]);
        }

        public double getYMax() {
            return Double.parseDouble(topLeft.split(",")[1]);
        }
    }

    // Getter methods for the class variables
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getUrl() {
        return url;
    }

    // Override toString for better debugging output
    @NonNull
    @Override
    public String toString() {
        return "Map{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", url='" + url + '\'' +
                '}';
    }

    // Utility method to convert a JSON array of maps to a List of Map objects
    public static List<Map> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Map> mapList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject mapObject = jsonArray.getJSONObject(i);
            Map map = new Map(mapObject);
            mapList.add(map);
        }
        return mapList;
    }
}
