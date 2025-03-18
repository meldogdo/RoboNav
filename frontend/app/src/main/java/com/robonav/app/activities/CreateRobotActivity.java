package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import static com.robonav.app.utilities.FragmentUtils.*;
public class CreateRobotActivity extends AppCompatActivity {

    private EditText robotModelEditText, ipAddressEditText, portEditText;
    private Button submitRobotButton;
    private String token;
    private static final String CREATE_ROBOT_URL = ConfigManager.getBaseUrl() + "/api/protected/robot/create";

    private static Toast activeToast; // Global Toast instance to prevent queuing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_robot);

        // Initialize UI elements
        robotModelEditText = findViewById(R.id.robotModelEditText);
        ipAddressEditText = findViewById(R.id.ipAddressEditText);
        portEditText = findViewById(R.id.portEditText);
        submitRobotButton = findViewById(R.id.submitRobotButton);

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

        // Click listener to create robot
        submitRobotButton.setOnClickListener(v -> createRobot());
    }

    private void createRobot() {
        String robotModel = robotModelEditText.getText().toString().trim();
        String ipAddress = ipAddressEditText.getText().toString().trim();
        String port = portEditText.getText().toString().trim();

        // Validate fields
        if (!isValidRobotModel(robotModel)) {
            showToast("Model must be 3-50 alphanumeric characters, spaces or underscores.");
            return;
        }
        if (!isValidIpAddress(ipAddress)) {
            showToast("Invalid IP. Enter a valid IPv4 (e.g., 192.168.1.1).");
            return;
        }
        if (!isValidPort(port)) {
            showToast("Invalid port. Enter a number between 1 and 65535.");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating robot...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("type", robotModel);
            jsonBody.put("ip_add", ipAddress);
            jsonBody.put("port", port);
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            showToast("Error creating JSON request");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CREATE_ROBOT_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    showToast("Robot created successfully");
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                error -> {
                    progressDialog.dismiss();

                    String errorMessage = "Error creating robot";
                    if (error.networkResponse != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorResponse = new JSONObject(responseBody);
                            if (errorResponse.has("message")) {
                                errorMessage = errorResponse.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        activeToast = Toast.makeText(CreateRobotActivity.this, message, Toast.LENGTH_SHORT);
        activeToast.show();
    }
}
