package com.robonav.app;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Find containers
        LinearLayout robotContainer = rootView.findViewById(R.id.robot_container);
        LinearLayout taskContainer = rootView.findViewById(R.id.task_container);

        // Add click listeners to robot container children
        for (int i = 0; i < robotContainer.getChildCount(); i++) {
            View child = robotContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setOnClickListener(view -> {
                    // Customize this title and progress for each robot as needed
                    showRobotPopup(view, "Robot Info", 75);
                });
            }
        }

        // Add click listeners to task container children
        for (int i = 0; i < taskContainer.getChildCount(); i++) {
            View child = taskContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setOnClickListener(view -> {
                    // Customize this title, task name, and progress for each task as needed
                    showTaskPopup(view, "Task Info", "Deliver Supplies", 50);
                });
            }
        }

        return rootView;
    }

    private void showRobotPopup(View anchorView, String title, int progress) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.robot_popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set content for the popup
        TextView titleView = popupView.findViewById(R.id.popup_title);
        ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
        TextView progressStatus = popupView.findViewById(R.id.progress_status);
        ImageView swipeDownIcon = popupView.findViewById(R.id.swipe_down_icon);

        titleView.setText(title);
        progressBar.setProgress(progress);
        progressStatus.setText("Robot Progress: " + progress + "%");

        // Handle swipe-down icon click
        swipeDownIcon.setOnClickListener(v -> dismissWithAnimation(popupView, popupWindow));

        // Set touch outside to dismiss with animation
        popupWindow.setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isInsideViewBounds(popupView, event)) {
                    dismissWithAnimation(popupView, popupWindow);
                    return true; // Consume touch event
                }
            }
            return false;
        });

        // Apply slide-up animation
        popupView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));

        // Show popup
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }

    private void dismissWithAnimation(View popupView, PopupWindow popupWindow) {
        Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                popupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        popupView.startAnimation(slideDown);
    }

    private boolean isInsideViewBounds(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();
        return x >= location[0] && x <= location[0] + view.getWidth() &&
                y >= location[1] && y <= location[1] + view.getHeight();
    }

    private void showTaskPopup(View anchorView, String title, String taskName, int progress) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.task_popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set content for the popup
        TextView titleView = popupView.findViewById(R.id.popup_title);
        ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
        TextView progressStatus = popupView.findViewById(R.id.progress_status);
        ImageView swipeDownIcon = popupView.findViewById(R.id.swipe_down_icon);

        titleView.setText(title);
        progressBar.setProgress(progress);
        progressStatus.setText("Task Progress: " + progress + "%");

        // Handle swipe-down icon click
        swipeDownIcon.setOnClickListener(v -> dismissWithAnimation(popupView, popupWindow));

        // Set touch outside to dismiss with animation
        popupWindow.setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isInsideViewBounds(popupView, event)) {
                    dismissWithAnimation(popupView, popupWindow);
                    return true; // Consume touch event
                }
            }
            return false;
        });

        // Apply slide-up animation
        popupView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));

        // Show popup
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }
}
