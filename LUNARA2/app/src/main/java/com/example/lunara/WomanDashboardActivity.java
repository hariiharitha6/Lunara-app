package com.example.lunara;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

public class WomanDashboardActivity extends AppCompatActivity {

    TextView nameText, areaText, mobileText, weightText;
    TextView weekText, trimesterText, dueDateText, tipsText;

    DrawerLayout drawerLayout;

    long weeks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woman_dashboard);

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);

        // TextViews
        nameText = findViewById(R.id.nameText);
        areaText = findViewById(R.id.areaText);
        mobileText = findViewById(R.id.mobileText);
        weightText = findViewById(R.id.weightText);
        weekText = findViewById(R.id.weekText);
        trimesterText = findViewById(R.id.trimesterText);
        dueDateText = findViewById(R.id.dueDateText);
        tipsText = findViewById(R.id.tipsText);

        // Buttons
        Button healthBtn = findViewById(R.id.healthBtn);
        Button babyBtn = findViewById(R.id.babyBtn);
        Button emergencyBtn = findViewById(R.id.emergencyBtn);
        Button riskBtn = findViewById(R.id.riskBtn);
        Button alertBtn = findViewById(R.id.alertBtn);

        // Navigation
        healthBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HealthTrackingActivity.class)));

        babyBtn.setOnClickListener(v ->
                startActivity(new Intent(this, BabyDevelopmentActivity.class)));

        emergencyBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EmergencyActivity.class)));

        riskBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RiskAlertActivity.class)));

        // Emergency Alert stays on same page
        alertBtn.setOnClickListener(v -> sendEmergencyAlert());

        // Load data
        loadUserData();

        // Schedule reminders
        ReminderUtils.scheduleWaterReminder(this);
        ReminderUtils.scheduleCheckupReminder(this);
    }

    private void sendEmergencyAlert() {

        SharedPreferences userPrefs =
                getSharedPreferences("UserData", MODE_PRIVATE);

        int currentUser = userPrefs.getInt("current_user", 1);

        String name = userPrefs.getString("user_" + currentUser + "_name", "Unknown");
        String mobile = userPrefs.getString("user_" + currentUser + "_mobile", "N/A");
        String area = userPrefs.getString("user_" + currentUser + "_area", "N/A");

        DatabaseReference ref =
                FirebaseDatabase.getInstance().getReference("alerts");

        String alertId = ref.push().getKey();

        AlertModel alert = new AlertModel(
                name,
                mobile,
                area,
                System.currentTimeMillis()
        );

        ref.child(alertId).setValue(alert);

        Toast.makeText(this,
                "Emergency Alert Sent Successfully!",
                Toast.LENGTH_LONG).show();
    }
    private void loadUserData() {

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        // Get currently logged-in user
        int currentUser = prefs.getInt("current_user", 1);

        // Load correct user data
        String name = prefs.getString("user_" + currentUser + "_name", "User");
        String area = prefs.getString("user_" + currentUser + "_area", "Area");
        String mobile = prefs.getString("user_" + currentUser + "_mobile", "N/A");
        String weight = prefs.getString("user_" + currentUser + "_weight", "N/A");

        int year = prefs.getInt("user_" + currentUser + "_year", 2024);
        int month = prefs.getInt("user_" + currentUser + "_month", 0);
        int day = prefs.getInt("user_" + currentUser + "_day", 1);

        // Set profile details
        nameText.setText("Name: " + name);
        areaText.setText("Area: " + area);
        mobileText.setText("Mobile: " + mobile);
        weightText.setText("Weight: " + weight + " kg");

        // Calculate pregnancy weeks
        Calendar lmp = Calendar.getInstance();
        lmp.set(year, month, day);

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - lmp.getTimeInMillis();
        weeks = (diff / (1000 * 60 * 60 * 24)) / 7;

        weekText.setText("Pregnancy Week: " + weeks);

        if (weeks <= 12)
            trimesterText.setText("Trimester: First");
        else if (weeks <= 27)
            trimesterText.setText("Trimester: Second");
        else
            trimesterText.setText("Trimester: Third");

        Calendar dueDate = (Calendar) lmp.clone();
        dueDate.add(Calendar.DAY_OF_YEAR, 280);
        dueDateText.setText("Estimated Due Date: " + dueDate.getTime().toString());

        tipsText.setText(getHealthTips(weeks));

        // Stage notifications
        if (weeks == 12) {
            showNotification("Second Trimester Started",
                    "You have entered the second trimester!");
        }

        if (weeks == 28) {
            showNotification("Third Trimester Started",
                    "Prepare for delivery and regular checkups.");
        }
    }
    private String getHealthTips(long week) {

        if (week <= 12)
            return "Tip: Take folic acid daily and avoid heavy lifting.";
        else if (week <= 27)
            return "Tip: Monitor baby movements and attend anomaly scan.";
        else
            return "Tip: Prepare hospital bag and attend weekly checkups.";
    }

    private void showNotification(String title, String message) {

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "stage_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelId,
                            "Alerts",
                            NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
