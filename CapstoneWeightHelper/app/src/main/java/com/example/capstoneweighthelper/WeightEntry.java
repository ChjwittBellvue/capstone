/*
* References
*
* Van Luke, M. (2020, August 7). Introduction to Android’s CameraX With Java. Retrieved April 11, 2025, from https://medium.com/swlh/introduction-to-androids-camerax-with-java-ca384c522c5
 */
package com.example.capstoneweighthelper;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.capstoneweighthelper.pojo.WeightHistoryRecord;
import com.example.capstoneweighthelper.util.ConfirmationDialogue;
import com.example.capstoneweighthelper.util.Validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeightEntry extends AppCompatActivity {
    private DatabaseManager databaseManager;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private WeightHistoryRecord historyRecord = new WeightHistoryRecord();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weight_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Pull shared values from other activities
        copyIntent();

        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        userId = sharedPreferences.getInt("ActiveUserId", -1);
        databaseManager = new DatabaseManager(this,userId);

        ButtonHandler bh = new ButtonHandler();

        ImageView backArrow = findViewById(R.id.back_arrow);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button saveButton = findViewById(R.id.save_button);
        Button pictureButton = findViewById(R.id.progress_picture_nav);
        Button deletePictureButton = findViewById(R.id.delete_picture_button);

        backArrow.setOnClickListener(bh);
        cancelButton.setOnClickListener(bh);
        saveButton.setOnClickListener(bh);
        pictureButton.setOnClickListener(bh);
        deletePictureButton.setOnClickListener(bh);

        setInProgressWeightHistory();

        // Set Delete Button instead of cancel
        if (historyRecord.getId()!=null) {
            cancelButton.setText(R.string.delete_entry);
        }

    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.back_arrow) {  // Back Arrow
                databaseManager.resetEditState();

                if (historyRecord.getId()==null) {
                    moveToSummary();
                } else {
                    moveToHistory();
                }
            } else if (v.getId() == R.id.cancel_button){ //Cancel Button

                if (historyRecord.getId()==null) {
                    databaseManager.resetEditState();
                    moveToSummary();
                } else {
                    ConfirmationDialogue confirmationDialogue = new ConfirmationDialogue(WeightEntry.this, historyRecord, userId);
                    confirmationDialogue.show(getSupportFragmentManager(), "");
                }
            } else if (v.getId() == R.id.save_button) { // Save
                boolean saveReady = validateWeightEntry();
                if (saveReady) {
                    if (historyRecord.getId() == null) {
                        saveWeightHistory(true);
                        moveToSummary();
                    } else {
                        saveWeightHistory(true, historyRecord);
                        moveToHistory();
                    }
                }
            } else if (v.getId() == R.id.progress_picture_nav) {  // Camera operations
                if (hasCameraPermission()) {
                    enableCamera();
                } else {
                    requestPermission();
                }
            } else if (v.getId() == R.id.delete_picture_button) {  // Delete Picture
                historyRecord.setPictureLocation(null);
                reloadEntry();
            }
        }
    }

    /**
     * Adds weight history to table
     * @param permanentRecord
     */
    private void saveWeightHistory(boolean permanentRecord) {
        updateWeightHistoryWithTextboxes(true);
        Integer recordId = databaseManager.insertIntoWeightHistory(historyRecord);
        historyRecord.setId(recordId);

        databaseManager.updateCurrentWeight();
    }

    /**
     * Adds weight history to table
     * @param permanentRecord
     * @param tempWeightHistoryRecord
     * @return weight history record
     */
    private WeightHistoryRecord saveWeightHistory(boolean permanentRecord, WeightHistoryRecord tempWeightHistoryRecord) {
        updateWeightHistoryWithTextboxes(permanentRecord);

        databaseManager.updateWeightEntry(tempWeightHistoryRecord);

        databaseManager.updateCurrentWeight();

        return tempWeightHistoryRecord;
    }

    /**
     * Uses textbox input to get weight entry information
     *
     * @param permanentRecord
     */
    private void updateWeightHistoryWithTextboxes(boolean permanentRecord) {
        TextView currentWeightTextbox = findViewById(R.id.entry_weight_textbox);
        TextView currentDateTextbox = findViewById(R.id.entry_date_textbox);

        if (currentWeightTextbox.getText()!=null && !currentWeightTextbox.getText().toString().isEmpty()) {
            Double currentWeight = Double.parseDouble(currentWeightTextbox.getText().toString());
            historyRecord.setEntryWeight(currentWeight);
        }

        if (currentDateTextbox.getText() != null && !currentDateTextbox.getText().toString().isEmpty()) {
            String userGoalDateAsString = currentDateTextbox.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                Date userCurrentDate = sdf.parse(userGoalDateAsString);
                historyRecord.setEntryDate(userCurrentDate.getTime());


            } catch (ParseException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (permanentRecord) {
            historyRecord.setPermanentRecord("Y");
        } else {
            historyRecord.setPermanentRecord("N");
        }
    }


    /**
     * Initializes texboxes with entry weight defaults and picture if it exists
     */
    private void setInProgressWeightHistory() {
        TextView currentWeight = findViewById(R.id.entry_weight_textbox);
        TextView CurrentDate = findViewById(R.id.entry_date_textbox);

        if (historyRecord.getPictureLocation() != null) {
            showPictureIcons(historyRecord);
        } else {
            hidePictureIcons();
        }

        if (historyRecord.getEntryWeight() != 0.0 ) {
            currentWeight.setText(historyRecord.getEntryWeight().toString());
        } else {
            UserProfile userProfile = databaseManager.selectActiveUserProfile();
            currentWeight.setText(userProfile.getCurrentWeight().toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        if (historyRecord.getEntryDate() != null) {
            Date date = new Date(historyRecord.getEntryDate());

            String goalDateAsString = sdf.format(date);
            CurrentDate.setText(goalDateAsString);
        } else {
            Date date = new Date();
            String goalDateAsString = sdf.format(date);
            CurrentDate.setText(goalDateAsString);
        }
    }

    /**
     * Shows picture data if it exists
     * @param weightHistoryRecord
     */
    private void showPictureIcons(WeightHistoryRecord weightHistoryRecord) {
        ImageView picturePreview = findViewById(R.id.picture_preview);
        Button deletePictureButton = findViewById(R.id.delete_picture_button);

        picturePreview.setVisibility(View.VISIBLE);
        deletePictureButton.setVisibility(View.VISIBLE);
        deletePictureButton.setEnabled(true);
        picturePreview.setImageURI(Uri.parse(historyRecord.getPictureLocation()));
    }

    /**
     * Hides the picture and delete picture button
     */
    private void hidePictureIcons() {
        ImageView picturePreview = findViewById(R.id.picture_preview);
        Button deletePictureButton = findViewById(R.id.delete_picture_button);

        picturePreview.setVisibility(View.INVISIBLE);
        deletePictureButton.setVisibility(View.INVISIBLE);
        deletePictureButton.setEnabled(false);
    }

    /**
     * Gets shared activity data
     */
    private void copyIntent() {
        if (getIntent().hasExtra("recordId")) {
            historyRecord.setId(getIntent().getExtras().getInt("recordId"));
        }
        if (getIntent().hasExtra("recordWeight")) {
            historyRecord.setEntryWeight(getIntent().getExtras().getDouble("recordWeight"));
        }
        if (getIntent().hasExtra("recordDate")) {
            historyRecord.setEntryDate(getIntent().getExtras().getLong("recordDate"));
        }

        if (getIntent().hasExtra("pictureUri")) {
            historyRecord.setPictureLocation(getIntent().getExtras().getString("pictureUri"));
        }

        System.out.println("historyRecord: " + historyRecord);
    }

    /**
     * Sets activity data for next activity
     * @param clazz
     * @return
     */
    private Intent setIntentForNextActivity(Class clazz) {
        updateWeightHistoryWithTextboxes(false);
        Intent intent = new Intent(this, clazz);
        if (historyRecord.getId() != null) {
            intent.putExtra("recordId", historyRecord.getId());
        }
        if (historyRecord.getEntryWeight()!= null) {
            intent.putExtra("recordWeight", historyRecord.getEntryWeight());
        }
        if (historyRecord.getEntryDate()!= null) {
            intent.putExtra("recordDate", historyRecord.getEntryDate());
        }
        if (historyRecord.getPictureLocation()!= null) {
            intent.putExtra("pictureUri", historyRecord.getPictureLocation());
        }

        return intent;
    }

    /**
     * Validates textbox input
     * @return
     */
    private boolean validateWeightEntry() {
        Validation validation = new Validation();
        TextView weightTextView = findViewById(R.id.entry_weight_textbox);
        TextView weightErrorTextView = findViewById(R.id.entry_weight_error);

        TextView dateTextView = findViewById(R.id.entry_date_textbox);
        TextView dateErrorTextView = findViewById(R.id.entry_date_error);

        boolean weightValid = validation.validateWeight(weightTextView, weightErrorTextView);
        boolean dateValid = validation.validateDate(dateTextView, dateErrorTextView);
        return weightValid && dateValid;
    }

    /**
     * Checks if permission has been granted for the camera
     * @return
     */
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests camera permission
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    /**
     * Starts the camera
     */
    private void enableCamera() {
        Intent intent = setIntentForNextActivity(CameraActivity.class);

        startActivity(intent);
    }

    // Activity Movement
    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void moveToHistory() {
        Intent intent = new Intent(this, WeightHistory.class);
        startActivity(intent);
    }

    private void reloadEntry() {
        Intent intent = setIntentForNextActivity(WeightEntry.class);
        startActivity(intent);
    }

}