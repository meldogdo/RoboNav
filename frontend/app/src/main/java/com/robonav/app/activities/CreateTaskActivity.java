package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import com.robonav.app.utilities.ConfigManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
import static com.robonav.app.utilities.FragmentUtils.*;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskNameEditText;
    private Spinner robotSpinner;
    private Button submitTaskButton;
    private String token;
    private static final String CREATE_TASK_URL = ConfigManager.getBaseUrl() + "/api/protected/robot/task/create";
    private static final String ROBOT_LIST_URL = ConfigManager.getBaseUrl() + "/api/protected/robot/robots";

    private final Map<String, Integer> robotMap = new HashMap<>(); // Store robot names & IDs

    private static Toast activeToast; // Prevents stacking Toast messages

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Initialize UI elements
        taskNameEditText = findViewById(R.id.taskNameEditText);
        robotSpinner = findViewById(R.id.robotSpinner);
        submitTaskButton = findViewById(R.id.submitTaskButton);

        // Retrieve token from SharedPreferences
        token = getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);

        if (token == null || token.isEmpty()) {
            showToast("Session expired. Please log in again.");
            finish();
            return;
        }

        // Close button to exit the activity
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish()); // Exit the activity when clicked

        // Fetch robots and populate the dropdown
        fetchRobotsForDropdown();

        // Click listener to create task
        submitTaskButton.setOnClickListener(v -> createTask());
    }

    private void createTask() {
        String taskName = taskNameEditText.getText().toString().trim();
        String selectedRobot = (String) robotSpinner.getSelectedItem(); // Get selected robot name

        // Validate task name
        if (!isValidTaskName(taskName)) {
            showToast("Task must be 3-50 alphanumeric characters, spaces or underscores.");
            return;
        }


        if (selectedRobot.equals("Select a Robot")) {
            showToast("Please select a valid robot.");
            return;
        }

        // Ensure the selected robot exists in the map
        Integer robotId = robotMap.get(selectedRobot);
        if (robotId == null) {
            showToast("Invalid Robot Selection");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating task...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", taskName);
            jsonBody.put("robot_id", robotId);
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            showToast("Invalid input format");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CREATE_TASK_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    showToast("Task created successfully");
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMessage = "Error creating task";

                    if (error.networkResponse != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorResponse = new JSONObject(responseBody);
                            if (errorResponse.has("message")) {
                                errorMessage = errorResponse.getString("message");
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    showToast(errorMessage);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(request);
    }
    // Fetches robots from the database and populates the dropdown
    private void fetchRobotsForDropdown() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest robotRequest = new JsonArrayRequest(Request.Method.GET, ROBOT_LIST_URL, null,
                response -> {
                    List<String> robotNames = new ArrayList<>();
                    robotNames.add("Select a Robot");  // Default option
                    robotMap.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject robot = response.getJSONObject(i);
                            if (!robot.has("name") || !robot.has("id")) continue;

                            String robotName = robot.getString("name");
                            int robotId = robot.getInt("id");

                            robotMap.put(robotName, robotId);
                            robotNames.add(robotName);
                        }

                        ArrayAdapter<String> robotAdapter = new ArrayAdapter<>(
                                CreateTaskActivity.this, android.R.layout.simple_spinner_item, robotNames);
                        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        robotSpinner.setAdapter(robotAdapter);
                    } catch (JSONException e) {
                        showToast("Failed to parse robot data.");
                    }
                },
                error -> {
                    showToast("Failed to fetch robots. Check your connection.");
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
    }


    // Custom method to handle Toast messages
    private void showToast(String message) {
        if (activeToast != null) {
            activeToast.cancel(); // Cancel previous toast if it exists
        }
        activeToast = Toast.makeText(CreateTaskActivity.this, message, Toast.LENGTH_SHORT);
        activeToast.show();
    }
}
