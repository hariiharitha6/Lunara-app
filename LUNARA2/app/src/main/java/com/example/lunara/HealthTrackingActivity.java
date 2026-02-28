package com.example.lunara;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.SharedPreferences;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.content.Context;
import android.text.TextUtils;

public class HealthTrackingActivity extends AppCompatActivity {

    EditText bpInput, sugarInput, weightInput, hemoInput;
    Button saveHealthBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tracking);

        bpInput = findViewById(R.id.bpInput);
        sugarInput = findViewById(R.id.sugarInput);
        weightInput = findViewById(R.id.weightInput);
        hemoInput = findViewById(R.id.hemoInput);
        saveHealthBtn = findViewById(R.id.saveHealthBtn);

        saveHealthBtn.setOnClickListener(v -> saveHealthData());
    }

    private void saveHealthData() {

        String bp = bpInput.getText().toString().trim();
        String sugar = sugarInput.getText().toString().trim();
        String weight = weightInput.getText().toString().trim();
        String hemo = hemoInput.getText().toString().trim();

        if (TextUtils.isEmpty(bp) || TextUtils.isEmpty(sugar)
                || TextUtils.isEmpty(weight) || TextUtils.isEmpty(hemo)) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("HealthData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("bp", bp);
        editor.putString("sugar", sugar);
        editor.putString("weight", weight);
        editor.putString("hemo", hemo);
        editor.apply();

        analyzeRisk(bp, sugar, hemo);

        Toast.makeText(this, "Health Data Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    private void analyzeRisk(String bp, String sugar, String hemo) {

        try {

            // BP format expected: 120/80
            int systolic = 0;

            if (bp.contains("/")) {
                String[] parts = bp.split("/");
                systolic = Integer.parseInt(parts[0]);
            } else {
                systolic = Integer.parseInt(bp);
            }

            int sugarValue = Integer.parseInt(sugar);
            double hemoValue = Double.parseDouble(hemo);

            if (systolic >= 140) {
                showNotification("⚠ High Blood Pressure",
                        "Your BP is high. Please consult doctor immediately.");
            }

            if (sugarValue >= 140) {
                showNotification("⚠ High Sugar Level",
                        "Monitor sugar levels carefully. Risk of gestational diabetes.");
            }

            if (hemoValue < 10) {
                showNotification("⚠ Low Hemoglobin",
                        "Iron deficiency detected. Increase iron-rich food.");
            }

            if (systolic < 140 && sugarValue < 140 && hemoValue >= 10) {
                showNotification("✔ Health Status Stable",
                        "Your health parameters look stable. Keep monitoring regularly.");
            }

        } catch (Exception e) {
            Toast.makeText(this, "Invalid Input Format (BP: 120/80)", Toast.LENGTH_LONG).show();
        }
    }

    private void showNotification(String title, String message) {

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "health_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelId,
                            "Health Alerts",
                            NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
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