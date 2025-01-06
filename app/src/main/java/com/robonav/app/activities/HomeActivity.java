package com.robonav.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.robonav.app.fragments.HomeFragment;
import com.robonav.app.fragments.NavigationFragment;
import com.robonav.app.fragments.GeoVisualizationFragment;


import com.robonav.app.R;
import com.robonav.app.fragments.MapFragment;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setting window navbar colour
        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.gray_medium));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the username from the Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        // Initialize the ViewPager2
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Connect the toolbar to the activity
        Objects.requireNonNull(getSupportActionBar()).setTitle("RoboNav Dashboard");
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("Welcome to RoboNav, " + username + "!");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.gray_high_contrast));

        // Ensure the changes are visible as soon as the window is in focus
        toolbar.post(() -> {
            // Update the title and subtitle immediately after the layout is drawn
            Objects.requireNonNull(getSupportActionBar()).setTitle("RoboNav Dashboard");
            Objects.requireNonNull(getSupportActionBar()).setSubtitle("Welcome to RoboNav, " + username + "!");
        });

        // Set the adapter for ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Use if-else to select the correct fragment
                if (position == 0) {
                    return new HomeFragment(); // Fragment 1: Home
                } else if (position == 1) {
                    return new MapFragment(); // Fragment 2: Map
                } else if (position == 2) {
                    return new NavigationFragment(); // Fragment 3: Navigation
                }
                else if (position == 3) {
                    return new GeoVisualizationFragment(); // Fragment 3: Geo Visualization
                }
                else {
                    return new HomeFragment(); // Default Fragment
                }
            }

            @Override
            public int getItemCount() {
                return 4; // Number of fragments you want to swipe between
            }
        });

        // Sync ViewPager2 with BottomNavigationView using if-else
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Switch fragments using if-else
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_map);
                } else if (position == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_navigation);
                } else if (position == 3) {
                    bottomNavigationView.setSelectedItemId(R.id.geo_visualization);
                }

            }
        });

        // Sync BottomNavigationView with ViewPager2 using if-else
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the ID of the clicked item

            // Use if-else for checking navigation items
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0); // Navigate to HomeFragment
                return true;
            } else if (itemId == R.id.nav_map) {
                viewPager.setCurrentItem(1); // Navigate to MapFragment
                return true;
            } else if (itemId == R.id.nav_navigation) {
                viewPager.setCurrentItem(2); // Navigate to NavigationFragment
                return true;
            } else if (itemId == R.id.geo_visualization) {
                viewPager.setCurrentItem(3); // Navigate to Geo-Visualization fragment
                return true;
            }
            else {
                return false; // Return false if no matching ID
            }
        });

        // Set OnClickListener for the logout button (use if-else)
        findViewById(R.id.signOutIcon).setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    // Method to show the logout confirmation dialog
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logout())  // If user clicks "Yes", log them out
                .setNegativeButton("No", null)  // If user clicks "No", just dismiss the dialog
                .show();
    }

    // Method to log the user out and navigate to MainActivity
    private void logout() {
        // Optionally, clear any saved session or user data (e.g., SharedPreferences or clearing user session)

        // Navigate to MainActivity
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack so the user cannot go back to HomeActivity
        startActivity(intent);
        finish(); // Finish HomeActivity so the user cannot return to it
    }
}
