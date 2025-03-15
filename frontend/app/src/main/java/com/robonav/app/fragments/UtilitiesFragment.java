package com.robonav.app.fragments;

import static com.robonav.app.models.Robot.findRobotByName;
import static com.robonav.app.utilities.FragmentUtils.appendOutput;
import static com.robonav.app.utilities.FragmentUtils.isValidCoordinates;
import static com.robonav.app.utilities.FragmentUtils.saveLocation;
import static com.robonav.app.utilities.FragmentUtils.showMessage;
import static com.robonav.app.utilities.JsonUtils.loadAllRobots;
import static com.robonav.app.utilities.JsonUtils.loadRobotsWithLocations;
import static com.robonav.app.utilities.JsonUtils.loadLocationNames;
import static com.robonav.app.utilities.JsonUtils.getCoordinatesForLocation;
import static com.robonav.app.utilities.JsonUtils.getAllLocations;

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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;


import com.robonav.app.utilities.JsonUtils;
import com.robonav.app.R;
import com.robonav.app.models.Robot;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UtilitiesFragment extends Fragment {

    private Spinner actionSpinner;
    private View dynamicContentContainer;

    private NestedScrollView scrollView;
    private View view;
    private Toast currentToast; // Store the latest toast reference


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_utilities, container, false);

        scrollView = view.findViewById(R.id.output_scroll_view);
        actionSpinner = view.findViewById(R.id.action_spinner);
        dynamicContentContainer = view.findViewById(R.id.dynamic_content_container);

        setupDropdownMenu();
        return view;
    }

    private void setupDropdownMenu() {
        String[] actions = {
                "Check Robot Position",
                "Save Current Location",
                "Remove All Locations",
                "Get Location by Name",
                "Get All Locations",
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
        List<String> robotNames = new ArrayList<>();
        ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
        List<String> locationNames = loadLocationNames(requireContext());
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNames);
        switch (action) {

            case "Check Robot Position":
                inflateContent(R.layout.dynamic_check_position);

                // Initialize components
                robotDropdown = dynamicContentContainer.findViewById(R.id.robot_dropdown);
                TextView locationNameTextView = dynamicContentContainer.findViewById(R.id.robot_location_name);
                TextView locationCoordinatesTextView = dynamicContentContainer.findViewById(R.id.robot_location_coordinates);
                Button btnCheckPosition = dynamicContentContainer.findViewById(R.id.btn_check_position);

                // Load robot names into the dropdown
                List<Robot> allRobots = loadAllRobots(requireContext());

                for (Robot robot : allRobots) {
                    robotNames.add(robot.getName());
                }

                robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                robotDropdown.setAdapter(robotAdapter);

                // Dropdown selection listener to display robot location
                robotDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedRobotName = robotDropdown.getSelectedItem().toString();
                        Robot selectedRobot = findRobotByName(selectedRobotName, allRobots);

                        if (selectedRobot != null) {
                            // Display location details
                            locationNameTextView.setText("Location Name: " + selectedRobot.getLocationName());
                            locationCoordinatesTextView.setText("Coordinates: " + selectedRobot.getLocationCoordinates());
                        } else {
                            locationNameTextView.setText("Location Name: Not found");
                            locationCoordinatesTextView.setText("Coordinates: Not found");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        locationNameTextView.setText("Location Name: ");
                        locationCoordinatesTextView.setText("Coordinates: ");
                    }
                });

                // Button to confirm checking position
                btnCheckPosition.setOnClickListener(v -> {
                    String selectedRobotName = robotDropdown.getSelectedItem().toString();
                    Robot selectedRobot = findRobotByName(selectedRobotName, allRobots);

                    if (selectedRobot != null) {
                        appendOutput("Checked Position for " + selectedRobot.getName() +
                                "\nLocation Name: " + selectedRobot.getLocationName() +
                                "\nCoordinates: " + selectedRobot.getLocationCoordinates(), scrollView, view);
                    } else {
                        showMessage("Please select a robot with a valid location.", requireContext());
                    }
                });
                break;
            case "Save Current Location":
                inflateContent(R.layout.dynamic_save_location);

                // Initialize components
                robotDropdown = dynamicContentContainer.findViewById(R.id.robot_dropdown);
                EditText inputLocationName = dynamicContentContainer.findViewById(R.id.input_location_name);
                Button btnSaveLocation = dynamicContentContainer.findViewById(R.id.btn_save_location);

                // Load robots with non-empty location fields
                List<Robot> robotsWithLocations = loadRobotsWithLocations(requireContext());
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
                            // Prefill the EditText with the current location name (if exists)
                            String location = selectedRobotObj.getLocationName();
                            if (!location.isEmpty()) {
                                inputLocationName.setText(location);
                            } else {
                                inputLocationName.setHint("Enter location name");
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        inputLocationName.setText("");
                        inputLocationName.setHint("Enter location name");
                    }
                });

                // Handle Save Location button click
                btnSaveLocation.setOnClickListener(v -> {
                    String selectedRobot = robotDropdown.getSelectedItem() != null ? robotDropdown.getSelectedItem().toString() : "";

                    if (selectedRobot.isEmpty()) {
                        showMessage("Please select a robot.",requireContext());
                        return;
                    }

                    String newLocationName = inputLocationName.getText().toString().trim();
                    if (newLocationName.isEmpty()) {
                        showMessage("Please enter a location name.",requireContext());
                        return;
                    }

                    // Find the robot and save the new location name
                    Robot selectedRobotObj = findRobotByName(selectedRobot, robotsWithLocations);
                    if (selectedRobotObj != null) {
                        selectedRobotObj.setLocationName(newLocationName);
                        saveLocation(selectedRobotObj,requireContext(), scrollView, view); // Your method to save or update the robot location
                        showMessage("Location saved successfully for " + selectedRobot,requireContext());
                    } else {
                        showMessage("Unable to find the selected robot.",requireContext());
                    }
                });
                break;

            case "Get All Locations":
                inflateContent(R.layout.dynamic_get_all_locations);

                // Initialize components
                Button btnShowAllLocations = dynamicContentContainer.findViewById(R.id.btn_show_all_locations);

                // Handle button click
                btnShowAllLocations.setOnClickListener(v -> {
                    List<String> allLocations = getAllLocations(requireContext());
                    if (allLocations.isEmpty()) {
                        appendOutput("No locations found.", scrollView, view);
                    } else {

                        StringBuilder output = new StringBuilder();
                        for (int i = 0; i < allLocations.size(); i++) {
                            // Append location
                            output.append(allLocations.get(i));

                            // If it's not the last location, add a newline
                            if (i < allLocations.size() - 1) {
                                output.append("\n");
                            }
                        }
                        appendOutput(String.valueOf(output), scrollView, view);
                    }
                });
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
                        showMessage("All robot locations have been cleared.",requireContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("Error clearing locations: " + e.getMessage(),requireContext());
                    }
                });
                break;

            case "Get Location by Name":
                inflateContent(R.layout.dynamic_get_location_by_name);

                // Initialize components
                Spinner locationDropdown = dynamicContentContainer.findViewById(R.id.location_dropdown);
                Button btnGetCoordinates = dynamicContentContainer.findViewById(R.id.btn_get_coordinates);

                // Load location names into dropdown
                locationNames = loadLocationNames(requireContext());
                locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNames);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locationDropdown.setAdapter(locationAdapter);

                // Handle Get Coordinates button click
                btnGetCoordinates.setOnClickListener(v -> {
                    String selectedLocation = locationDropdown.getSelectedItem() != null ? locationDropdown.getSelectedItem().toString() : "";

                    if (selectedLocation.isEmpty()) {
                        showMessage("Please select a location.",requireContext());
                        return;
                    }

                    // Retrieve and display coordinates for the selected location
                    String coordinates = getCoordinatesForLocation(selectedLocation, requireContext());
                    if (!coordinates.isEmpty()) {
                        appendOutput("Coordinates: " + coordinates, scrollView, view);
                    } else {
                        appendOutput("Coordinates not found for the selected location.", scrollView, view);
                    }
                });
                break;
            default:
                break;
        }

    }
    private void clearDynamicContent() {
        ((ViewGroup) dynamicContentContainer).removeAllViews();
    }

    private void inflateContent(int layoutResId) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(layoutResId, (ViewGroup) dynamicContentContainer, false);
        ((ViewGroup) dynamicContentContainer).addView(view);
    }
    // Toast helper method to prevent toast queue buildup
    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();  // Cancel the previous toast if it exists
        }
        currentToast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT);
        currentToast.show();
    }


}