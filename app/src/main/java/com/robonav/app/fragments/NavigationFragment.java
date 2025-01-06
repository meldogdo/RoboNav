package com.robonav.app.fragments;

import static com.robonav.app.utilities.FragmentUtils.appendOutput;
import static com.robonav.app.utilities.FragmentUtils.isValidCoordinates;
import static com.robonav.app.utilities.FragmentUtils.showMessage;
import static com.robonav.app.utilities.JsonUtils.getCoordinatesForLocation;
import static com.robonav.app.utilities.JsonUtils.loadLocationNames;
import static com.robonav.app.utilities.JsonUtils.loadRobotNames;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import com.robonav.app.R;

import java.util.List;

public class NavigationFragment extends Fragment {

    private Spinner robotDropdown, locationDropdown;
    private TextView locationCoordinatesTextView;
    private NestedScrollView scrollView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout only once
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        // Initialize components
        scrollView = view.findViewById(R.id.output_scroll_view);
        robotDropdown = view.findViewById(R.id.robot_dropdown);
        locationDropdown = view.findViewById(R.id.pre_existing_location_dropdown);
        locationCoordinatesTextView = view.findViewById(R.id.input_coordinates);
        Button btnNavigate = view.findViewById(R.id.btn_navigate);

        // Load robot names into the dropdown
        List<String> robotNames = loadRobotNames(requireContext());
        ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotDropdown.setAdapter(robotAdapter);

        // Load location names into the location dropdown, add "No Location" as the first option
        List<String> locationNames = loadLocationNames(requireContext());
        locationNames.add(0, "[Use Coordinates]");  // Add "No Location" at the start
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNames);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationDropdown.setAdapter(locationAdapter);

        // Disable location dropdown and coordinates input initially
        locationDropdown.setEnabled(false);
        locationCoordinatesTextView.setEnabled(false);

        // Set the button click listener
        btnNavigate.setOnClickListener(v -> {
            String selectedRobot = robotDropdown.getSelectedItem().toString();
            String selectedLocation = locationDropdown.getSelectedItem().toString();
            String coordinates = locationCoordinatesTextView.getText().toString().trim();  // Get the coordinates entered

            // Check if "Use Coordinates" is selected and coordinates are empty
            if (selectedLocation.equals("[Use Coordinates]") && coordinates.isEmpty()) {
                // Show a toast message if coordinates are not entered
                showMessage("Please enter coordinates.",requireContext());
                return;  // Exit the method to prevent further action
            }

            // Validate coordinates if "[Use Coordinates]" is selected
            if (selectedLocation.equals("[Use Coordinates]") && !isValidCoordinates(coordinates)) {
                // Show a toast message if coordinates are invalid
                showMessage("Invalid coordinates. Enter as 'latitude, longitude' within valid ranges.",requireContext());
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

        // Handle location selection and reset coordinates input or allow editing
        locationDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLocation = locationDropdown.getSelectedItem().toString();
                if (selectedLocation.equals("[Use Coordinates]")) {
                    // If "No Location" is selected, allow the user to enter coordinates
                    locationCoordinatesTextView.setEnabled(true);
                    locationCoordinatesTextView.setText("");  // Clear any coordinates
                } else {
                    // If a location is selected, fill coordinates and disable the input
                    String coordinates = getCoordinatesForLocation(selectedLocation,requireContext());
                    locationCoordinatesTextView.setText(coordinates);
                    locationCoordinatesTextView.setEnabled(false);  // Disable editing
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected
            }
        });

        return view;
    }
}
