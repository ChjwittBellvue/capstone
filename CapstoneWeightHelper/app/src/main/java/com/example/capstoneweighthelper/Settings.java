package com.example.capstoneweighthelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.UserProfile;
import com.example.capstoneweighthelper.util.Validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    TextView nameTextbox;
    TextView nameError;
    TextView goalWeightTextbox;
    TextView goalWeightError;
    TextView goalDateTextbox;
    TextView goalDateError;
    TextView genderTextbox;
    TextView feetTextbox;
    TextView feetError;
    TextView inchesTextbox;
    TextView inchesError;
    ImageView backArrow;
    Button cancelButton;
    Button saveButton;

    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Pre-set views
        initializeViews();

        // Get user info and set button behavior
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("ActiveUserId", -1);
        databaseManager = new DatabaseManager(this,userId);

        ButtonHandler bh = new ButtonHandler();
        prePopulateSettings();

        backArrow.setOnClickListener(bh);
        cancelButton.setOnClickListener(bh);
        saveButton.setOnClickListener(bh);
    }

    /**
     * Note: Switch case no longer works here, ids are not considered final
     */
    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.back_arrow || v.getId() == R.id.cancel_button) {
                moveToSummary();
            }  if (v.getId() == R.id.save_button) {
                if (validateSettings()) {
                    updateUserProfile();
                    moveToSummary();
                }
            }
        }
    }

    /**
     * Moves to the summary page
     */
    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Pre-populates textboxes
     */
    private void prePopulateSettings() {
        UserProfile userProfile = databaseManager.selectActiveUserProfile();

        nameTextbox.setText(userProfile.getName());
        goalWeightTextbox.setText(userProfile.getGoalWeight().toString());
        genderTextbox.setText(userProfile.getGender());
        feetTextbox.setText(userProfile.getHeightFeet().toString());
        inchesTextbox.setText(userProfile.getHeightInches().toString());

        Date goal = new Date(userProfile.getGoalDate());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String goalDateAsString = df.format(goal);
        goalDateTextbox.setText(goalDateAsString);
    }

    /**
     * Sets user profile based on textbox input
     */
    private void updateUserProfile() {
        UserProfile userProfile = databaseManager.selectActiveUserProfile();

        String userName = nameTextbox.getText().toString();
        Double userGoalWeight = Double.parseDouble(goalWeightTextbox.getText().toString());
        String userGender = genderTextbox.getText().toString();
        Integer userFeet = Integer.parseInt(feetTextbox.getText().toString());
        Integer userInches = Integer.parseInt(inchesTextbox.getText().toString());
        String userGoalDateAsString = goalDateTextbox.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            userProfile.setName(userName);
            userProfile.setGoalWeight(userGoalWeight);
            userProfile.setGender(userGender);
            userProfile.setHeightFeet(userFeet);
            userProfile.setHeightInches(userInches);
            Date userGoalDate = sdf.parse(userGoalDateAsString);
            userProfile.setGoalDate(userGoalDate.getTime());

            databaseManager.updateUserProfile(userProfile);
        } catch (ParseException | NullPointerException e) {
            //TODO: update exception handling
            e.printStackTrace();
        }
    }

    /**
     * Validates textbox input
     * @return
     */
    private boolean validateSettings() {
        Validation validation = new Validation();
        boolean weightValid = validation.validateWeight(goalWeightTextbox, goalWeightError);
        boolean dateValid = validation.validateDate(goalDateTextbox, goalDateError);
        boolean feetValid = validation.validateInteger(feetTextbox, feetError, "Feet");
        boolean inchesValid = validation.validateInteger(inchesTextbox, inchesError, "Inches");
        boolean nameValid = validation.validateStringExists(nameTextbox, nameError, "Name");

        return weightValid && dateValid && feetValid && inchesValid && nameValid;
    }

    /**
     * Pre-poulates views when the activity starts
     */
    private void initializeViews() {
        nameTextbox = findViewById(R.id.name_textbox);
        nameError = findViewById(R.id.name_error);
        goalWeightTextbox = findViewById(R.id.goal_weight_textbox);
        goalWeightError = findViewById(R.id.goal_weight_error);
        goalDateTextbox = findViewById(R.id.goal_date_textbox);
        goalDateError = findViewById(R.id.goal_date_error);
        genderTextbox = findViewById(R.id.gender_textbox);
        feetTextbox = findViewById(R.id.height_feet_textbox);
        feetError = findViewById(R.id.height_feet_error);
        inchesTextbox = findViewById(R.id.height_inches_textbox);
        inchesError = findViewById(R.id.height_inches_error);
        backArrow = findViewById(R.id.back_arrow);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
    }
}