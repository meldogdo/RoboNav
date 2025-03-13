package com.robonav.app.fragments;

import static com.robonav.app.utilities.FragmentUtils.appendOutput;
import static com.robonav.app.utilities.FragmentUtils.isValidCoordinates;
import static com.robonav.app.utilities.FragmentUtils.showMessage;
import static com.robonav.app.utilities.JsonUtils.getCoordinatesForLocation;
import static com.robonav.app.utilities.JsonUtils.loadLocationDetails;
import static com.robonav.app.utilities.JsonUtils.loadRobotNames;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import com.robonav.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationFragment extends Fragment {

    private Spinner robotDropdown, locationDropdown;
    private TextView locationCoordinatesTextView;
    private NestedScrollView scrollView;
    private List<String> locationNames;  // Declare it here to be used in both places
    private ArrayAdapter<String> locationAdapter;  // Declare it here so it can be reused

    private Map<String, String> locationCoordinatesMap = new HashMap<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        // Initialize components
        scrollView = view.findViewById(R.id.output_scroll_view);
        robotDropdown = view.findViewById(R.id.robot_dropdown);
        locationDropdown = view.findViewById(R.id.pre_existing_location_dropdown);
        locationCoordinatesTextView = view.findViewById(R.id.input_coordinates);
        Button btnNavigate = view.findViewById(R.id.btn_navigate);

        // Initialize locationNames here
        locationNames = new ArrayList<>();
        locationNames.add("[Use Coordinates]"); // Default option

        // Initialize locationAdapter
        locationAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, locationNames);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationDropdown.setAdapter(locationAdapter);

        // Disable location dropdown and coordinates input initially
        locationDropdown.setEnabled(false);
        locationCoordinatesTextView.setEnabled(false);

        // Load robot names into the dropdown asynchronously
        loadRobotNames(requireContext()).thenAccept(robotNames -> {
            // When robot names are loaded, update the adapter
            ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
            robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            robotDropdown.setAdapter(robotAdapter);
        }).exceptionally(ex -> {
            // Handle any errors during the network request
            ex.printStackTrace();
            return null;
        });

        // Set the button click listener
        btnNavigate.setOnClickListener(v -> {
            String selectedRobot = robotDropdown.getSelectedItem().toString();
            String selectedLocation = locationDropdown.getSelectedItem().toString();
            String coordinates = locationCoordinatesTextView.getText().toString().trim();  // Get the coordinates entered

            // Check if "Use Coordinates" is selected and coordinates are empty
            if (selectedLocation.equals("[Use Coordinates]") && coordinates.isEmpty()) {
                // Show a toast message if coordinates are not entered
                showMessage("Please enter coordinates.", requireContext());
                return;  // Exit the method to prevent further action
            }

            // Validate coordinates if "[Use Coordinates]" is selected
            if (selectedLocation.equals("[Use Coordinates]") && !isValidCoordinates(coordinates)) {
                // Show a toast message if coordinates are invalid
                showMessage("Invalid coordinates. Enter as 'latitude, longitude' within valid ranges.", requireContext());
                return;  // Exit the method to prevent further action
            }

            // Build the message based on the location choice
            String message;
            if (selectedLocation.equals("[Use Coordinates]")) {
                // Use the coordinates entered by the user if no location is selected
                message = selectedRobot + " to Coordinates:\n" + coordinates;
            } else {
                // If a location is selected, use the predefined coordinates for that location
                message = selectedRobot + " to Location:\n" + selectedLocation + " (" + coordinates + ")";
            }

            // Append the message to the output
            appendOutput(message, scrollView, view);
        });

        // Handle robot selection and enable location dropdown or coordinates input
        robotDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Enable location dropdown and coordinates input once a robot is selected
                locationDropdown.setEnabled(true);
                locationCoordinatesTextView.setEnabled(true);

                // Clear location and coordinates inputs
                locationDropdown.setSelection(0);  // Set "No Location" initially
                locationCoordinatesTextView.setText("");  // Clear coordinates
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected
            }
        });

        // Set the listener for when a location is selected
        // Set the listener for when a location is selected
        locationDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected location name
                String selectedLocation = locationDropdown.getSelectedItem().toString();

                // Check if the selected location is "[Use Coordinates]"
                if (selectedLocation.equals("[Use Coordinates]")) {
                    // If "[Use Coordinates]" is selected, allow manual input of coordinates
                    locationCoordinatesTextView.setEnabled(true);
                    locationCoordinatesTextView.setText("");  // Clear any existing coordinates
                } else {
                    // If a predefined location is selected, get the coordinates from the map
                    String coordinates = locationCoordinatesMap.get(selectedLocation);

                    if (coordinates != null) {
                        // Populate the coordinates section with the corresponding coordinates
                        locationCoordinatesTextView.setText(coordinates);
                    } else {
                        // If no coordinates are found for the selected location, clear the coordinates section
                        locationCoordinatesTextView.setText("");
                    }

                    // Disable editing if coordinates are pre-defined
                    locationCoordinatesTextView.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If nothing is selected, clear the coordinates section
                locationCoordinatesTextView.setText("");
            }
        });



        // Set the touch listener for the robot dropdown to update when expanded
        robotDropdown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // When the dropdown is opened (touched), update the robot names
                loadRobotNames(requireContext()).thenAccept(robotNames -> {
                    // Update the adapter with new robot names
                    ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
                    robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    robotDropdown.setAdapter(robotAdapter);
                }).exceptionally(ex -> {
                    // Handle any errors during the loading of robot names
                    ex.printStackTrace();
                    return null;
                });
            }
            return false;
        });

        // Set the touch listener for the location dropdown to update when expanded
        locationDropdown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // When the dropdown is opened (touched), update the location names
                String selectedRobot = robotDropdown.getSelectedItem().toString();
                String robotId = selectedRobot.replaceAll("[^0-9]", ""); // Extract robot ID

                loadLocationDetails(requireContext(), robotId).thenAccept(locationDetails -> {
                    // Initialize a list to hold location names
                    List<String> newLocationNames = new ArrayList<>();
                    newLocationNames.add("[Use Coordinates]"); // Default option

                    // If location details are empty, don't add any further locations
                    if (locationDetails.isEmpty()) {

                    } else {
                        // Iterate over the location details and add names
                        for (JSONObject location : locationDetails) {
                            try {

                                String name = location.getString("location_name");


                                // Adding name to newLocationNames
                                newLocationNames.add(name);

                                // Check if the location has coordinates and retrieve them
                                if (location.has("location_coordinates")) {
                                    String coordinates = location.getString("location_coordinates");
                                    // Populate your map with the location name and coordinates
                                    locationCoordinatesMap.put(name, coordinates);
                                } else {
                                }
                            } catch (JSONException e) {

                            }
                        }
                    }

                    // Now update the location adapter on the main thread
                    requireActivity().runOnUiThread(() -> {
                        // Update the adapter with new names
                        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, newLocationNames);
                        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationDropdown.setAdapter(locationAdapter);
                    });

                }).exceptionally(ex -> {
                    // Handle any errors during the loading of location details
                    return null;
                });
            }
            return false;
        });








        return view;

    }
}
