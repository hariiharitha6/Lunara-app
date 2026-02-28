package com.example.lunara;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.content.SharedPreferences;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, area, mobile, weight, password;
    DatePicker datePicker;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = findViewById(R.id.name);
        area = findViewById(R.id.area);
        mobile = findViewById(R.id.mobile);
        weight = findViewById(R.id.weight);
        password = findViewById(R.id.password);
        datePicker = findViewById(R.id.datePicker);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {

        String nameStr = name.getText().toString().trim();
        String areaStr = area.getText().toString().trim();
        String mobileStr = mobile.getText().toString().trim();
        String weightStr = weight.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        // Validation
        if (nameStr.isEmpty()) {
            name.setError("Enter Name");
            return;
        }

        if (areaStr.isEmpty()) {
            area.setError("Enter Area");
            return;
        }

        if (mobileStr.length() != 10) {
            mobile.setError("Enter valid 10-digit number");
            return;
        }

        if (passwordStr.length() < 4) {
            password.setError("Password must be at least 4 characters");
            return;
        }

        if (weightStr.isEmpty()) {
            weight.setError("Enter Weight");
            return;
        }

        saveData(nameStr, areaStr, mobileStr, weightStr, passwordStr);
    }

    private void saveData(String nameStr, String areaStr,
                          String mobileStr, String weightStr,
                          String passwordStr) {

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int userCount = prefs.getInt("user_count", 0);

        if (userCount >= 5) {
            Toast.makeText(this,
                    "Maximum 5 users allowed",
                    Toast.LENGTH_LONG).show();
            return;
        }

        userCount++;

        editor.putInt("user_count", userCount);

        editor.putString("user_" + userCount + "_name", nameStr);
        editor.putString("user_" + userCount + "_area", areaStr);
        editor.putString("user_" + userCount + "_mobile", mobileStr);
        editor.putString("user_" + userCount + "_weight", weightStr);
        editor.putString("user_" + userCount + "_password", passwordStr);

        editor.putInt("user_" + userCount + "_year", datePicker.getYear());
        editor.putInt("user_" + userCount + "_month", datePicker.getMonth());
        editor.putInt("user_" + userCount + "_day", datePicker.getDayOfMonth());

        editor.apply();

        Toast.makeText(this,
                "Registration Successful",
                Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}