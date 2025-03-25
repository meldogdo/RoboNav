package com.robonav.app.fragments;


import static com.robonav.app.utilities.FragmentUtils.appendOutput;
import static com.robonav.app.utilities.FragmentUtils.*;

import static com.robonav.app.utilities.FragmentUtils.showMessage;
import static com.robonav.app.utilities.JsonUtils.fetchRecentInstructions;
import static com.robonav.app.utilities.JsonUtils.loadLocationDetails;
import static com.robonav.app.utilities.JsonUtils.loadRobotNames;
import static com.robonav.app.utilities.JsonUtils.loadRobotTasks;
import static com.robonav.app.utilities.JsonUtils.sendInstructionToTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.util.Pair;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import com.robonav.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NavigationFragment extends Fragment {

    private static Spinner taskDropdown;
    private static Spinner locationDropdown,instructionSpinner;
    private static TextView fulfilledBy, outputTextView;
    private static TextView coordinates;
    private Button addInstruction;
    private static List<String> locationNames, instructionNames;
    private static List<String> taskNames;
    private static ArrayAdapter<String> locationAdapter, instructionAdapter;
    private static ArrayAdapter<String> taskAdapter;
    private static HashMap<String, Pair<String, String>> storedTasks;
    private static Map<String, String> locationCoordinates;
    private static String currentTaskId;
    private static String currentRobotId;
    private static NestedScrollView scrollView;
    private ImageButton refresh;
    private boolean isSpinnerInitialized = false;


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
        outputTextView = view.findViewById(R.id.output_text_view_nav);
        scrollView = view.findViewById(R.id.output_scroll_view_nav);
        instructionSpinner = view.findViewById(R.id.robot_callbacks_spinner_nav);
        refresh = view.findViewById(R.id.refresh_button_nav);

        // Initialize locationNames here
        locationNames = new ArrayList<>();
        taskNames = new ArrayList<>();

        // Initialize instructionNames here
        instructionNames = new ArrayList<>();

        // Initialize instructionAdapter
        instructionAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, instructionNames);
        instructionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructionSpinner.setAdapter(instructionAdapter);

        // Initialize locationAdapter
        locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNames);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationDropdown.setAdapter(locationAdapter);


        // Task adapter
        taskAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, taskNames);
        taskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskDropdown.setAdapter(taskAdapter);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger your refresh logic here
                Log.d("Refresh", "Refresh button clicked.");

                // Get the selected instruction from the spinner
                String selectedInstruction = instructionSpinner.getSelectedItem().toString();
                Log.d("InstructionSelected", "Selected Instruction: " + selectedInstruction);

                if (selectedInstruction.equals("All Robots")) {
                    getAndDisplayRecentInstructions(requireContext(), null);
                } else {
                    // Extract the number after the '#' symbol
                    String[] parts = selectedInstruction.split("#");

                    if (parts.length > 1) {
                        String instructionId = parts[1].trim(); // Get the number after '#'

                        try {
                            String encodedInstructionId = URLEncoder.encode(instructionId, "UTF-8");
                            // Call getAndDisplayRecentInstructions with the encoded instruction ID
                            getAndDisplayRecentInstructions(requireContext(), encodedInstructionId);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            showMessage("Encoding error: Cannot retrieve instruction data.", requireContext());
                        }
                    } else {
                        // Handle case where the instruction format is incorrect
                        showMessage("Invalid instruction format. Please retry.", requireContext());
                    }
                }
            }
        });

        instructionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true;
                    return;
                }

                String selectedInstruction = parentView.getItemAtPosition(position).toString();
                Log.d("InstructionSelected", "Selected Instruction: " + selectedInstruction);

                if (selectedInstruction.equals("All Robots")) {
                    getAndDisplayRecentInstructions(requireContext(), null);
                } else {
                    String[] parts = selectedInstruction.split("#");
                    if (parts.length > 1) {
                        String instructionId = parts[1].trim();
                        try {
                            String encodedInstructionId = URLEncoder.encode(instructionId, "UTF-8");
                            getAndDisplayRecentInstructions(requireContext(), encodedInstructionId);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            showMessage("Encoding error: Unable to fetch instruction data.", requireContext());
                        }
                    } else {
                        showMessage("Invalid instruction format. Please retry.", requireContext());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d("InstructionSelected", "No instruction selected.");
            }
        });




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
        fetchAndPopulateInstructionSpinner(requireContext());
        fetchAndPopulateLocationSpinner(requireContext(),null);
        getAndDisplayRecentInstructions(requireContext(), null);

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
        }).exceptionally(e -> null);
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

    public static void getAndDisplayRecentInstructions(Context context, String robotId) {
        fetchRecentInstructions(context, robotId).thenAccept(instructions -> {
            if (instructions.isEmpty()) {
                showMessage("No recent instructions found.", context);
            } else {
                for (String message : instructions) {
                    appendOutputMessage(message);  // Output each instruction message
                }
            }
        }).exceptionally(ex -> null);
    }

    private static void appendOutputMessage(String message) {

        // Get existing text
        String currentText = outputTextView.getText().toString().trim(); // Trim to avoid trailing newlines

        // Append the new message at the bottom, ensuring spacing between messages
        String updatedText = currentText.isEmpty() ? message : currentText + "\n\n" + message;

        outputTextView.setText(updatedText);

        // Ensure UI updates before scrolling
        scrollView.post(() -> {
            scrollView.fullScroll(View.FOCUS_UP); // Scroll to the bottom to show the latest message
            outputTextView.invalidate(); // Force UI update
        });
    }

    public static void fetchAndPopulateInstructionSpinner(Context context) {
        CompletableFuture<List<String>> future = loadRobotNames(context);

        future.thenAccept(robotNames -> {
            // Clear the existing items in the spinner
            instructionNames.clear();
            instructionNames.add("All Robots");

            // Add robot names to the instruction spinner list
            instructionNames.addAll(robotNames);

            // Notify the adapter that data has changed so it can refresh the spinner
            instructionAdapter.notifyDataSetChanged();

            if (!instructionNames.isEmpty()) {
                instructionSpinner.setSelection(0);
            }

        }).exceptionally(e -> {
            return null;
        });
    }


}