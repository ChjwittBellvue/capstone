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
import com.example.capstoneweighthelper.pojo.LoginObject;
import com.example.capstoneweighthelper.pojo.UserProfile;
import com.example.capstoneweighthelper.util.Validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Setup extends AppCompatActivity {

    TextView displayNameTextbox;
    TextView displayNameError;
    TextView currentWeightTextbox;
    TextView currentWeightError;
    TextView goalWeightTextbox;
    TextView goalWeightError;
    TextView goalDateTextbox;
    TextView goalDateError;
    TextView heightFeetTextbox;
    TextView heightFeetError;
    TextView heightInchesTextbox;
    TextView heightInchesError;
    TextView genderTextbox;
    TextView usernameTextbox;
    TextView passwordTextbox;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ButtonHandler bh = new ButtonHandler();

        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("ActiveUserId", -1);
        databaseManager = new DatabaseManager(this,userId);
        setTextViews();

        Button createUserButton = findViewById(R.id.setup_create_user_button);
        ImageView backArrow = findViewById(R.id.back_arrow);
        createUserButton.setOnClickListener(bh);
        backArrow.setOnClickListener(bh);
    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.setup_create_user_button) {
                if (validateSetup()) {
                    Integer newId = initializeUser();
                    setUserForApplication(newId);
                    moveToSummary();
                }
            } else if (v.getId() == R.id.back_arrow) {
                moveToLogin();
            }
        }
    }

    /**
     * Validates textboxes
     * @return
     */
    private boolean validateSetup() {
        Validation validation = new Validation();
        boolean credentialsValid = validateCredentialSetup();
        boolean displayNameValid = validation.validateStringExists(displayNameTextbox, displayNameError, "Display Name");
        boolean currentWeightValid = validation.validateWeight(currentWeightTextbox, currentWeightError);
        boolean goalWeightValid = validation.validateWeight(goalWeightTextbox, goalWeightError);
        boolean goalDateValid = validation.validateDate(goalDateTextbox, goalDateError);
        boolean feetValid = validation.validateInteger(heightFeetTextbox, heightFeetError, "Feet");
        boolean inchesValid = validation.validateInteger(heightInchesTextbox, heightInchesError, "Inches");

        return credentialsValid && displayNameValid && currentWeightValid && goalWeightValid
                && goalDateValid && feetValid && inchesValid;
    }

    /**
     * Validates username and password
     * @return
     */
    private boolean validateCredentialSetup(){

        if (usernameTextbox.getText()!=null && !usernameTextbox.getText().toString().isEmpty()
        && passwordTextbox.getText()!=null && !passwordTextbox.getText().toString().isEmpty()) {
            TextView credentialsError = findViewById(R.id.setup_credentials_error);

            // return false if user already exists
            if(!checkUsernameUnused(usernameTextbox.getText().toString())) {
                credentialsError.setText("Username taken, choose another");
                return false;
            }

            credentialsError = findViewById(R.id.setup_credentials_error);
            credentialsError.setText("");
            return true;
        } else {
            TextView credentialsError = findViewById(R.id.setup_credentials_error);
            credentialsError.setText("Enter a Username and Password");
        }
        return false;
    }

    /**
     * Moves to summary page
     */
    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Creates a new user
     *
     * @return
     */
    private Integer initializeUser() {
        UserProfile userProfile = getUserProfileFromTextboxInput();
        LoginObject loginObject = getLoginObjectFromTextboxInput();


        return databaseManager.createNewUser(userProfile, loginObject);
    }

    /**
     * Sets new user information
     *
     * @return
     */
    private UserProfile getUserProfileFromTextboxInput() {
        try {
            UserProfile userProfile = new UserProfile();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            userProfile.setName(displayNameTextbox.getText().toString());
            userProfile.setCurrentWeight(Double.parseDouble(currentWeightTextbox.getText().toString()));
            userProfile.setGoalWeight(Double.parseDouble(goalWeightTextbox.getText().toString()));
            userProfile.setGender(genderTextbox.getText().toString());
            userProfile.setHeightFeet(Integer.parseInt(heightFeetTextbox.getText().toString()));
            userProfile.setHeightInches(Integer.parseInt(heightInchesTextbox.getText().toString()));

            Date goalDate = sdf.parse(goalDateTextbox.getText().toString());
            userProfile.setGoalDate(goalDate.getTime());

            return userProfile;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets credential information from setup
     *
     * @return
     */
    private LoginObject getLoginObjectFromTextboxInput() {
        LoginObject login = new LoginObject();
        login.setUserName(usernameTextbox.getText().toString());
        login.setPassword(passwordTextbox.getText().toString());

        return login;
    }

    /**
     * Sets the active user for the application
     * @param newId
     */
    private void setUserForApplication(Integer newId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ActiveUserId", newId);
        editor.apply();
    }

    /**
     * Checks if username already exists
     *
     * @param username
     * @return
     */
    private boolean checkUsernameUnused(String username) {
        List<LoginObject> users = databaseManager.getAllUsers();
        if (users!=null && !users.isEmpty()) {
            for (LoginObject user : users) {
                if (user.getUserName().equals(username)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Moves to login page
     */
    private void moveToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Pre-populate textboxes
     */
    private void setTextViews() {
        displayNameTextbox = findViewById(R.id.setup_display_name_textbox);
        displayNameError = findViewById(R.id.setup_display_name_error);
        currentWeightTextbox = findViewById(R.id.setup_current_weight_textbox);
        currentWeightError = findViewById(R.id.setup_current_weight_error);
        goalWeightTextbox = findViewById(R.id.setup_goal_weight_textbox);
        goalWeightError = findViewById(R.id.setup_goal_weight_error);
        goalDateTextbox = findViewById(R.id.setup_goal_date_textbox);
        goalDateError = findViewById(R.id.setup_goal_date_error);
        heightFeetTextbox = findViewById(R.id.setup_height_feet_textbox);
        heightFeetError = findViewById(R.id.setup_height_feet_error);
        heightInchesTextbox = findViewById(R.id.setup_height_inches_textbox);
        heightInchesError = findViewById(R.id.setup_height_inches_error);
        genderTextbox = findViewById(R.id.setup_gender_textbox);
        usernameTextbox = findViewById(R.id.setup_username_textbox);
        passwordTextbox = findViewById(R.id.setup_password_textbox);
    }
}