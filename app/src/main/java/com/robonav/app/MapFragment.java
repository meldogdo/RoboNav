package com.robonav.app;

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

import java.text.SimpleDateFormat;
import java.util.Date;

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

        // Create and populate the adapter for the Spinner
        String[] robotOptions = {"Robot 1", "Robot 2", "Robot 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, robotOptions);
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

        // Set click listeners to toggle visibility for map and output layouts
        outputTextView.setOnClickListener(v -> {
            outputContentLayout.setVisibility(outputContentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        mapTextView.setOnClickListener(v -> {
            mapContentLayout.setVisibility(mapContentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        return view;
    }

    // Method to dynamically add a TextView to the output_content_layout with timestamp
// Method to dynamically add a TextView to the output_content_layout with timestamp
    private void addTextViewToOutput(LinearLayout outputContentLayout, String message) {
        // Create a new TextView
        TextView newTextView = new TextView(getContext());

        // Get the current date and time using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());

        // Set the text of the new TextView with the timestamp, message, and newline
        newTextView.setText("[" + currentDateAndTime + "] - " + message);

        // Set text size, color, etc.
        newTextView.setTextSize(16f);
        newTextView.setTextColor(getResources().getColor(android.R.color.white));

        // Set margins programmatically
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Set margins (left, top, right, bottom)
        layoutParams.setMargins(0, 15, 0, 15);
        newTextView.setLayoutParams(layoutParams);

        // Add the TextView to the output layout
        outputContentLayout.addView(newTextView);
    }


    // Method to clear all TextViews from the output_content_layout
// Method to clear only the dynamically added TextViews
// Method to clear only the dynamically added TextViews, keeping the "Clear Output" button intact
    private void clearOutputText(LinearLayout outputContentLayout) {
        // Loop through all the child views in the outputContentLayout
        for (int i = 0; i < outputContentLayout.getChildCount(); i++) {
            View child = outputContentLayout.getChildAt(i);

            // Check if the child view is a TextView and not the "Clear Output" button
            if (child instanceof TextView && child.getId() != R.id.button_clear_output) {
                outputContentLayout.removeView(child);
                i--; // Adjust the index to account for the removed view
            }
        }
    }


}
