package com.example.lunara;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {

    EditText mobileInput, passwordInput;
    Button loginBtn;
    TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobileInput = findViewById(R.id.mobileInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        signupText = findViewById(R.id.signupText);

        loginBtn.setOnClickListener(v -> {

            String mobile = mobileInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            validateLogin(mobile, password);
        });

        signupText.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));
    }

    private void validateLogin(String mobileInput, String passwordInput) {

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        int userCount = prefs.getInt("user_count", 0);

        if (userCount == 0) {
            Toast.makeText(this,
                    "No users registered yet",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 1; i <= userCount; i++) {

            String savedMobile = prefs.getString("user_" + i + "_mobile", "");
            String savedPassword = prefs.getString("user_" + i + "_password", "");

            if (mobileInput.equals(savedMobile) &&
                    passwordInput.equals(savedPassword)) {

                // Save current logged-in user index
                prefs.edit().putInt("current_user", i).apply();

                startActivity(new Intent(this, WomanDashboardActivity.class));
                finish();
                return;
            }
        }

        Toast.makeText(this,
                "Invalid Mobile or Password",
                Toast.LENGTH_SHORT).show();
    }
}