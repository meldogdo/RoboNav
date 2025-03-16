package com.robonav.app.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import static com.robonav.app.models.Robot.getTaskInProgress;
import static com.robonav.app.utilities.FragmentUtils.showMessage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import com.robonav.app.interfaces.OnUpdateListener;
import com.robonav.app.models.Robot;
import com.robonav.app.models.Task;
import com.robonav.app.utilities.ConfigManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RobotAdapter extends RecyclerView.Adapter<RobotAdapter.RobotViewHolder> {

    private final Context context;
    private final List<Robot> robotList;
    private final List<Task> taskList;
    private final OnUpdateListener onUpdateListener;
    private final String token;
    private static final String TAG = "RobotAdapter";

    public RobotAdapter(Context context, List<Robot> robotList, List<Task> taskList, OnUpdateListener onUpdateListener) {
        this.context = context;
        this.robotList = robotList;
        this.taskList = taskList;
        this.onUpdateListener = onUpdateListener;

        // Retrieve token from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        this.token = prefs.getString("JWT_TOKEN", null);
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

        Task activeTask = Robot.getTaskInProgress(robot, taskList);
        holder.taskTextView.setText(activeTask != null ? "Task: " + activeTask.getName() : "Task: None");

        int batteryPercentage = robot.getBattery();
        holder.batteryTextView.setText(robot.getIsCharging() == 1 ?
                "Charging (Battery: " + batteryPercentage + "%)" :
                "Battery: " + batteryPercentage + "%");

        holder.batteryIcon.setImageResource(batteryPercentage > 75 ? R.drawable.ic_full_battery :
                (batteryPercentage > 25 ? R.drawable.ic_half_battery : R.drawable.ic_empty_battery));

        // Show popup when robot is clicked
        holder.itemView.setOnClickListener(view -> showRobotPopup(view, robot, position));
    }

    @Override
    public int getItemCount() {
        return robotList.size();
    }

    static class RobotViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, pingTextView, batteryTextView, taskTextView, locationTextView;
        ImageView batteryIcon;

        public RobotViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.robot_name);
            pingTextView = itemView.findViewById(R.id.robot_ping);
            batteryTextView = itemView.findViewById(R.id.robot_battery);
            taskTextView = itemView.findViewById(R.id.robot_task);
            locationTextView = itemView.findViewById(R.id.robot_location);
            batteryIcon = itemView.findViewById(R.id.robot_battery_icon);
        }
    }

    private void showRobotPopup(View anchorView, Robot robot, int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.robot_popup_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        TextView titleView = popupView.findViewById(R.id.popup_title);
        TextView locationDetails = popupView.findViewById(R.id.location_details);
        TextView positionDetails = popupView.findViewById(R.id.position_details);
        TextView ipDetails = popupView.findViewById(R.id.ip_details);
        int batteryPercentage = robot.getBattery();
        TextView batteryPercentageText = popupView.findViewById(R.id.battery_percentage_text);
        ImageView swipeDownIcon = popupView.findViewById(R.id.swipe_down_icon);
        ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
        Button deleteButton = popupView.findViewById(R.id.delete_robot_button);
        TextView taskNameView = popupView.findViewById(R.id.task_progress_title); // Removed ProgressBar reference
        TextView taskPercentageView = popupView.findViewById(R.id.task_start);


        locationDetails.setText(robot.getLocationName());
        positionDetails.setText(robot.getLocationCoordinates());
        ipDetails.setText(robot.getIpAdd());
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
        deleteButton.setOnClickListener(v -> {
            deleteRobot(Integer.parseInt(robot.getId()), position);
            popupWindow.dismiss();
        });

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

        // Apply the correct slide-up animation and then show the popup
        popupView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up));
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }

    private void deleteRobot(int robotId, int position) {
        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/" + robotId + "/delete";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        showMessage(message, context);
                    } catch (JSONException e) {
                        showMessage("Robot deleted successfully", context);
                    }
                    robotList.remove(position);
                    notifyItemRemoved(position);
                    if (onUpdateListener != null) {
                        onUpdateListener.onUpdate();
                    }
                },
                error -> {
                    try {
                        String errorMsg = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorMsg);
                        String message = errorJson.getString("message");
                        showMessage(message, context);
                    } catch (Exception e) {
                        showMessage("Failed to delete robot", context);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void dismissWithAnimation(View popupView, PopupWindow popupWindow) {
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
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
}
