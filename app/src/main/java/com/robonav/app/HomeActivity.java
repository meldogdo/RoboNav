package com.robonav.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the ViewPager2
        viewPager = findViewById(R.id.view_pager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the adapter for ViewPager2
        FragmentStateAdapter adapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return new HomeFragment(); // Fragment 1: Home
                } else if (position == 1) {
                    return new MapFragment(); // Fragment 2: Map
                } else if (position == 2) {
                    return new NavigationFragment(); // Fragment 3: Navigation
                } else {
                    return new HomeFragment(); // Default Fragment
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Number of fragments you want to swipe between
            }
        };

        // Set the adapter on ViewPager2
        viewPager.setAdapter(adapter);

        // Sync ViewPager2 with BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_map);
                } else if (position == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_navigation);
                }
            }
        });

        // Sync BottomNavigationView with ViewPager2
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the ID of the clicked item

            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0); // Navigate to HomeFragment
                return true;
            } else if (itemId == R.id.nav_map) {
                viewPager.setCurrentItem(1); // Navigate to MapFragment
                return true;
            } else if (itemId == R.id.nav_navigation) {
                viewPager.setCurrentItem(2); // Navigate to NavigationFragment
                return true;
            } else {
                return false; // Return false if no matching ID
            }
        });
    }
}
