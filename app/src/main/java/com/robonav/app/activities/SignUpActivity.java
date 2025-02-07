package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonObjectRequest;
import com.robonav.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private static final String REGISTER_URL = "http://10.0.2.2:8080/api/open/users/register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        // Apply underline to "Login"
        String fullText = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf("Login");
        spannableString.setSpan(new UnderlineSpan(), startIndex, startIndex + "Login".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTextView.setText(spannableString);

        signUpButton.setOnClickListener(v -> registerUser());

        // Handle Login text click
        loginTextView.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!areFieldsValid(username, email, password, confirmPassword)) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        // Create JSON request body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send JSON request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    try {
                        Toast.makeText(SignUpActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(SignUpActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Ensure JSON request
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Validate all fields
    private boolean areFieldsValid(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!username.matches("^[a-zA-Z0-9]{4,20}$")) {
            Toast.makeText(this, "Username must be between 4-20 alphanumeric characters.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.matches("^[A-Za-z0-9@#!$%^&*()_+={}\\[\\]:;\"'<>,.?/`~|-]{6,20}$")) {
            Toast.makeText(this, "Password must be between 6-20 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}