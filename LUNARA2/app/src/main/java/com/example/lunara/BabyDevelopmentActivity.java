package com.example.lunara;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.SharedPreferences;
import java.util.Calendar;

public class BabyDevelopmentActivity extends AppCompatActivity {

    TextView weekText, developmentText, quoteText;
    long weeks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_development);

        weekText = findViewById(R.id.weekText);
        developmentText = findViewById(R.id.developmentText);
        quoteText = findViewById(R.id.quoteText);

        calculateWeek();
        quoteText.setText(getDailyQuote(weeks));
    }

    private String getDailyQuote(long week) {

        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if (week <= 12) {

            String[] firstTrimesterQuotes = {
                    "Every heartbeat inside you is a miracle in progress.",
                    "You are creating life — that is pure strength.",
                    "Small beginnings lead to beautiful miracles.",
                    "Rest well, your body is doing something magical."
            };

            return firstTrimesterQuotes[dayOfMonth % firstTrimesterQuotes.length];
        }

        else if (week <= 27) {

            String[] secondTrimesterQuotes = {
                    "Your baby feels your love every single day.",
                    "You glow differently when you carry life.",
                    "Each day brings you closer to meeting your miracle.",
                    "Your strength inspires the life within you."
            };

            return secondTrimesterQuotes[dayOfMonth % secondTrimesterQuotes.length];
        }

        else {

            String[] thirdTrimesterQuotes = {
                    "You are almost there — stay strong, mama.",
                    "Your courage is building a future.",
                    "The final stretch leads to the greatest blessing.",
                    "Every breath you take prepares you for motherhood."
            };

            return thirdTrimesterQuotes[dayOfMonth % thirdTrimesterQuotes.length];
        }
    }

    private void calculateWeek() {

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        int year = prefs.getInt("year", 2024);
        int month = prefs.getInt("month", 0);
        int day = prefs.getInt("day", 1);

        Calendar lmp = Calendar.getInstance();
        lmp.set(year, month, day);

        Calendar today = Calendar.getInstance();

        weeks = ((today.getTimeInMillis() - lmp.getTimeInMillis())
                / (1000 * 60 * 60 * 24)) / 7;

        TextView weekTitle = findViewById(R.id.weekTitle);
        if (weekTitle != null) {
            weekTitle.setText("Week " + weeks);
        } else if (weekText != null) {
            weekText.setText("Week " + weeks);
        }

        if (weeks <= 12) {
            developmentText.setText("Your baby is forming major organs. Heartbeat has started and tiny hands and feet are developing.");
        }
        else if (weeks <= 27) {
            developmentText.setText("Your baby can hear sounds and respond to movement. Brain development is rapid during this stage.");
        }
        else {
            developmentText.setText("Your baby is gaining weight and preparing for birth. Lungs are maturing for life outside the womb.");
        }
    }
}