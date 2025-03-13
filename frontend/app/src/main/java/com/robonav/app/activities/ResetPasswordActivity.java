package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button resetPasswordButton;
    private String email, token;
    private Toast currentToast; // Store the latest toast reference

    private static final String RESET_PASSWORD_URL = "http://10.0.2.2:8080/api/protected/users/reset-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Retrieve email and token from the intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        token = intent.getStringExtra("token");

        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!arePasswordsValid(newPassword, confirmPassword)) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RESET_PASSWORD_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();

                    try {
                        // Get the message from the response JSON
                        String message = response.getString("message");
                        showToast(message);
                    } catch (JSONException e) {
                        showToast("Password reset successful");
                    }

                    // Redirect to login
                    Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressDialog.dismiss();

                    // Default error message
                    String errorMessage = "Error resetting password";

                    if (error.networkResponse != null) {
                        try {
                            // Convert response data to JSON and extract the message
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorResponse = new JSONObject(responseBody);
                            if (errorResponse.has("message")) {
                                errorMessage = errorResponse.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Display the extracted message in the toast
                    showToast(errorMessage);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token); // Use reset token
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private boolean arePasswordsValid(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill in both fields");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
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
