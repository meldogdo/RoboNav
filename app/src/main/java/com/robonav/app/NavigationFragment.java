package com.robonav.app;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NavigationFragment extends Fragment {

    private Spinner robotDropdown, locationDropdown;
    private TextView locationCoordinatesTextView;
    private Button btnNavigate;
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
        btnNavigate = view.findViewById(R.id.btn_navigate);

        // Load robot names into the dropdown
        List<String> robotNames = loadRobotNames();
        ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotDropdown.setAdapter(robotAdapter);

        // Load location names into the location dropdown, add "No Location" as the first option
        List<String> locationNames = loadLocationNames();
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
                showMessage("Please enter coordinates.");
                return;  // Exit the method to prevent further action
            }

            // Validate coordinates if "[Use Coordinates]" is selected
            if (selectedLocation.equals("[Use Coordinates]") && !isValidCoordinates(coordinates)) {
                // Show a toast message if coordinates are invalid
                showMessage("Invalid coordinates. Enter as 'latitude, longitude' within valid ranges.");
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
            appendOutput(message);
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
                    String coordinates = getCoordinatesForLocation(selectedLocation);
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

    // Load the robot names from the robots.json file
    private List<String> loadRobotNames() {
        List<String> robotNames = new ArrayList<>();
        try {
            String robotsJson = JsonUtils.loadJSONFromAsset(requireContext(), "robots.json");
            JSONArray robotsArray = new JSONArray(robotsJson);
            for (int i = 0; i < robotsArray.length(); i++) {
                JSONObject robot = robotsArray.getJSONObject(i);
                robotNames.add(robot.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading robots: " + e.getMessage());
        }
        return robotNames;
    }

    // Load the location names from the locations.json file
    private List<String> loadLocationNames() {
        List<String> locationNames = new ArrayList<>();
        try {
            String locationJson = JsonUtils.loadJSONFromAsset(requireContext(), "locations.json");
            JSONArray locationsArray = new JSONArray(locationJson);
            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject location = locationsArray.getJSONObject(i);
                locationNames.add(location.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage("Error loading locations: " + e.getMessage());
        }
        return locationNames;
    }

    // Get coordinates for a given location name
    private String getCoordinatesForLocation(String locationName) {
        try {
            String locationJson = JsonUtils.loadJSONFromAsset(requireContext(), "locations.json");
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

    // Helper method to display toast messages
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void appendOutput(String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = formatter.format(new Date());
        // Assuming an output box exists in your layout
        View rootView = getView();
        if (rootView != null) {
            TextView outputBox = rootView.findViewById(R.id.output_text_view);
            String currentOutput = outputBox.getText().toString();
            if (currentOutput.isEmpty()) {
                scrollView.setVisibility(View.VISIBLE);
                outputBox.setText(currentOutput + currentTime +"\n" + message);
            }
            else {
                outputBox.setText(currentOutput + "\n\n" + currentTime + "\n" + message );

            }
            // Scroll to the bottom after updating the content

            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        }
    }

    // Helper method to validate coordinates format and range
    private boolean isValidCoordinates(String coordinates) {
        // Split coordinates by comma
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            return false; // Invalid format (must have exactly 2 parts)
        }

        try {
            // Parse latitude and longitude
            float latitude = Float.parseFloat(parts[0].trim());
            float longitude = Float.parseFloat(parts[1].trim());

            // Check if latitude is within range -90 to 90 and longitude is within range -180 to 180
            return (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180);
        } catch (NumberFormatException e) {
            return false; // Invalid number format
        }
    }
}
