package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText, resetCodeEditText;
    private Button submitButton, verifyButton;
    private TextView backToLoginText, resetCodeLabel;
    private String email;  // Stores email for later verification
    private Toast currentToast; // Store the latest toast reference

    private static final String REQUEST_RESET_URL = "http://10.0.2.2:8080/api/open/users/request-reset";
    private static final String VERIFY_RESET_URL = "http://10.0.2.2:8080/api/open/users/verify-reset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText);
        resetCodeEditText = findViewById(R.id.resetCodeEditText);
        submitButton = findViewById(R.id.submitButton);
        verifyButton = findViewById(R.id.verifyButton);
        backToLoginText = findViewById(R.id.backToLoginText);
        resetCodeLabel = findViewById(R.id.resetCodeLabel);

        // Hide the verification UI initially
        resetCodeEditText.setVisibility(View.GONE);
        verifyButton.setVisibility(View.GONE);
        resetCodeLabel.setVisibility(View.GONE);

        // Send Reset Code
        submitButton.setOnClickListener(v -> requestResetCode());

        // Verify Reset Code
        verifyButton.setOnClickListener(v -> verifyResetCode());

        // Back to login
        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        backToLoginText.setPaintFlags(backToLoginText.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
    }

    // Function to send reset code
    private void requestResetCode() {
        email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("Please enter your email");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending reset code...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REQUEST_RESET_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();

                    try {
                        String message = response.getString("message");
                        showToast(message);
                    } catch (JSONException e) {
                        showToast("Reset code sent to email");
                    }

                    // Show the reset code input & verify button
                    resetCodeEditText.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);
                    resetCodeLabel.setVisibility(View.VISIBLE);

                    // Disable submit button to prevent multiple requests
                    submitButton.setEnabled(false);
                },
                error -> {
                    progressDialog.dismiss();

                    String errorMessage = "Error sending reset code";

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
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Function to verify the reset code
    private void verifyResetCode() {
        String resetCode = resetCodeEditText.getText().toString().trim();

        if (email.isEmpty() || resetCode.isEmpty()) {
            showToast("Enter your email and reset code");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying reset code...");
        progressDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("resetCode", resetCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, VERIFY_RESET_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    try {
                        if (response.has("token")) {
                            String token = response.getString("token");

                            showToast("Code Verified. Redirecting to Reset Password");

                            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("token", token);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Verification failed. Try again.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();

                    String errorMessage = "Invalid or expired reset code";

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
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
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
