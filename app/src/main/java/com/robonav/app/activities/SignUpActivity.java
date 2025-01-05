package com.robonav.app.activities;

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

import com.robonav.app.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        // Apply underline to only the word "Login"
        String fullText = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf("Login");
        int endIndex = startIndex + "Login".length();
        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTextView.setText(spannableString);

        // Handle Sign Up button click
        signUpButton.setOnClickListener(v -> {
            // Get user inputs
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate inputs
            if (!areFieldsValid(username, email, password, confirmPassword)) return;

            // Simulate saving user data and sending a verification email
            Toast.makeText(SignUpActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();

            // Pass the signed-up username to MainActivity
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.putExtra("username", username); // Add the username to the intent
            startActivity(intent);
            finish(); // Close the SignUpActivity
        });

        // Handle Login text click
        loginTextView.setOnClickListener(v -> {
            // Navigate back to MainActivity
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Validate all fields
    private boolean areFieldsValid(String username, String email, String password, String confirmPassword) {
        // Check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate username (4–20 alphanumeric characters)
        if (!isValidUsername(username)) {
            Toast.makeText(this, "Username must be between 4-20 alphanumeric characters.", Toast.LENGTH_LONG).show();
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

        // Validate email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate matching passwords
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // All validations passed
    }

    // Validate the username
    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9]{4,20}$");
    }

    private boolean isValidPassword(String password) {
        // Check if password length is between 6 and 20, contains no spaces, or invalid characters
        return password.matches("^[A-Za-z0-9@#!$%^&*()_+={}\\[\\]:;\"'<>,.?/`~|-]{6,20}$");
    }
}