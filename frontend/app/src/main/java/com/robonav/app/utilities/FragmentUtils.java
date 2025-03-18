package com.robonav.app.utilities;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import java.util.function.Consumer;


import com.robonav.app.R;
import com.robonav.app.activities.CreateTaskActivity;
import com.robonav.app.models.Robot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentUtils {
    private static Toast activeToast; // Single instance for all classes
    public static final int VALID = 0;
    public static final int EMPTY_FIELDS = 1;
    public static final int INVALID_USERNAME = 2;
    public static final int INVALID_PASSWORD = 3;
    public static final int INVALID_EMAIL = 4;

    public static void showMessage(String message, Context context) {
        if (activeToast != null) {
            activeToast.cancel(); // Cancel previous toast if it exists
        }
        activeToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        activeToast.show();
    }
    public static void appendOutput(String message, NestedScrollView scrollView, View rootView) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = formatter.format(new Date());
        // Assuming an output box exists in your layout
        if (rootView != null) {
            TextView outputBox = rootView.findViewById(R.id.output_text_view);
            String currentOutput = outputBox.getText().toString();
            if (currentOutput.isEmpty()) {
                scrollView.setVisibility(View.VISIBLE);
                outputBox.setText(currentOutput + currentTime +"\n" + message);
            }
            else {
                outputBox.setText(currentOutput + "\n\n" + currentTime + "\n" + message );

            }
            // Scroll to the bottom after updating the content

            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        }
    }


    /**
         * Validates a robot model name.
         */
        public static boolean isValidRobotModel(String model) {
            return model.length() >= 3 && model.length() <= 50 && model.matches("^[a-zA-Z0-9 _]+$");
        }

        /**
         * Validates an IP address.
         * - Must be a valid IPv4 format (xxx.xxx.xxx.xxx).
         */
        public static boolean isValidIpAddress(String ip) {
            return ip.matches("^((25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9][0-9]?)$");
        }

        /**
         * Validates a port number.
         * - Must be numeric and between 1-65535.
         */
        public static boolean isValidPort(String port) {
            try {
                int portNum = Integer.parseInt(port);
                return portNum >= 1 && portNum <= 65535;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        /**
         * Validates a task name.
         */
        public static boolean isValidTaskName(String taskName) {
            return taskName.length() >= 3 && taskName.length() <= 50 && taskName.matches("^[a-zA-Z0-9 _]+$");
        }

        /**
         * Validates a location name.
         */
        public static boolean isValidLocationName(String locationName) {
            return locationName.length() >= 3 && locationName.length() <= 50 && locationName.matches("^[a-zA-Z0-9 _]+$");
        }

        /**
         * Validates coordinates in the format "latitude, longitude".
         * - Latitude must be between -90 and 90.
         * - Longitude must be between -180 and 180.
         */
        public static boolean isValidCoordinates(String coordinates) {
            String regex = "^\\s*(-?\\d{1,2}(\\.\\d+)?),\\s*(-?\\d{1,3}(\\.\\d+)?)\\s*$";

            if (!coordinates.matches(regex)) {
                return false;
            }

            try {
                String[] parts = coordinates.split(",");
                double latitude = Double.parseDouble(parts[0].trim());
                double longitude = Double.parseDouble(parts[1].trim());

                return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
            } catch (NumberFormatException e) {
                return false;
            }
        }

    public static boolean arePasswordsValid(String newPassword, String confirmPassword, @Nullable String oldPassword, Consumer<String> showToast) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast.accept("All fields are required");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            showToast.accept("New passwords do not match");
            return false;
        }
        if (!newPassword.matches("^[A-Za-z0-9@#!$%^&*()_+={}\\[\\]:;\"'<>,.?/`~|-]{6,20}$")) {
            showToast.accept("Password must be between 6 and 20 characters.");
            return false;
        }
        if (oldPassword != null && oldPassword.isEmpty()) {
            showToast.accept("Old password cannot be empty");
            return false;
        }
        return true;
    }


    public static int areInputsValid(@Nullable String username, @Nullable String password, @Nullable String email) {
        // Check if at least one input is provided
        if ((username == null || username.isEmpty()) &&
                (password == null || password.isEmpty()) &&
                (email == null || email.isEmpty())) {
            return EMPTY_FIELDS;
        }

        // Validate username if it's provided
        if (username != null && !username.isEmpty() && !username.matches("^[a-zA-Z0-9]{4,20}$")) {
            return INVALID_USERNAME;
        }

        // Validate password if it's provided
        if (password != null && !password.isEmpty() && !password.matches("^[A-Za-z0-9@#!$%^&*()_+={}\\[\\]:;\"'<>,.?/`~|-]{6,20}$")) {
            return INVALID_PASSWORD;
        }

        // Validate email if it's provided
        if (email != null && !email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return INVALID_EMAIL;
        }

        return VALID; // All provided inputs are valid
    }

}
