package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CreateRobotActivity extends AppCompatActivity {

    private EditText robotNameEditText, robotModelEditText, robotTypeEditText, ipAddressEditText, portEditText;
    private Button submitRobotButton;
    private String token;
    private static final String CREATE_ROBOT_URL = "http://10.0.2.2:8080/api/protected/robot/create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_robot);

        // Initialize UI elements
        robotNameEditText = findViewById(R.id.robotNameEditText);
        robotModelEditText = findViewById(R.id.robotModelEditText);
        robotTypeEditText = findViewById(R.id.robotTypeEditText);
        ipAddressEditText = findViewById(R.id.ipAddressEditText);
        portEditText = findViewById(R.id.portEditText);
        submitRobotButton = findViewById(R.id.submitRobotButton);

        // Retrieve token from SharedPreferences
        token = getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);

        // Click listener to create robot
        submitRobotButton.setOnClickListener(v -> createRobot());
    }

    private void createRobot() {
        String robotName = robotNameEditText.getText().toString().trim();
        String robotModel = robotModelEditText.getText().toString().trim();
        String robotType = robotTypeEditText.getText().toString().trim();
        String ipAddress = ipAddressEditText.getText().toString().trim();
        String port = portEditText.getText().toString().trim();

        if (robotName.isEmpty() || robotModel.isEmpty() || robotType.isEmpty() || ipAddress.isEmpty() || port.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating robot...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", robotName);
            jsonBody.put("model", robotModel);
            jsonBody.put("type", robotType);
            jsonBody.put("ip_add", ipAddress);
            jsonBody.put("port", port);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CREATE_ROBOT_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreateRobotActivity.this, "Robot created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreateRobotActivity.this, "Error creating robot", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}