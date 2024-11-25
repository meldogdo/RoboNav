package com.robonav.app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);
        TextView signUpText = findViewById(R.id.signUpText);

        // Underline the "Forgot Password?" and "Sign Up" text
        forgotPasswordText.setPaintFlags(forgotPasswordText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signUpText.setPaintFlags(signUpText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Pre-fill username if provided
        Intent intent = getIntent();
        String prefilledUsername = intent.getStringExtra("username");
        if (prefilledUsername != null && !prefilledUsername.isEmpty()) {
            usernameEditText.setText(prefilledUsername);
        }

        // Set onClickListener for the login button
        loginButton.setOnClickListener(v -> {
            // Get input values
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate inputs
            if (!areInputsValid(username, password)) return;

            // Simulate successful login and navigate to HomeActivity
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            homeIntent.putExtra("username", username); // Pass username to the next activity
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish(); // Close MainActivity to prevent going back to login
        });

        // Set onClickListener for the "Forgot Password?" text
        forgotPasswordText.setOnClickListener(v -> {
            // Navigate to Forgot Password screen
            Intent forgotPasswordIntent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotPasswordIntent);
        });

        // Set onClickListener for the "Sign Up" text
        signUpText.setOnClickListener(v -> {
            // Navigate to Sign Up screen
            Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
        });
    }

    // Validate username and password
    private boolean areInputsValid(String username, String password) {
        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate username (4–20 alphanumeric characters)
        if (!isValidUsername(username)) {
            Toast.makeText(this, "Username must be between 4-20 alphanumeric characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate password length
        if (!isValidPassword(password)) {
            // Check if the password is too short or too long, contains spaces, or invalid characters
            if (password.length() < 6 || password.length() > 20) {
                Toast.makeText(this, "Password must be between 6 and 20 characters.", Toast.LENGTH_SHORT).show();
            } else if (password.contains(" ")) {
                Toast.makeText(this, "Password cannot contain spaces.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Password contains invalid characters. Only letters, numbers, and special characters (@, #, !, $, %, ^, &, *, etc.) are allowed.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true; // All validations passed
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9]{4,20}$");
    }

    private boolean isValidPassword(String password) {
        // Check if password length is between 6 and 20, contains no spaces, or invalid characters
        return password.matches("^[A-Za-z0-9@#!$%^&*()_+={}\\[\\]:;\"'<>,.?/`~|-]{6,20}$");
    }

}
