package com.robonav.app.utilities;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import com.robonav.app.R;
import com.robonav.app.models.Robot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentUtils {
    public static void showMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
    public static void saveLocation(Robot robot, Context context, NestedScrollView scrollView, View rootView) {
        try {
            // Step 1: Load existing locations.json
            String locationJson;
            File file = new File(context.getFilesDir(), "locations.json");
            JSONArray locations;

            if (file.exists()) {
                locationJson = JsonUtils.loadJSONFromFile(context, "locations.json");
                locations = new JSONArray(locationJson);
            } else {
                locations = new JSONArray();
            }

            // Step 2: Create new location object
            JSONObject newLocation = new JSONObject();
            newLocation.put("name", "Checkpoint for " + robot.getName());
            newLocation.put("coordinates", robot.getLocationCoordinates());
            newLocation.put("robots", new JSONArray().put(robot.getId()));

            // Step 3: Add the new location to the array
            locations.put(newLocation);

            // Step 4: Save updated JSON array back to locations.json
            JsonUtils.saveJSONToFile(context, "locations.json", locations.toString());

            // Step 5: Notify user and update UI
            showMessage("Location saved successfully.",context);
            appendOutput("Saved location for " + robot.getName() + " at coordinates: " + robot.getLocationCoordinates(), scrollView, rootView);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error saving location: " + e.getMessage(),context);
        }
    }
    // Helper method to validate coordinates format and range
    public static boolean isValidCoordinates(String coordinates) {
        // Split coordinates by comma
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            return false; // Invalid format (must have exactly 2 parts)
        }

        try {
            // Parse latitude and longitude
            float latitude = Float.parseFloat(parts[0].trim());
            float longitude = Float.parseFloat(parts[1].trim());

            // Check if latitude is within range -90 to 90 and longitude is within range -180 to 180
            return (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180);
        } catch (NumberFormatException e) {
            return false; // Invalid number format
        }
    }
}
