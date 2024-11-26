package com.robonav.app;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map); // XML layout with FragmentContainerView

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // Initialize the map fragment dynamically
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        transaction.replace(R.id.map_view, mapFragment);
        transaction.commit();

        // Initialize the map
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // Basic map setup
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls
                googleMap.getUiSettings().setCompassEnabled(true); // Enable compass
                googleMap.getUiSettings().setMyLocationButtonEnabled(true); // Enable my location button
                googleMap.getUiSettings().setMapToolbarEnabled(true); // Enable map toolbar
                googleMap.getUiSettings().setRotateGesturesEnabled(true); // Enable rotation
                googleMap.getUiSettings().setTiltGesturesEnabled(true); // Enable tilt






                // Add markers for various cities in Nigeria
                LatLng lagos = new LatLng(6.5244, 3.3792); // Lagos coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(lagos)
                        .title("Lagos")
                        .snippet("Nigeria's largest city"));

                LatLng abuja = new LatLng(9.0579, 7.4951); // Abuja coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(abuja)
                        .title("Abuja")
                        .snippet("Capital of Nigeria"));

                LatLng kano = new LatLng(12.0022, 8.5919); // Kano coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(kano)
                        .title("Kano")
                        .snippet("Major city in northern Nigeria"));

                LatLng portHarcourt = new LatLng(4.8156, 7.0498); // Port Harcourt coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(portHarcourt)
                        .title("Port Harcourt")
                        .snippet("Nigerian city known for oil industry"));

                LatLng kaduna = new LatLng(10.5200, 7.4403); // Kaduna coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(kaduna)
                        .title("Kaduna")
                        .snippet("Historic city in Nigeria"));

                LatLng enugu = new LatLng(6.4473, 7.5139); // Enugu coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(enugu)
                        .title("Enugu")
                        .snippet("Coal city of Nigeria"));

                LatLng ibadan = new LatLng(7.3775, 3.8956); // Ibadan coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(ibadan)
                        .title("Ibadan")
                        .snippet("Large city in southwestern Nigeria"));

                LatLng beninCity = new LatLng(6.3344, 5.6212); // Benin City coordinates
                googleMap.addMarker(new MarkerOptions()
                        .position(beninCity)
                        .title("Benin City")
                        .snippet("City in southern Nigeria"));

                // Set up LatLngBounds to ensure all markers are visible
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(lagos);
                builder.include(abuja);
                builder.include(kano);
                builder.include(portHarcourt);
                builder.include(kaduna);
                builder.include(enugu);
                builder.include(ibadan);
                builder.include(beninCity);

                LatLngBounds bounds = builder.build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // Add padding of 100px

                // Optional: Set a default zoom level (in case you want a specific zoom)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lagos, 5)); // Start zoomed in on Lagos
            }
        });

        // Initialize the GestureDetector
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                return true;
            }

            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                return true;
            }

        });

        // Set the custom touch listener on the map container
        View mapContainer = findViewById(R.id.map_layout);
        mapContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Pass the touch event to the gesture detector
                if (gestureDetector.onTouchEvent(event)) {
                    v.performClick(); // Ensure accessibility support (call performClick when a click is detected)
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
