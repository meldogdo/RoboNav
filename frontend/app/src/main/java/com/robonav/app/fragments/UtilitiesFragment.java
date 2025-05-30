package com.robonav.app.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.robonav.app.models.Robot.findRobotByName;
import static com.robonav.app.utilities.FragmentUtils.showMessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.utilities.ConfigManager;
import com.robonav.app.R;
import com.robonav.app.models.Robot;
import static com.robonav.app.utilities.FragmentUtils.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UtilitiesFragment extends Fragment {


    private Spinner actionSpinner, robotCallbacksSpinner;


    private View dynamicContentContainer;
    private NestedScrollView scrollView;

    private ImageButton refreshButton;
    private View view;
    private Toast currentToast; // Store the latest toast reference

    @Override
    public void onResume() {
        super.onResume();
        setupDropdownMenu();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_utilities, container, false);
        scrollView = view.findViewById(R.id.output_scroll_view);
        actionSpinner = view.findViewById(R.id.action_spinner);
        robotCallbacksSpinner = view.findViewById(R.id.robot_callbacks_spinner);
        dynamicContentContainer = view.findViewById(R.id.dynamic_content_container);

        refreshButton = view.findViewById(R.id.refresh_button); // Find the refresh button

        // Set up refresh button click listener
        refreshButton.setOnClickListener(v -> {
            String selectedRobot = robotCallbacksSpinner.getSelectedItem().toString(); // Get the selected robot
            handleRobotSelection(selectedRobot); // Refresh the API call
            showMessage("Refreshing callbacks...", requireContext()); // Show feedback
        });


        return view;
    }

    private void appendOutputMessage(String message) {
        TextView outputTextView = view.findViewById(R.id.output_text_view);
        NestedScrollView scrollView = view.findViewById(R.id.output_scroll_view);

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

    private void clearOutput() {
        TextView outputTextView = view.findViewById(R.id.output_text_view);
        outputTextView.setText(""); // Clears previous messages
    }

    private void setupDropdownMenu() {
        // Actions Spinner
        String[] actions = {
                "Save Robot's Current Location",
                "Remove Robot's Location",
                "Get Location Coordinates"
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
                clearDynamicContent();
            }
        });

        // Robot Callbacks Spinner
        fetchRobotsForDropdown();
    }


    private void fetchRobotsForDropdown() {
        String robotUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/robots";
        String token = requireContext().getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);

        if (token == null) {
            showMessage("Authentication error: No token found.", requireContext());
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest robotRequest = new JsonArrayRequest(Request.Method.GET, robotUrl, null,
                response -> {
                    List<String> robotNames = new ArrayList<>();
                    robotNames.add("All Robots");  // Default option

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject robot = response.getJSONObject(i);
                            robotNames.add(robot.getString("name"));
                        }

                        // Populate dropdown with robot names
                        ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
                        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        robotCallbacksSpinner.setAdapter(robotAdapter);

                        // Set default selection and listener
                        robotCallbacksSpinner.setSelection(0);  // Ensure default is 'All Robots'
                        robotCallbacksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedRobot = robotNames.get(position);
                                handleRobotSelection(selectedRobot);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showMessage("Failed to parse robot data.", requireContext());
                    }
                },
                error -> {
                    error.printStackTrace();
                    String message = extractVolleyErrorMessage(error);
                    showMessage(message, requireContext());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        robotRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,  // Timeout in milliseconds
                0,      // No retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(robotRequest);
    }

    private void handleRobotSelection(String selectedRobot) {
        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/callbacks";

        if (!selectedRobot.equals("All Robots")) {
            // Extract ID from "Robot #ID" format
            String[] parts = selectedRobot.split("#");
            if (parts.length > 1) {
                String robotId = parts[1].trim(); // Get the ID part

                try {
                    String encodedRobotId = URLEncoder.encode(robotId, "UTF-8");
                    url += "?robotId=" + encodedRobotId;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    showMessage("Encoding error: Unable to fetch robot callbacks.", requireContext());
                    return;
                }
            } else {
                showMessage("Invalid robot format. Try again.", requireContext());
                return;
            }
        }
        fetchRobotCallbacks(url);
    }

    private void fetchRobotCallbacks(String url) {
        String token = requireContext().getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);
        if (token == null) {
            showMessage("Authentication error: No token found.", requireContext());
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Clear output before fetching new data
        clearOutput();

        JsonObjectRequest callbackRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");

                        if (data.length() == 0) {
                            appendOutputMessage("No callbacks found.");
                            return;
                        }

                        // Append each formatted message with exactly two newlines between messages
                        for (int i = 0; i < data.length(); i++) {
                            String message = data.getString(i);
                            appendOutputMessage(message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showMessage("Failed to parse callback data.", requireContext());
                    }
                },
                error -> {
                    error.printStackTrace();
                    String message = extractVolleyErrorMessage(error);
                    showMessage(message, requireContext());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        callbackRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,  // Timeout in milliseconds
                0,      // No retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(callbackRequest);
    }

    private void handleActionSelection(String action) {
        clearDynamicContent();
        Spinner robotDropdown, locationDropdown;
        String token;
        RequestQueue queue;
        JsonArrayRequest robotRequest;
        String robotUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/robots";
        String locationsUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/locations";
        switch (action) {
            case "Save Robot's Current Location":
                inflateContent(R.layout.dynamic_save_robots_current_location);

                robotDropdown = dynamicContentContainer.findViewById(R.id.robot_dropdown);

                EditText inputLocationName = dynamicContentContainer.findViewById(R.id.input_location_name);
                Button btnSaveLocation = dynamicContentContainer.findViewById(R.id.btn_save_location);

                // Retrieve JWT token from SharedPreferences
                token = requireContext().getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);
                if (token == null) {
                    showMessage("Authentication error: No token found.", requireContext());
                    return;
                }

                // API URL for fetching robots
                queue = Volley.newRequestQueue(requireContext());

                // Request to get the list of robots
                robotRequest = new JsonArrayRequest(Request.Method.GET, robotUrl, null,
                        response -> {
                            List<Robot> robotsWithLocations = new ArrayList<>();
                            List<String> robotNames = new ArrayList<>();

                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    Robot robot = new Robot(response.getJSONObject(i));
                                    robotsWithLocations.add(robot);
                                    robotNames.add(robot.getName());
                                }

                                // Populate the dropdown with updated robot names
                                ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
                                robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                robotDropdown.setAdapter(robotAdapter);

                                // Handle robot selection and prefill location name
                                robotDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedRobot = robotDropdown.getSelectedItem().toString();
                                        Robot selectedRobotObj = findRobotByName(selectedRobot, robotsWithLocations);

                                        if (selectedRobotObj != null) {
                                            // Prefill the location name if the robot already has one
                                            String location = selectedRobotObj.getLocationName();
                                            inputLocationName.setText(location.isEmpty() ? "" : location);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        inputLocationName.setText("");
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showMessage("Failed to parse robot data.", requireContext());
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            String message = extractVolleyErrorMessage(error);
                            showMessage(message, requireContext());
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };


                // Apply retry policy (single request attempt)
                robotRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20000,  // Timeout in milliseconds
                        0,      // No retries
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

                queue.add(robotRequest);


                // Handle "Save Location" button click
                btnSaveLocation.setOnClickListener(v -> {
                    String selectedRobot = robotDropdown.getSelectedItem() != null ? robotDropdown.getSelectedItem().toString() : "";

                    if (selectedRobot.isEmpty()) {
                        showMessage("Please select a robot.", requireContext());
                        return;
                    }

                    String newLocationName = inputLocationName.getText().toString().trim();


                    if (!isValidLocationName(newLocationName)) {
                        showMessage("Location must be 3-50 alphanumeric characters, spaces or underscores.", requireContext());
                        return;
                    }

                    // Extract robotId from the robot name (assuming format: "Robot #X")
                    String[] parts = selectedRobot.split("#");
                    if (parts.length < 2) {
                        showMessage("Invalid robot format. Please select a valid robot.", requireContext());
                        return;
                    }


                    String robotId = parts[1].trim(); // Get the ID part

                    // API URL
                    String saveLocationUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/save-current-position";


                    // Create JSON body
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("robotId", robotId);
                        requestBody.put("locationName", newLocationName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showMessage("Error creating request data.", requireContext());
                        return;
                    }

                    // Send API request
                    JsonObjectRequest saveLocationRequest = new JsonObjectRequest(Request.Method.POST, saveLocationUrl, requestBody,
                            response -> {
                                try {
                                    String message = response.getString("message");
                                    showMessage(message, requireContext()); // Display API response as a toast
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showMessage("Unexpected response from server.", requireContext());
                                }
                            },
                            error -> {
                                error.printStackTrace();
                                String message = extractVolleyErrorMessage(error);
                                showMessage(message, requireContext());
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };


                    queue.add(saveLocationRequest);
                });



                break;

            case "Remove Robot's Location":
                inflateContent(R.layout.dynamic_remove_robots_location);


                robotDropdown = dynamicContentContainer.findViewById(R.id.robot_dropdown);

                locationDropdown = dynamicContentContainer.findViewById(R.id.location_dropdown);
                Button btnRemoveLocation = dynamicContentContainer.findViewById(R.id.btn_remove_location);

                token = requireContext().getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);
                if (token == null) {
                    showMessage("Authentication error: No token found.", requireContext());
                    return;
                }

                queue = Volley.newRequestQueue(requireContext());

                // Request to get the list of robots
                robotRequest = new JsonArrayRequest(Request.Method.GET, robotUrl, null,
                        response -> {
                            List<Robot> robotsWithLocations = new ArrayList<>();
                            List<String> robotNames = new ArrayList<>();

                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    Robot robot = new Robot(response.getJSONObject(i));
                                    robotsWithLocations.add(robot);
                                    robotNames.add(robot.getName());
                                }

                                ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, robotNames);
                                robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                robotDropdown.setAdapter(robotAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showMessage("Failed to parse robot data.", requireContext());
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            String message = extractVolleyErrorMessage(error);
                            showMessage(message, requireContext());
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };

                queue.add(robotRequest);

                // HashMap to store location names and IDs
                HashMap<String, String> locationMap = new HashMap<>();

                // Fetch locations when a robot is selected
                robotDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedRobot = robotDropdown.getSelectedItem().toString();

                        // Extract robotId from the robot name
                        String[] parts = selectedRobot.split("#");
                        if (parts.length < 2) {
                            showMessage("Invalid robot format. Please select a valid robot.", requireContext());
                            return;
                        }

                        String robotId = parts[1].trim();
                        String locationUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/" + robotId + "/locations";

                        JsonArrayRequest locationRequest = new JsonArrayRequest(Request.Method.GET, locationUrl, null,
                                locationResponse -> {
                                    List<String> locationNames = new ArrayList<>();
                                    locationMap.clear(); // Clear previous entries

                                    try {
                                        for (int i = 0; i < locationResponse.length(); i++) {
                                            JSONObject location = locationResponse.getJSONObject(i);
                                            String locName = location.getString("name");
                                            String locId = location.getString("loc_id");

                                            locationNames.add(locName);
                                            locationMap.put(locName, locId); // Map locName to locId
                                        }

                                        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNames);
                                        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        locationDropdown.setAdapter(locationAdapter);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        showMessage("Failed to parse location data.", requireContext());
                                    }
                                },
                                error -> {
                                    error.printStackTrace();
                                    String message = extractVolleyErrorMessage(error);
                                    showMessage(message, requireContext());
                                }
                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + token);
                                return headers;
                            }
                        };

                        queue.add(locationRequest);
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });


                // Handle "Remove Location" button click
                btnRemoveLocation.setOnClickListener(v -> {
                    String selectedLocation = locationDropdown.getSelectedItem() != null ? locationDropdown.getSelectedItem().toString() : "";

                    if (selectedLocation.isEmpty()) {
                        showMessage("Please select a location.", requireContext());
                        return;
                    }

                    // Retrieve the corresponding location ID from the HashMap
                    String selectedLocationId = locationMap.get(selectedLocation);

                    if (selectedLocationId == null) {
                        showMessage("Error: Could not retrieve location ID.", requireContext());
                        return;
                    }

                    String removeLocationUrl = ConfigManager.getBaseUrl() + "/api/protected/location/" + selectedLocationId;

                    JsonObjectRequest removeLocationRequest = new JsonObjectRequest(Request.Method.DELETE, removeLocationUrl, null,
                            response -> {
                                try {
                                    String message = response.getString("message");
                                    showMessage(message, requireContext());

                                    // ✅ Remove the deleted location from the dropdown
                                    locationMap.remove(selectedLocation);  // Remove from HashMap

                                    // ✅ Remove from dropdown UI
                                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) locationDropdown.getAdapter();
                                    adapter.remove(selectedLocation);
                                    adapter.notifyDataSetChanged();  // Refresh dropdown

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showMessage("Unexpected response from server.", requireContext());
                                }
                            },
                            error -> {
                                error.printStackTrace();
                                String message = extractVolleyErrorMessage(error);
                                showMessage(message, requireContext());
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };

                    queue.add(removeLocationRequest);
                });


                break;

            case "Get Location Coordinates":
                inflateContent(R.layout.dynamic_get_location_coordinates);

                locationDropdown = dynamicContentContainer.findViewById(R.id.location_dropdown);
                Button btnGetCoordinates = dynamicContentContainer.findViewById(R.id.btn_get_coordinates);

                // Retrieve JWT token from SharedPreferences
                token = requireContext().getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);
                if (token == null) {
                    showMessage("Authentication error: No token found.", requireContext());
                    return;
                }

                // API URL to fetch locations
                queue = Volley.newRequestQueue(requireContext());

                // Request to get the list of locations
                JsonObjectRequest locationsRequest = new JsonObjectRequest(Request.Method.GET, locationsUrl, null,
                        response -> {
                            try {
                                if (!response.has("locations")) {
                                    showMessage("Invalid API response: 'locations' key missing.", requireContext());
                                    return;
                                }

                                JSONArray locationsArray = response.getJSONArray("locations");
                                List<String> locationNamesWithId = new ArrayList<>();
                                HashMap<String, String> locationIdMap = new HashMap<>();

                                if (locationsArray.length() == 0) {
                                    showMessage("No locations found.", requireContext());
                                    return;
                                }

                                for (int i = 0; i < locationsArray.length(); i++) {
                                    JSONObject locationObj = locationsArray.getJSONObject(i);

                                    // Extract fields properly
                                    String locId = locationObj.getString("locId"); // Now using locId
                                    String locationName = locationObj.getString("locationName");

                                    // Combine name with loc_id for better selection
                                    String displayName = locationName + " (ID: " + locId + ")";
                                    locationNamesWithId.add(displayName);
                                    locationIdMap.put(displayName, locId);
                                }

                                // Populate the dropdown with updated location names
                                ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationNamesWithId);
                                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                locationDropdown.setAdapter(locationAdapter);

                                // Handle "Get Coordinates" button click
                                btnGetCoordinates.setOnClickListener(v -> {
                                    String selectedDisplayName = locationDropdown.getSelectedItem() != null ? locationDropdown.getSelectedItem().toString() : "";

                                    if (selectedDisplayName.isEmpty()) {
                                        showMessage("Please select a location.", requireContext());
                                        return;
                                    }

                                    // Extract loc_id from selected dropdown item
                                    String locId = locationIdMap.get(selectedDisplayName);
                                    if (locId == null) {
                                        showMessage("Error retrieving location ID.", requireContext());
                                        return;
                                    }

                                    // API URL for getting coordinates
                                    String getCoordinatesUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/location/" + locId;

                                    // Send GET request
                                    JsonObjectRequest getCoordinatesRequest = new JsonObjectRequest(Request.Method.GET, getCoordinatesUrl, null,
                                            coordinatesResponse -> {
                                                try {
                                                    JSONObject coordinates = coordinatesResponse.getJSONObject("coordinates");
                                                    double x = coordinates.getDouble("x");
                                                    double y = coordinates.getDouble("y");
                                                    double z = coordinates.optDouble("z", 0);
                                                    double theta = coordinates.optDouble("theta", 0);
                                                    String coordinateText = String.format("x: %.2f, y: %.2f, z: %.2f, θ: %.2f", x, y, z, theta);
                                                    showMessage("Coordinates:\n" + coordinateText, requireContext()); // Display API response as a toast
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    showMessage("Unexpected response from server.", requireContext());
                                                }
                                            },
                                            error -> {
                                                error.printStackTrace();
                                                String message = extractVolleyErrorMessage(error);
                                                showMessage(message, requireContext());
                                            }
                                    ) {
                                        @Override
                                        public Map<String, String> getHeaders() {
                                            Map<String, String> headers = new HashMap<>();
                                            headers.put("Authorization", "Bearer " + token);
                                            return headers;
                                        }
                                    };

                                    // Apply retry policy (single attempt)
                                    getCoordinatesRequest.setRetryPolicy(new DefaultRetryPolicy(
                                            20000,  // Timeout in milliseconds
                                            0,      // No retries
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                    ));

                                    queue.add(getCoordinatesRequest);
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showMessage("Failed to parse location data.", requireContext());
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            String message = extractVolleyErrorMessage(error);
                            showMessage(message, requireContext());
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };

                // Apply retry policy (single request attempt)
                locationsRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20000,  // Timeout in milliseconds
                        0,      // No retries
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

                queue.add(locationsRequest);

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

}