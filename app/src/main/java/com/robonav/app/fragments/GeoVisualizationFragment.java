//package com.robonav.app.fragments;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.robonav.app.R;
//import com.robonav.app.models.Robot;
//import com.robonav.app.utilities.JsonUtils;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GeoVisualizationFragment extends Fragment {
//
//    private FrameLayout mapContainer;
//    private TextView botDetailsTextView;
//
//    // Map boundaries (example coordinates)
//    private static final double MAP_LATITUDE_MIN = -90.0;   // Southernmost latitude
//    private static final double MAP_LATITUDE_MAX = 90.0;    // Northernmost latitude
//    private static final double MAP_LONGITUDE_MIN = -180.0; // Westernmost longitude
//    private static final double MAP_LONGITUDE_MAX = 180.0;  // Easternmost longitude
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_visualization, container, false);
//
//        // Initialize views
//        mapContainer = rootView.findViewById(R.id.mapContainer);
//        botDetailsTextView = rootView.findViewById(R.id.botDetailsContainer);
//
//        // Load bots and add them dynamically to the map
//        loadAndAddBots();
//
//        return rootView;
//    }
//
//    private void loadAndAddBots() {
//        String robotJson = JsonUtils.loadJSONFromAsset(requireContext(), "robots.json");
//        List<Robot> robotList = new ArrayList<>();
//
//        try {
//            JSONArray robots = new JSONArray(robotJson);
//            for (int i = 0; i < robots.length(); i++) {
//                robotList.add(new Robot(robots.getJSONObject(i)));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // Dynamically add each bot to the map
//        for (Robot robot : robotList) {
//            addRobotToMap(robot);
//        }
//    }
//
//    private void addRobotToMap(Robot robot) {
//        // Ensure the map layout is measured first
//        mapContainer.post(new Runnable() {
//            @Override
//            public void run() {
//                // Create a new ImageView for the bot
//                ImageView botView = new ImageView(requireContext());
//                botView.setImageResource(R.drawable.bot); // Use the correct mipmap resource
//                botView.setContentDescription(robot.getName()); // Set content description for accessibility
//
//                // Get the robot's geographic coordinates (latitude, longitude)
//                String[] coordinates = robot.getLocationCoordinates().split(",");
//                if (coordinates.length != 2) {
//                    Log.e("GeoVisualization", "Invalid coordinates for robot: " + robot.getName());
//                    return;
//                }
//
//                try {
//                    double latitude = Double.parseDouble(coordinates[0]);
//                    double longitude = Double.parseDouble(coordinates[1]);
//
//                    // Log the coordinates for debugging
//                    Log.d("GeoVisualization", "Coordinates for " + robot.getName() + ": " + latitude + ", " + longitude);
//
//                    // Calculate the robot's position on the map (based on coordinates)
//                    int mapWidth = mapContainer.getWidth();  // Get the width of the map container
//                    int mapHeight = mapContainer.getHeight(); // Get the height of the map container
//
//                    // Check if map container dimensions are valid
//                    if (mapWidth == 0 || mapHeight == 0) {
//                        Log.e("GeoVisualization", "Map container width or height is zero.");
//                        return;
//                    }
//
//                    // Convert latitude and longitude into pixel coordinates (simple proportional scaling example)
//                    int xPosition = (int) ((longitude - MAP_LONGITUDE_MIN) * mapWidth / (MAP_LONGITUDE_MAX - MAP_LONGITUDE_MIN)); // Longitude mapped to horizontal position
//                    int yPosition = (int) ((MAP_LATITUDE_MAX - latitude) * mapHeight / (MAP_LATITUDE_MAX - MAP_LATITUDE_MIN));   // Latitude mapped to vertical position
//
//                    // Log the calculated positions for debugging
//                    Log.d("GeoVisualization", "Position for " + robot.getName() + ": x=" + xPosition + ", y=" + yPosition);
//
//                    // Set the calculated position for the bot image
//                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                            100, // Width (e.g., 100px)
//                            100  // Height (e.g., 100px)
//                    );
//                    params.leftMargin = xPosition; // X-coordinate
//                    params.topMargin = yPosition;  // Y-coordinate
//
//                    botView.setLayoutParams(params);
//
//                    // Add click listener to update bot details
//                    botView.setOnClickListener(v -> botDetailsTextView.setText(
//                            String.format("Bot: %s\nLocation: %s\nPing: %s\nBattery: %d%%",
//                                    robot.getName(), robot.getLocationName(), robot.getPing(), robot.getBattery())
//                    ));
//
//                    // Add the bot to the map container
//                    mapContainer.addView(botView);
//
//                } catch (NumberFormatException e) {
//                    Log.e("GeoVisualization", "Error parsing coordinates for robot: " + robot.getName(), e);
//                }
//            }
//        });
//    }
//}
