package com.robonav.app.activities;

import static com.robonav.app.utilities.FragmentUtils.VALID;
import static com.robonav.app.utilities.FragmentUtils.areInputsValid;
import static com.robonav.app.utilities.FragmentUtils.arePasswordsValid;
import static com.robonav.app.utilities.FragmentUtils.showMessage;

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

import com.android.volley.DefaultRetryPolicy;
import com.robonav.app.utilities.ConfigManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import com.robonav.app.utilities.FragmentUtils;
import com.robonav.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Toast currentToast; // Store the latest toast reference

    private Button signUpButton;

    private static final String REGISTER_URL = ConfigManager.getBaseUrl() + "/api/open/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        TextView loginTextView = findViewById(R.id.loginTextView);
        signUpButton = findViewById(R.id.signUpButton);

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
        if (signUpButton.isEnabled()) {
            signUpButton.setEnabled(false);  // Prevent multiple clicks
        } else {
            return; // Prevent duplicate calls
        }

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate username, email, and password
        int validationCode = areInputsValid(username, password, email);

        if (validationCode != VALID) {
            signUpButton.setEnabled(true); // Re-enable if validation fails
            switch (validationCode) {
                case FragmentUtils.EMPTY_FIELDS:
                    showMessage("Please fill in all fields.",this);
                    break;
                case FragmentUtils.INVALID_USERNAME:
                    showMessage("Invalid username. Must be 4-20 alphanumeric characters.",this);
                    break;
                case FragmentUtils.INVALID_PASSWORD:
                    showMessage("Invalid password. Must be 6-20 characters.",this);
                    break;
                case FragmentUtils.INVALID_EMAIL:
                    showMessage("Invalid email address.", this);
                    break;
            }
            return;
        }

        // Check if passwords match before validating strength
        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", this);
            signUpButton.setEnabled(true);
            return;
        }

        // Validate password strength
        if (!arePasswordsValid(password, confirmPassword, null,msg -> showMessage(msg,this) )) {
            signUpButton.setEnabled(true);
            return;
        }

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        // Prepare request payload
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    signUpButton.setEnabled(true);

                    try {
                        showMessage(response.getString("message"),this);
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        showMessage("Error parsing response",this);
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    signUpButton.setEnabled(true);

                    String errorMessage = "Registration failed. Try again.";
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
                    showMessage(errorMessage,this);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setTag("registerRequest");
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        queue.cancelAll("registerRequest");  // Cancel any ongoing registration requests
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
