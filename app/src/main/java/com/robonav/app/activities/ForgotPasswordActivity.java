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

    private EditText emailEditText;
    private static final String REQUEST_RESET_URL = "http://10.0.2.2:8080/api/open/users/request-reset"; // Backend URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        Button submitButton = findViewById(R.id.submitButton);
        TextView backToLoginText = findViewById(R.id.backToLoginText);

        // Underline "Back to Login"
        SpannableString spannableString = new SpannableString("Back to Login");
        spannableString.setSpan(new UnderlineSpan(), 8, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        backToLoginText.setText(spannableString);

        // Handle Submit button click
        submitButton.setOnClickListener(v -> requestResetCode());

        // Handle "Back to Login" text click
        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void requestResetCode() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending reset code...");
        progressDialog.show();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REQUEST_RESET_URL, jsonBody,
                    response -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Reset code sent to email", Toast.LENGTH_SHORT).show();

                        // Navigate to VerifyResetCodeActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyResetCodeActivity.class);
                        intent.putExtra("email", email); // Pass email to next activity
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error sending reset code: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

}
