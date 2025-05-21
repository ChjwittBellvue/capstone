/*
*
*
*
 */
package com.example.capstoneweighthelper;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Checks for logged in user, if not, move to login screen
        checkForLogin();

        setContentView(R.layout.activity_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get user id and setup database object
        ButtonHandler bh = new ButtonHandler();
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("ActiveUserId", -1);
        databaseManager = new DatabaseManager(this,userId);

        // move to the summary screen if the userid is valid
        if (userId > 0) {
            databaseManager.resetEditState();
            setSummaryScreen();
        }


        // Set inputs and views
        Button settingsButton = findViewById(R.id.settings_nav);
        Button weightEntryButton = findViewById(R.id.weight_entry_nav);
        Button historyButton = findViewById(R.id.weight_history_nav);
        Button progressPictureButton = findViewById(R.id.summary_progress_picture_nav);
        Button logoutButton = findViewById(R.id.summary_logout);

        settingsButton.setOnClickListener(bh);
        weightEntryButton.setOnClickListener(bh);
        historyButton.setOnClickListener(bh);
        progressPictureButton.setOnClickListener(bh);
        logoutButton.setOnClickListener(bh);

    }

    /**
     * Note: Switch case no longer works here, ids are not considered final
     *
     * Sets button behavior
     */
    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.weight_entry_nav) {  // weight entry button
                databaseManager.resetEditState();
                moveToEntry();
            } else if (v.getId() == R.id.weight_history_nav) { // weight history button
                moveToHistory();
            } else if (v.getId() == R.id.settings_nav) { // settings button
                moveToSettings();
            } else if (v.getId() == R.id.summary_progress_picture_nav) { // progress picture button
                databaseManager.resetEditState();

                if (hasCameraPermission()) {
                    enableCamera();
                } else {
                    requestPermission();
                }
            } else if (v.getId() == R.id.summary_logout) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("ActiveUserId", -1);
                editor.commit();
                reloadSummary();
            }
        }
    }

    // Page movement
    private void moveToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void moveToHistory() {
        Intent intent = new Intent(this, WeightHistory.class);
        startActivity(intent);
    }

    private void moveToEntry() {
        Intent intent = new Intent(this, WeightEntry.class);
        startActivity(intent);
    }

    private void reloadSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Sets views
     */
    private void setSummaryScreen() {
        UserProfile userProfile = databaseManager.selectActiveUserProfile();

        setWeightMetrics(userProfile);
        calculateAndSetBmi(userProfile);
        calculateAndSetAverageWeeklyWeightLoss();
        setGreeting(userProfile);
    }

    /**
     * Handles calculations used to for views
     * @param userProfile
     */
    private void setWeightMetrics(UserProfile userProfile) {
        TextView currentWeight = findViewById(R.id.current_weight);
        TextView goalWeight = findViewById(R.id.goal_weight);
        TextView goalDate = findViewById(R.id.goal_date);

        currentWeight.setText(setWeightString(userProfile.getCurrentWeight()));
        goalWeight.setText(setWeightString(userProfile.getGoalWeight()));

        Date goal = new Date(userProfile.getGoalDate());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String goalDateAsString = df.format(goal);
        goalDate.setText(goalDateAsString);
    }

    /**
     * Sets greeting using user's first name
     * @param userProfile
     */
    private void setGreeting(UserProfile userProfile) {
        String greeting = "Hello, " + userProfile.getName() + "!";

        TextView greetingView = findViewById(R.id.greeting);
        greetingView.setText(greeting);
    }

    /**
     * BMI calculations
     * @param userProfile
     */
    private void calculateAndSetBmi(UserProfile userProfile) {
        double lbToKgConversion = 0.453592;
        double currentWeightInKg = userProfile.getCurrentWeight() * lbToKgConversion;

        double inchToMeterConversion = 0.0254;
        int heightInInches = (userProfile.getHeightFeet() * 12) + userProfile.getHeightInches();
        double heightInMeters = heightInInches * inchToMeterConversion;

        Double Bmi = currentWeightInKg / Math.pow(heightInMeters, 2);
        Double roundedBmi = Math.round(Bmi * 10)/10.0;
        String formattedBmi = roundedBmi.toString();

        TextView bmiView = findViewById(R.id.bmi);
        bmiView.setText(formattedBmi);
    }

    /**
     * Checks that camera permissions are enabled
     * @return
     */
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests camera permission if it is not enabled
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    /**
     * Starts camera activity
     */
    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /**
     * Weight string formatter
     *
     * @param weight
     * @return
     */
    private String setWeightString(Double weight) {
        return weight + " LBS";
    }

    /**
     * Average weekly weight loss calculator
     */
    private void calculateAndSetAverageWeeklyWeightLoss() {
        Long millisInAWeek = 604800000L;
        HashMap<String, Long> timeMap = databaseManager.selectMinAndMaxHistoryDate();
        Double min = timeMap.get("min").doubleValue();
        Double max = timeMap.get("max").doubleValue();
        Double difference = (Double) (max-min)/millisInAWeek;
        Double weeks = Math.ceil(difference);

        // Need avg weight for this too
        TextView avgWeightView = findViewById(R.id.avg_weekly_weight_change);
        Double averageWeight = databaseManager.getMinAndMaxWeightDifference();

        Double averageWeeklyWeightChange = averageWeight/weeks;
        Double roundedWeeklyAvgWeightChange = Math.round(averageWeeklyWeightChange * 10)/10.0;

        String avgWeightViewString = "";
        if(roundedWeeklyAvgWeightChange > 0) {
            avgWeightViewString = "+" + roundedWeeklyAvgWeightChange + " LBS";
        } else {
            avgWeightViewString = roundedWeeklyAvgWeightChange + " LBS";
        }

        avgWeightView.setText(avgWeightViewString);
    }

    /**
     * Checks if user is logged in
     */
    private void checkForLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("ActiveUserId", -1);
        if (userId == -1) {
            moveToLogin();
        }
    }

    /**
     * Moves to login page
     */
    private void moveToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}