package com.robonav.app.fragments;


import static com.robonav.app.utilities.FragmentUtils.appendOutput;
import static com.robonav.app.utilities.FragmentUtils.*;

import static com.robonav.app.utilities.FragmentUtils.showMessage;
import static com.robonav.app.utilities.JsonUtils.loadLocationDetails;
import static com.robonav.app.utilities.JsonUtils.loadRobotTasks;
import static com.robonav.app.utilities.JsonUtils.sendInstructionToTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.util.Pair;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import com.robonav.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NavigationFragment extends Fragment {

    private static Spinner taskDropdown;
    private static Spinner locationDropdown;
    private static TextView fulfilledBy;
    private static TextView coordinates;
    private Button addInstruction;
    private static List<String> locationNames;
    private static List<String> taskNames;
    private static ArrayAdapter<String> locationAdapter;
    private static ArrayAdapter<String> taskAdapter;
    private static HashMap<String, Pair<String, String>> storedTasks;
    private static Map<String, String> locationCoordinates;
    private static String currentTaskId;
    private static String currentRobotId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        // Initialize components
        taskDropdown = view.findViewById(R.id.task_dropdown);
        locationDropdown = view.findViewById(R.id.pre_existing_location_dropdown);
        fulfilledBy = view.findViewById(R.id.current_robot);
        coordinates = view.findViewById(R.id.location_coordinates);
        addInstruction = view.findViewById(R.id.btn_add_instruction);

        // Initialize locationNames here
        locationNames = new ArrayList<>();
        taskNames = new ArrayList<>();

        // Initialize locationAdapter
        locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNames);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationDropdown.setAdapter(locationAdapter);


        // Task adapter
        taskAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, taskNames);
        taskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskDropdown.setAdapter(taskAdapter);

        //Fetching and storing tasks
        fetchAndStoreRobotTasks(requireContext());

        //Task dropdown listener
        taskDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected task name
                String selectedTask = taskDropdown.getSelectedItem().toString();

                // Check if the map contains the selected task
                if (storedTasks != null && storedTasks.containsKey(selectedTask)) {
                    // Retrieve task details
                    Pair<String, String> taskDetails = storedTasks.get(selectedTask);

                    if (taskDetails != null) {
                        // Get Task ID and Robot ID
                        currentTaskId = taskDetails.first;
                        currentRobotId = taskDetails.second;
                        fulfilledBy.setText("Fulfilled By: Robot #" + currentRobotId);
                        coordinates.setText("Coordinates: ");
                        fetchAndPopulateLocationSpinner(requireContext(), currentRobotId);
                    } else {
                    }
                } else {
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        locationDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected location name
                String selectedLocation = parentView.getItemAtPosition(position).toString();

                // Log or use the selected location as needed
                Log.d("LocationSelected", "Selected Location: " + selectedLocation);

                // You can now use the selected location to fetch coordinates or perform other actions
                String currentCoordinates = locationCoordinates.get(selectedLocation);
                Log.d("LocationCoordinates", "Coordinates: " + coordinates);
                updateCoordinates(currentCoordinates);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        addInstruction.setOnClickListener(v -> {
            if (currentTaskId == null) {
                showMessage("No task selected", requireContext());
                return;
            }

            String selectedLocation = (String) locationDropdown.getSelectedItem();
            if (selectedLocation == null || !locationCoordinates.containsKey(selectedLocation)) {
                showMessage("Invalid location selected.", requireContext());
                return;
            }

            String instruction = "navigation:startNavigation:" + selectedLocation;

            sendInstructionToTask(requireContext(), currentTaskId, instruction)
                    .thenAccept(response -> {
                        // Do nothing or handle success
                    })
                    .exceptionally(e -> {
                        return null;
                    });
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchAndStoreRobotTasks(requireContext());
    }
    public static void fetchAndStoreRobotTasks(Context context) {
        CompletableFuture<HashMap<String, Pair<String, String>>> future = loadRobotTasks(context);

        future.thenAccept(taskMap -> {
            // Store the result in a local HashMap
            storedTasks = new HashMap<>(taskMap);

            // Example: Print stored tasks
            for (Map.Entry<String, Pair<String, String>> entry : storedTasks.entrySet()) {
                String taskName = entry.getKey();  // Now the key is taskName
                String taskId = entry.getValue().first;  // taskId is stored in the Pair's first element
                String robotId = entry.getValue().second;  // robotId is stored in the Pair's second element

                Log.d("TaskData", "Task Name: " + taskName + ", Task ID: " + taskId + ", Robot ID: " + robotId);
            }
            populateTaskSpinner(storedTasks, context);
        }).exceptionally(e -> {
            Log.e("TaskError", "Error fetching tasks", e);
            return null;
        });
    }
    private static void populateTaskSpinner(HashMap<String, Pair<String, String>> storedTasks, Context context) {
        // Clear the existing items in the spinner
        taskNames.clear();

        // Iterate over the map and add task names to the list
        for (Map.Entry<String, Pair<String, String>> entry : storedTasks.entrySet()) {
            String taskName = entry.getKey();  // Use taskName as the key
            taskNames.add(taskName);
        }

        // Notify the adapter that data has changed so it can refresh the spinner
        taskAdapter.notifyDataSetChanged();

        if (!taskNames.isEmpty()) {
            taskDropdown.setSelection(0);
        }

        // Get the selected task name
        String selectedTask = taskDropdown.getSelectedItem().toString();

        // Check if the map contains the selected task
        if (storedTasks != null && storedTasks.containsKey(selectedTask)) {
            // Retrieve task details
            Pair<String, String> taskDetails = storedTasks.get(selectedTask);

            if (taskDetails != null) {
                // Get Task ID and Robot ID
                currentTaskId = taskDetails.first;
                currentRobotId = taskDetails.second;
                fulfilledBy.setText("Fulfilled By: Robot #" + currentRobotId);
                coordinates.setText("Coordinates: ");
                fetchAndPopulateLocationSpinner(context, currentRobotId);
            } else {
            }
        } else {
        }
    }
    public static void fetchAndPopulateLocationSpinner(Context context, String robotId) {
        // Create a new map to store location name and coordinates
        locationCoordinates = new HashMap<>();

        CompletableFuture<List<JSONObject>> future = loadLocationDetails(context, robotId);

        future.thenAccept(locationDetails -> {
            // Clear the existing items in the spinner
            locationNames.clear();

            // Iterate over the location details and add location names to the list
            for (JSONObject location : locationDetails) {
                try {
                    String locationName = location.getString("location_name");
                    locationNames.add(locationName);

                    // Add location name and coordinates (latitude, longitude) to the map
                    String coordinates = location.getString("location_coordinates");
                    locationCoordinates.put(locationName, coordinates);
                } catch (JSONException e) {
                    Log.e("LocationError", "Error extracting location name", e);
                }
            }

            Log.d("LocationCoordinates", "Map contents: " + locationCoordinates.toString());

            // Notify the adapter that data has changed so it can refresh the spinner
            locationAdapter.notifyDataSetChanged();

            // Check if the spinner has items and populate the coordinates with the first item if available
            if (!locationNames.isEmpty()) {
                locationDropdown.setSelection(0);
                String firstLocationName = locationNames.get(0);
                String currentCoordinates = locationCoordinates.get(firstLocationName);
                updateCoordinates(currentCoordinates);
            }

        }).exceptionally(e -> {
            Log.e("LocationError", "Error fetching location details", e);
            return null;
        });
    }
    private static void updateCoordinates(String currentCoordinates) {
        // Split the coordinates string by the comma
        String[] coordinateParts = currentCoordinates.split(",");

        if (coordinateParts.length == 2) {
            try {
                // Parse both parts as double
                double latitude = Double.parseDouble(coordinateParts[0]);
                double longitude = Double.parseDouble(coordinateParts[1]);

                // Format both latitude and longitude to 3 decimal places
                String formattedLatitude = String.format("%.3f", latitude);
                String formattedLongitude = String.format("%.3f", longitude);

                // Set the formatted coordinates in the TextView
                coordinates.setText("Coordinates: " + formattedLatitude + ", " + formattedLongitude);
            } catch (NumberFormatException e) {
                Log.e("LocationError", "Invalid coordinate format", e);
                // Handle the error if the coordinates are invalid (optional)
            }
        } else {
            Log.e("LocationError", "Invalid coordinates format");
        }
    }
}