package com.robonav.app;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapFragment extends Fragment {

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Get references to the views
        TextView outputTextView = view.findViewById(R.id.output_text_view);
        LinearLayout outputContentLayout = view.findViewById(R.id.output_content_layout);
        TextView mapTextView = view.findViewById(R.id.map_text_view);
        FrameLayout mapContentLayout = view.findViewById(R.id.map_view);

        // Get reference to the Spinner
        Spinner spinnerBotSelect = view.findViewById(R.id.spinner_bot_select);

        // Load robot names from the JSON file in assets
        List<String> robotNames = loadRobotNamesFromJson();

        // Create and populate the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, robotNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBotSelect.setAdapter(adapter);

        // Get the buttons
        Button checkPositionButton = view.findViewById(R.id.button_check_position);
        Button saveLocationButton = view.findViewById(R.id.button_save_current_location);
        Button setPositionButton = view.findViewById(R.id.button_set_initial_position);
        Button clearOutputButton = view.findViewById(R.id.button_clear_output);

        // Set click listeners for each button
        clearOutputButton.setOnClickListener(v -> {
            clearOutputText(outputContentLayout);
        });

        checkPositionButton.setOnClickListener(v -> {
            String selectedBot = spinnerBotSelect.getSelectedItem().toString();

            addTextViewToOutput(outputContentLayout, selectedBot + ':' + " Check Position ");
        });

        saveLocationButton.setOnClickListener(v -> {
            String selectedBot = spinnerBotSelect.getSelectedItem().toString();

            addTextViewToOutput(outputContentLayout, selectedBot + ':' + " Save Current Location");
        });

        setPositionButton.setOnClickListener(v -> {
            String selectedBot = spinnerBotSelect.getSelectedItem().toString();

            addTextViewToOutput(outputContentLayout, selectedBot + ':' + " Set Initial Position At Coordinates ___ ___");
        });

        return view;
    }

    // Method to read robots.json from the assets folder
    private List<String> loadRobotNamesFromJson() {
        List<String> robotNames = new ArrayList<>();
        try {
            // Open the file using AssetManager
            AssetManager assetManager = requireContext().getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("robots.json")));

            // Read the file content into a StringBuilder
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse the JSON content
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject robotObject = jsonArray.getJSONObject(i);
                String robotName = robotObject.getString("name");
                robotNames.add(robotName);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return robotNames;
    }

    // Method to dynamically add a TextView to the output_content_layout with timestamp
    private void addTextViewToOutput(LinearLayout outputContentLayout, String message) {
        TextView newTextView = new TextView(getContext());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        newTextView.setText("[" + currentDateAndTime + "] - " + message);
        newTextView.setTextSize(16f);
        newTextView.setTextColor(getResources().getColor(android.R.color.white));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 15, 0, 15);
        newTextView.setLayoutParams(layoutParams);

        outputContentLayout.addView(newTextView);
    }

    // Method to clear only the dynamically added TextViews
    private void clearOutputText(LinearLayout outputContentLayout) {
        outputContentLayout.removeAllViews();
    }
}
