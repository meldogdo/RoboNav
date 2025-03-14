package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.robonav.app.utilities.ConfigManager;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private String token;
    private Toast currentToast; // Store the latest toast reference

    private static final String CHANGE_PASSWORD_URL = ConfigManager.getBaseUrl() + "/api/protected/users/change-password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Close button to exit the activity
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish()); // Exit the activity when clicked

        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Retrieve token from SharedPreferences (since user is logged in)
        token = getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("JWT_TOKEN", null);

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!arePasswordsValid(oldPassword, newPassword, confirmPassword)) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Changing password...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("old_password", oldPassword);
            jsonBody.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CHANGE_PASSWORD_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        showToast(message);
                        finish(); // Close activity after success
                    } catch (JSONException e) {
                        showToast("Password changed successfully");
                        finish();
                    }
                },
                error -> {
                    progressDialog.dismiss();

                    String errorMessage = "Error changing password";

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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token); // Use login token
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private boolean arePasswordsValid(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("All fields are required");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            showToast("New passwords do not match");
            return false;
        }
        if (!newPassword.matches("^[A-Za-z0-9@#!$%^&*()_+={}\\[\\]:;\"'<>,.?/`~|-]{6,20}$")) {
            showToast("Password must be between 6 and 20 characters.");
            return false;
        }
        return true;
    }

    // Toast helper method to prevent toast queue buildup
    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();  // Cancel the previous toast if it exists
        }
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
