package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;

import java.util.HashMap;
import java.util.Map;

public class VerifyResetCodeActivity extends AppCompatActivity {

    private EditText otpEditText;
    private static final String VERIFY_OTP_URL = "http://10.0.2.2:8080/api/open/users/verify-reset"; // Backend URL

    private String email; // Email from previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_reset_code_activity);

        // Get email from Intent
        email = getIntent().getStringExtra("email");

        // Initialize views
        otpEditText = findViewById(R.id.otpEditText);
        Button verifyButton = findViewById(R.id.verifyButton);

        // Handle Verify button click
        verifyButton.setOnClickListener(v -> verifyOTP());
    }

    private void verifyOTP() {
        String otp = otpEditText.getText().toString().trim();

        if (otp.isEmpty()) {
            Toast.makeText(this, "Please enter the code", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, VERIFY_OTP_URL,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Code verified! Redirecting...", Toast.LENGTH_SHORT).show();

                    // Save JWT token for authenticated requests
                    SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("JWT_TOKEN", response); // Assuming the backend returns a JWT
                    editor.apply();

                    // Navigate to Reset Password page
                    Intent intent = new Intent(VerifyResetCodeActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Invalid or expired code", Toast.LENGTH_SHORT).show();
                    Log.e("VerifyOTPError", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("otp", otp);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
