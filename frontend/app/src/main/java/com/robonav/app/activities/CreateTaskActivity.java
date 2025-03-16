package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import com.robonav.app.utilities.ConfigManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskNameEditText, robotIdEditText;
    private Button submitTaskButton;
    private String token;
    private static final String CREATE_TASK_URL = ConfigManager.getBaseUrl() + "/api/protected/robot/task/create";

    private static Toast activeToast; // Global Toast instance to prevent queuing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Initialize UI elements
        taskNameEditText = findViewById(R.id.taskNameEditText);
        robotIdEditText = findViewById(R.id.robotIdEditText);
        submitTaskButton = findViewById(R.id.submitTaskButton);

        // Retrieve token from SharedPreferences
        token = getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);

        // Validate token
        if (token == null || token.isEmpty()) {
            showToast("Session expired. Please log in again.");
            finish();
            return;
        }

        // Close button to exit the activity
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish()); // Exit the activity when clicked

        // Click listener to create task
        submitTaskButton.setOnClickListener(v -> createTask());
    }

    private static final String TAG = "CreateTask";

    private void createTask() {
        String taskName = taskNameEditText.getText().toString().trim();
        String robotId = robotIdEditText.getText().toString().trim();

        if (taskName.isEmpty() || robotId.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating task...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", taskName);
            jsonBody.put("robot_id", Integer.parseInt(robotId));
        } catch (JSONException | NumberFormatException e) {
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

    // Custom method to handle Toast messages
    private void showToast(String message) {
        if (activeToast != null) {
            activeToast.cancel(); // Cancel previous toast if it exists
        }
        activeToast = Toast.makeText(CreateTaskActivity.this, message, Toast.LENGTH_SHORT);
        activeToast.show();
    }
}
