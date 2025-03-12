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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText, resetCodeEditText;
    private Button submitButton, verifyButton;
    private TextView backToLoginText;
    private String email;  // Stores email for later verification

    private TextView resetCodeLabel;

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
    }

    // Function to send reset code
    private void requestResetCode() {
        email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Reset code sent to email", Toast.LENGTH_SHORT).show();

                    // Show the reset code input & verify button
                    resetCodeEditText.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);
                    resetCodeLabel.setVisibility(View.VISIBLE);

                    // Disable submit button to prevent multiple requests
                    submitButton.setEnabled(false);
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error sending reset code", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Function to verify the reset code
    private void verifyResetCode() {
        String resetCode = resetCodeEditText.getText().toString().trim();

        if (email.isEmpty() || resetCode.isEmpty()) {
            Toast.makeText(this, "Enter your email and reset code", Toast.LENGTH_SHORT).show();
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
                            String token = response.getString("token");  // Extract token from response

                            Toast.makeText(this, "Code Verified. Redirecting to Reset Password", Toast.LENGTH_SHORT).show();

                            // Navigate to ResetPasswordActivity with token
                            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("email", email);  // Pass email
                            intent.putExtra("token", token);  // Pass token
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Verification failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Invalid or expired reset code", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}
