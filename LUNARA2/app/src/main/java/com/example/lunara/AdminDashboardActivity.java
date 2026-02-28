package com.example.lunara;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Vibrator;
import android.media.RingtoneManager;
import android.net.Uri;
import android.media.Ringtone;
import com.google.firebase.database.*;
import java.util.Calendar;

public class AdminDashboardActivity extends AppCompatActivity {

    TextView areaSummary, trimesterSummary, riskSummary, alertStatusText;
    Button mapBtn, clearAlertBtn;

    DatabaseReference alertRef;
    ValueEventListener alertListener;
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Views
        areaSummary = findViewById(R.id.areaSummary);
        trimesterSummary = findViewById(R.id.trimesterSummary);
        riskSummary = findViewById(R.id.riskSummary);
        alertStatusText = findViewById(R.id.alertStatusText);
        mapBtn = findViewById(R.id.mapBtn);
        clearAlertBtn = findViewById(R.id.clearAlertBtn);

        // Load Summary
        loadSummary();

        // Map Navigation
        mapBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegionDashboardActivity.class)));

        // Firebase Reference
        alertRef = FirebaseDatabase.getInstance().getReference("alerts");

        alertListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    alertStatusText.setText("No Emergency Alerts");
                    alertStatusText.setTextColor(
                            getResources().getColor(android.R.color.black));
                    return;
                }

                DataSnapshot latest = null;

                for (DataSnapshot data : snapshot.getChildren()) {
                    latest = data;
                }

                if (latest != null) {

                    AlertModel alert = latest.getValue(AlertModel.class);

                    if (alert != null) {

                        alertStatusText.setText(
                                "🚨 EMERGENCY ALERT\n\n" +
                                        "Name: " + alert.name + "\n" +
                                        "Mobile: " + alert.mobile + "\n" +
                                        "Area: " + alert.area
                        );

                        alertStatusText.setTextColor(
                                getResources().getColor(android.R.color.holo_red_dark));

                        playAlertSound(); // plays once
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        };

        alertRef.addValueEventListener(alertListener);

        // Clear Alert Button
        clearAlertBtn.setOnClickListener(v -> clearEmergencyAlert());
    }

    // ===============================
    // LOAD PREGNANCY SUMMARY
    // ===============================
    private void loadSummary() {

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        String area = prefs.getString("area", "No Area");
        int year = prefs.getInt("year", 2024);
        int month = prefs.getInt("month", 0);
        int day = prefs.getInt("day", 1);

        Calendar lmp = Calendar.getInstance();
        lmp.set(year, month, day);

        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - lmp.getTimeInMillis();
        long weeks = (diff / (1000 * 60 * 60 * 24)) / 7;

        String trimester;

        if (weeks <= 12)
            trimester = "First Trimester";
        else if (weeks <= 27)
            trimester = "Second Trimester";
        else
            trimester = "Third Trimester";

        areaSummary.setText("Area: " + area);
        trimesterSummary.setText("Current Trimester: " + trimester);

        if (weeks > 36)
            riskSummary.setText("⚠ High Monitoring Required");
        else
            riskSummary.setText("Stable Pregnancy Status");
    }

    // ===============================
    // CLEAR ALERT FROM FIREBASE
    // ===============================
    private void clearEmergencyAlert() {

        alertRef.removeValue().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                // 🔴 STOP SOUND IMMEDIATELY
                if (ringtone != null && ringtone.isPlaying()) {
                    ringtone.stop();
                    ringtone = null; // reset reference
                }

                alertStatusText.setText("No Emergency Alerts");
                alertStatusText.setTextColor(
                        getResources().getColor(android.R.color.black));

                Toast.makeText(this,
                        "Alert Cleared Successfully",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this,
                        "Failed to Clear Alert",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===============================
    // PLAY ALERT SOUND
    // ===============================
    private void playAlertSound() {

        if (ringtone != null && ringtone.isPlaying()) {
            return; // prevent multiple sounds
        }

        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        ringtone = RingtoneManager.getRingtone(this, alarmSound);

        if (ringtone != null) {
            ringtone.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertRef != null && alertListener != null) {
            alertRef.removeEventListener(alertListener);
        }
    }

}
