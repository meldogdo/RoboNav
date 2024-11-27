package com.robonav.app;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.View;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map); // Ensure this layout has a container for the map

        // Initialize the map fragment dynamically
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        if (mapFragment == null) {
            Log.e(TAG, "MapFragment not found. Initializing a new instance.");
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_view, mapFragment)
                    .commit();
        }

        // Initialize the map
        mapFragment.getMapAsync(googleMap -> {
            Log.d(TAG, "Map is ready.");
            setupMap(googleMap);
        });

        // Initialize the GestureDetector
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Handle scroll gestures if needed
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Handle single tap gestures if needed
                return true;
            }
        });

        // Set a custom touch listener on the map layout
        View mapContainer = findViewById(R.id.map_view);
        mapContainer.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {
                v.performClick(); // Ensure accessibility support
                return true;
            }
            return false;
        });
    }

    /**
     * Configures the map settings and adds markers.
     *
     * @param googleMap The GoogleMap object to configure.
     */
    private void setupMap(GoogleMap googleMap) {
        // Basic map setup
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        // Add city markers
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        addCityMarker(googleMap, boundsBuilder, new LatLng(6.5244, 3.3792), "Lagos", "Nigeria's largest city");
        addCityMarker(googleMap, boundsBuilder, new LatLng(9.0579, 7.4951), "Abuja", "Capital of Nigeria");
        addCityMarker(googleMap, boundsBuilder, new LatLng(12.0022, 8.5919), "Kano", "Major city in northern Nigeria");
        addCityMarker(googleMap, boundsBuilder, new LatLng(4.8156, 7.0498), "Port Harcourt", "Nigerian city known for oil industry");
        addCityMarker(googleMap, boundsBuilder, new LatLng(10.5200, 7.4403), "Kaduna", "Historic city in Nigeria");
        addCityMarker(googleMap, boundsBuilder, new LatLng(6.4473, 7.5139), "Enugu", "Coal city of Nigeria");
        addCityMarker(googleMap, boundsBuilder, new LatLng(7.3775, 3.8956), "Ibadan", "Large city in southwestern Nigeria");
        addCityMarker(googleMap, boundsBuilder, new LatLng(6.3344, 5.6212), "Benin City", "City in southern Nigeria");

        // Adjust the camera to show all markers
        LatLngBounds bounds = boundsBuilder.build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // Padding of 100px
    }

    /**
     * Adds a marker to the map and includes it in the bounds for camera adjustment.
     *
     * @param googleMap    The GoogleMap object.
     * @param boundsBuilder The LatLngBounds.Builder object for defining map bounds.
     * @param position     The position of the marker.
     * @param title        The title of the marker.
     * @param snippet      The snippet/description of the marker.
     */
    private void addCityMarker(GoogleMap googleMap, LatLngBounds.Builder boundsBuilder,
                               LatLng position, String title, String snippet) {
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet));
        boundsBuilder.include(position);
    }
}
