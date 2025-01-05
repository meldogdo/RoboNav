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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView backToLoginText = findViewById(R.id.backToLoginText);

        // Underline the "Back to Login" text
        SpannableString spannableString = new SpannableString("Back to Login");
        spannableString.setSpan(new UnderlineSpan(), 8, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        backToLoginText.setText(spannableString);

        // Handle Submit button click
        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Validate email
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Reset password link sent", Toast.LENGTH_SHORT).show();
                // Optionally navigate back to login
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Handle "Back to Login" text click
        backToLoginText.setOnClickListener(v -> {
            // Navigate back to login page
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close ForgotPasswordActivity
        });
    }
}
