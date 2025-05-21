package com.example.capstoneweighthelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.processing.SurfaceProcessorNode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.LoginObject;

import java.util.List;

public class Login extends AppCompatActivity {

    TextView usernameTextBox;
    TextView passwordTextBox;
    Button loginButton;
    Button createNewUserButton;
    TextView loginErrorMessage;
    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTextViews();
        databaseManager = new DatabaseManager(this, -1);
        ButtonHandler bh = new ButtonHandler();

        createNewUserButton.setOnClickListener(bh);
        loginButton.setOnClickListener(bh);

    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.login_button) { // If user chooses to login
                if (validateLoginTextboxes()) {
                    String username = usernameTextBox.getText().toString();
                    String password = passwordTextBox.getText().toString();
                    Integer id = checkUserLogin(username, password);
                    if (id>-1) {
                        setUserForApplication(id);
                        moveToSummary();
                    }
                } else {
                    loginErrorMessage.setText("Username and password cannot be empty");
                }
            } else if (v.getId() == R.id.login_create_user_button) { // If user chooses to create a user
                moveToSetup();
            }
        }
    }

    /**
     * Handles movement to setup page
     */
    private void moveToSetup() {
        Intent intent = new Intent(this, Setup.class);
        startActivity(intent);
    }

    /**
     * Handles movement to summary page
     */
    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Sets logged in user in application
     */
    private void setUserForApplication(Integer newId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ActiveUserId", newId);
        editor.apply();
    }

    /**
     * Validates user input
     * @return
     */
    private boolean validateLoginTextboxes() {
        if (usernameTextBox.getText()!=null && !usernameTextBox.getText().toString().isEmpty()
                && passwordTextBox.getText()!=null && !passwordTextBox.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks for valid user, avoids using SQL where clauses to protect against SQL injection
     *
     * @param username
     * @param password
     * @return
     */
    private Integer checkUserLogin(String username, String password) {
        List<LoginObject> users = databaseManager.getAllUsers();
        if (users!=null && !users.isEmpty()) {
            for (LoginObject user : users) {
                if (user.getUserName().equals(username)
                        && user.getPassword().equals(password)) {
                    return user.getId();
                }
            }
        }
        loginErrorMessage.setText("Invalid User Credentials");
        return -1;
    }

    /**
     * Sets views initially
     */
    private void setTextViews() {
        usernameTextBox = findViewById(R.id.login_username_textbox);
        passwordTextBox = findViewById(R.id.login_password_textbox);;
        loginButton = findViewById(R.id.login_button);
        createNewUserButton = findViewById(R.id.login_create_user_button);
        loginErrorMessage = findViewById(R.id.login_error);
    }
}