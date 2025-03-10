package com.robonav.app.adapters;

import static com.robonav.app.models.Robot.getTaskInProgress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robonav.app.R;
import com.robonav.app.models.Robot;
import com.robonav.app.models.Task;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class RobotAdapter extends RecyclerView.Adapter<RobotAdapter.RobotViewHolder> {

    private final Context context;
    private final List<Robot> robotList;
    private final List<Task> taskList;

    public RobotAdapter(Context context, List<Robot> robotList, List<Task> taskList) {
        this.context = context;
        this.robotList = robotList;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public RobotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.robot_item, parent, false);
        return new RobotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RobotViewHolder holder, int position) {
        Robot robot = robotList.get(position);

        // Bind data to the robot card
        holder.nameTextView.setText(robot.getName());
        holder.pingTextView.setText("IP: " + robot.getIpAdd());

        holder.locationTextView.setText("Location: " + robot.getLocationName());

        // Get active task for the robot
        Task activeTask = getTaskInProgress(robot, taskList);

        // Safely handle the case where no task is in progress
        if (activeTask != null) {
            holder.taskTextView.setText("Task: " + activeTask.getName());
        } else {
            holder.taskTextView.setText("Task: None");
        }

        // Update battery icon based on percentage or charging status
        int batteryPercentage = robot.getBattery();

        // Check if the robot is charging
        if (robot.getIsCharging() == 1) {
            // If the robot is charging, display the charging icon
            holder.batteryIcon.setImageResource(R.drawable.charging);
            holder.batteryTextView.setText("Charging (Battery: " + robot.getBattery() + "%)");
        } else {
            holder.batteryTextView.setText("Battery: " + robot.getBattery() + "%");
            // Otherwise, update based on battery percentage
            if (batteryPercentage > 75) {
                holder.batteryIcon.setImageResource(R.drawable.ic_full_battery);
            } else if (batteryPercentage > 25) {
                holder.batteryIcon.setImageResource(R.drawable.ic_half_battery);
            } else {
                holder.batteryIcon.setImageResource(R.drawable.ic_empty_battery);
            }
        }

        // Show popup on click
        holder.itemView.setOnClickListener(view -> showRobotPopup(view, robot));
    }

    @Override
    public int getItemCount() {
        return robotList.size();
    }

    // ViewHolder class
    static class RobotViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, pingTextView, batteryTextView, taskTextView, locationTextView;
        ImageView batteryIcon; // Add reference to the battery icon ImageView

        public RobotViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.robot_name);
            pingTextView = itemView.findViewById(R.id.robot_ping);
            batteryTextView = itemView.findViewById(R.id.robot_battery);
            taskTextView = itemView.findViewById(R.id.robot_task);
            locationTextView = itemView.findViewById(R.id.robot_location);
            batteryIcon = itemView.findViewById(R.id.robot_battery_icon); // Initialize battery icon
        }
    }

    // Show popup method
    private void showRobotPopup(View anchorView, Robot robot) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.robot_popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Initialize UI components
        TextView titleView = popupView.findViewById(R.id.popup_title);
        TextView taskNameView = popupView.findViewById(R.id.task_progress_title); // Removed ProgressBar reference
        TextView taskPercentageView = popupView.findViewById(R.id.task_start);
        ImageView swipeDownIcon = popupView.findViewById(R.id.swipe_down_icon);
        TextView batteryPercentageText = popupView.findViewById(R.id.battery_percentage_text);
        TextView locationDetails = popupView.findViewById(R.id.location_details);
        TextView positionDetails = popupView.findViewById(R.id.position_details);
        TextView ipDetails = popupView.findViewById(R.id.ip_details);
        ProgressBar progressBar = popupView.findViewById(R.id.progress_bar); // ProgressBar reference

        // Assuming the Robot object has a method getLocation() that returns the current location
        locationDetails.setText(robot.getLocationName());
        positionDetails.setText(robot.getLocationCoordinates());
        ipDetails.setText(robot.getIpAdd());
        int batteryPercentage = robot.getBattery();
        titleView.setText(robot.getName());



        // Check if the robot is charging
        if (robot.getIsCharging() == 1) {
            // If the robot is charging, display the charging status
            batteryPercentageText.setText("Charging (Battery Percentage: " + batteryPercentage + "%)");

            // Pulse animation for the progress bar
            ValueAnimator pulseAnimator = ValueAnimator.ofInt(0, batteryPercentage);
            pulseAnimator.setDuration(1500);  // Duration of one pulse animation
            pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // Smooth acceleration and deceleration

            // Update the progress bar in the animation
            pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int progress = (int) animation.getAnimatedValue();
                    progressBar.setProgress(progress);
                }
            });

            // Reset the animation to repeat
            pulseAnimator.setRepeatMode(ValueAnimator.RESTART);
            pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);

            // Start the pulse animation
            pulseAnimator.start();

        } else {
            // If not charging, display the battery percentage and set the progress bar
            batteryPercentageText.setText("Battery Percentage: " + batteryPercentage + "%");

            // Set the ProgressBar progress directly to the battery percentage
            progressBar.setProgress(batteryPercentage);
        }




        // Set robot name
        titleView.setText(robot.getName());

        // Update task progress details
        Task activeTask = getTaskInProgress(robot, this.taskList);

        if (activeTask != null) {
            taskNameView.setText("Active Task");
            String dateCreated = !Objects.equals(activeTask.getDateCreated(), "null") ? activeTask.getDateCreated() : "Unknown";
            taskPercentageView.setText("Task: " + activeTask.getName() + "\nStarted: " + dateCreated);

        } else {
            taskNameView.setText("Active Task");
            taskPercentageView.setText("No task in progress");
        }

        // Swipe down icon to close the popup
        swipeDownIcon.setOnClickListener(v -> dismissWithAnimation(popupView, popupWindow));

        // Close popup when clicked outside
        popupWindow.setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isInsideViewBounds(popupView, event)) {
                    dismissWithAnimation(popupView, popupWindow);
                    return true;
                }
            }
            return false;
        });

        // Show popup with animation
        popupView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up));
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }


    private void dismissWithAnimation(View popupView, PopupWindow popupWindow) {
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                popupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
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
}
