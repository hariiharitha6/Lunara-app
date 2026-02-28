package com.example.lunara;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class ReminderUtils {

    public static void scheduleWaterReminder(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", "Water Reminder");
        intent.putExtra("message", "Time to drink water! Stay hydrated.");
        intent.putExtra("id", 1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Schedule repeating alarm (e.g., every 2 hours)
        long interval = 2 * 60 * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + interval;

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
        }
    }

    public static void scheduleCheckupReminder(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", "Checkup Reminder");
        intent.putExtra("message", "Don't forget your scheduled prenatal checkup!");
        intent.putExtra("id", 2);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Schedule for tomorrow at 9 AM
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
