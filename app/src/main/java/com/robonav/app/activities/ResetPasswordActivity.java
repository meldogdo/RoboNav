package com.robonav.app.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
    private static final String RESET_PASSWORD_URL = "http://10.0.2.2:8080/api/open/users/reset-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!arePasswordsValid(newPassword, confirmPassword)) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password...");
        progressDialog.show();

        // Get JWT token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", "");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RESET_PASSWORD_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("ResetPasswordError", error.toString());
                    Toast.makeText(ResetPasswordActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private boolean arePasswordsValid(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            Toast.makeText(this, "Password must be between 6 and 20 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
