package com.robonav.app;

import static com.robonav.app.JsonUtils.saveJSONToFile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private Spinner actionSpinner;
    private View dynamicContentContainer;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        actionSpinner = view.findViewById(R.id.action_spinner);
        dynamicContentContainer = view.findViewById(R.id.dynamic_content_container);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap map) {
                    googleMap = map;
                }
            });
        }

        setupDropdownMenu();
        return view;
    }

    private void setupDropdownMenu() {
        String[] actions = {
                "Check Position",
                "Set Initial Position",
                "Save Current Location",
                "Remove All Locations",
                "Get Location by Name",
                "Get All Locations",
                "Get Current Map",
                "Swap Map",
                "View Map File"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, actions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(adapter);

        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleActionSelection(actions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Clear dynamic content if nothing is selected
                clearDynamicContent();
            }
        });
    }

    private void handleActionSelection(String action) {
        clearDynamicContent();
        Spinner robotDropdown;
        switch (action) {

            case "Check Position":
                inflateContent(R.layout.dynamic_check_position);
                break;
            case "Set Initial Position":
                inflateContent(R.layout.dynamic_set_position);

                // Initialize components
                robotDropdown = dynamicContentContainer.findViewById(R.id.robot_dropdown);
                EditText inputCoordinates = dynamicContentContainer.findViewById(R.id.input_coordinates);
                Button btnSetPosition = dynamicContentContainer.findViewById(R.id.btn_set_initial_position);

                // Load robot names into the dropdown
                List<String> robotNames = loadRobotNames();
                if (robotNames.isEmpty()) {
                    showMessage("No robots found in the system.");
                    return;
                }

                ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
                robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                robotDropdown.setAdapter(robotAdapter);

                Spinner finalRobotDropdown = robotDropdown;
                btnSetPosition.setOnClickListener(v -> {
                    String selectedRobot = finalRobotDropdown.getSelectedItem() != null ? finalRobotDropdown.getSelectedItem().toString() : "";
                    String coordinates = inputCoordinates.getText().toString();

                    if (coordinates.isEmpty() || selectedRobot.isEmpty()) {
                        showMessage("Please select a robot and provide coordinates.");
                        return;
                    }

                    handleSetPosition(selectedRobot, coordinates);
                });
                break;

            case "View Map File":
                inflateContent(R.layout.dynamic_view_map_file);
                break;

            case "Save Current Location":
                inflateContent(R.layout.dynamic_save_location);

                // Initialize components
                robotDropdown = dynamicContentContainer.findViewById(R.id.robot_dropdown);
                TextView robotLocationName = dynamicContentContainer.findViewById(R.id.robot_location_name);
                Button btnSaveLocation = dynamicContentContainer.findViewById(R.id.btn_save_location);

                // Load robots with non-empty location fields
                List<Robot> robotsWithLocations = loadRobotsWithLocations();
                robotNames = new ArrayList<>();
                for (Robot robot : robotsWithLocations) {
                    robotNames.add(robot.getName());
                }

                // Populate the dropdown
                robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
                robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                robotDropdown.setAdapter(robotAdapter);

                // Handle robot selection
                robotDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedRobot = robotDropdown.getSelectedItem().toString();
                        Robot selectedRobotObj = findRobotByName(selectedRobot, robotsWithLocations);

                        if (selectedRobotObj != null) {
                            // Update the location name dynamically
                            String location = selectedRobotObj.getLocation();
                            if (!location.isEmpty()) {
                                robotLocationName.setText("Location Name: " + location);
                            } else {
                                robotLocationName.setText("No location found for this robot.");
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        robotLocationName.setText("");
                    }
                });

                // Handle Save Location button click
                btnSaveLocation.setOnClickListener(v -> {
                    String selectedRobot = robotDropdown.getSelectedItem() != null ? robotDropdown.getSelectedItem().toString() : "";

                    if (selectedRobot.isEmpty()) {
                        showMessage("Please select a robot.");
                        return;
                    }

                    Robot selectedRobotObj = findRobotByName(selectedRobot, robotsWithLocations);
                    if (selectedRobotObj != null && !selectedRobotObj.getLocation().isEmpty()) {
                        saveLocation(selectedRobotObj);
                    } else {
                        showMessage("No valid location found for the selected robot.");
                    }
                });
                break;
            case "Get All Locations":
                inflateContent(R.layout.dynamic_get_all_locations);

                // Initialize the output TextView
                TextView allLocationsTextView = dynamicContentContainer.findViewById(R.id.all_locations_text_view);

                try {
                    // Load existing locations
                    File file = new File(requireContext().getFilesDir(), "locations.json");
                    if (!file.exists()) {
                        allLocationsTextView.setText("No locations found.");
                        return;
                    }

                    String locationJson = JsonUtils.loadJSONFromFile(requireContext(), "locations.json");
                    JSONArray locations = new JSONArray(locationJson);

                    // Extract location names
                    StringBuilder locationNames = new StringBuilder();
                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject location = locations.getJSONObject(i);
                        locationNames.append(location.getString("name")).append("\n");
                    }

                    // Display the location names
                    allLocationsTextView.setText(locationNames.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    allLocationsTextView.setText("Error retrieving locations: " + e.getMessage());
                }
                break;
            case "Remove All Locations":
                inflateContent(R.layout.dynamic_remove_all_locations);

                // Initialize button
                Button btnRemoveAllLocations = dynamicContentContainer.findViewById(R.id.btn_remove_all_locations);

                // Handle button click
                btnRemoveAllLocations.setOnClickListener(v -> {
                    try {
                        // Load existing robots.json
                        String robotsJson = JsonUtils.loadJSONFromAsset(requireContext(), "robots.json");
                        JSONArray robots = new JSONArray(robotsJson);

                        // Update each robot's location to an empty string
                        for (int i = 0; i < robots.length(); i++) {
                            JSONObject robot = robots.getJSONObject(i);
                            robot.put("location", "");
                        }

                        // Save the updated robots.json
                        JsonUtils.saveJSONToFile(requireContext(), "robots.json", robots.toString());

                        // Notify user
                        showMessage("All robot locations have been cleared.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("Error clearing locations: " + e.getMessage());
                    }
                });
                break;


            default:
                break;
        }
    }

    private void handleSetPosition(String selectedRobot, String coordinates) {
        String[] latLng = coordinates.split(",");
        if (latLng.length != 2) {
            showMessage("Invalid coordinates. Please enter in 'Latitude, Longitude' format.");
            return;
        }

        try {
            double latitude = Double.parseDouble(latLng[0].trim());
            double longitude = Double.parseDouble(latLng[1].trim());
            LatLng position = new LatLng(latitude, longitude);

            if (googleMap != null) {
                googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(selectedRobot + "'s Checkpoint")
                        .snippet("Initial Position Set"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
            }

            appendOutput("Initial position set for " + selectedRobot + " at coordinates: " + coordinates);

        } catch (NumberFormatException e) {
            showMessage("Invalid coordinates. Please enter numeric values for Latitude and Longitude.");
        }
    }

    private List<String> loadRobotNames() {
        List<String> robotNames = new ArrayList<>();
        String robotJson = JsonUtils.loadJSONFromAsset(requireContext(), "robots.json");
        try {
            JSONArray robots = new JSONArray(robotJson);
            for (int i = 0; i < robots.length(); i++) {
                Robot robot = new Robot(robots.getJSONObject(i));
                robotNames.add(robot.getName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading robots: " + e.getMessage());
        }
        return robotNames;
    }

    private void clearDynamicContent() {
        ((ViewGroup) dynamicContentContainer).removeAllViews();
    }

    private void inflateContent(int layoutResId) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(layoutResId, (ViewGroup) dynamicContentContainer, false);
        ((ViewGroup) dynamicContentContainer).addView(view);
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void appendOutput(String message) {
        // Assuming an output box exists in your layout
        View rootView = getView();
        if (rootView != null) {
            TextView outputBox = rootView.findViewById(R.id.output_text_view);
            String currentOutput = outputBox.getText().toString();
            outputBox.setText(currentOutput + "\n" + message);
        }
    }
    private List<Robot> loadRobotsWithLocations() {
        List<Robot> robotsWithLocations = new ArrayList<>();
        String robotJson = JsonUtils.loadJSONFromAsset(requireContext(), "robots.json");
        try {
            JSONArray robots = new JSONArray(robotJson);
            for (int i = 0; i < robots.length(); i++) {
                Robot robot = new Robot(robots.getJSONObject(i));
                if (!robot.getLocation().isEmpty()) {
                    robotsWithLocations.add(robot);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading robots: " + e.getMessage());
        }
        return robotsWithLocations;
    }
    private Robot findRobotByName(String name, List<Robot> robots) {
        for (Robot robot : robots) {
            if (robot.getName().equals(name)) {
                return robot;
            }
        }
        return null;
    }
    private void saveLocation(Robot robot) {
        try {
            // Step 1: Load existing locations.json
            String locationJson;
            File file = new File(requireContext().getFilesDir(), "locations.json");
            JSONArray locations;

            if (file.exists()) {
                locationJson = JsonUtils.loadJSONFromFile(requireContext(), "locations.json");
                locations = new JSONArray(locationJson);
            } else {
                locations = new JSONArray();
            }

            // Step 2: Create new location object
            JSONObject newLocation = new JSONObject();
            newLocation.put("name", "Checkpoint for " + robot.getName());
            newLocation.put("coordinates", robot.getLocation());
            newLocation.put("robots", new JSONArray().put(robot.getId()));

            // Step 3: Add the new location to the array
            locations.put(newLocation);

            // Step 4: Save updated JSON array back to locations.json
            JsonUtils.saveJSONToFile(requireContext(), "locations.json", locations.toString());

            // Step 5: Notify user and update UI
            showMessage("Location saved successfully.");
            appendOutput("Saved location for " + robot.getName() + " at coordinates: " + robot.getLocation());
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error saving location: " + e.getMessage());
        }
    }

}
