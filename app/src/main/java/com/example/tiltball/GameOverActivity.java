package com.example.tiltball;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Make sure to import TextView


public class GameOverActivity extends Activity {

    // Constant for SharedPreferences
    private static final String PREFS_NAME = "com.example.tiltball";
    private static final String KEY_HIGH_SCORE = "high_score";

    private void saveHighScore(int highScore) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HIGH_SCORE, highScore);
        editor.apply();
    }

    private int getHighScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_HIGH_SCORE, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Retrieve the score from the Intent
        int score = getIntent().getIntExtra("SCORE", 0);

        // Get the stored high score
        int storedHighScore = getHighScore();

        // If current score is greater than stored high score, update it
        if (score > storedHighScore) {
            saveHighScore(score);
            storedHighScore = score; // set the new high score to be displayed
        }

        // Display the score
        TextView scoreTextView = findViewById(R.id.tv_score);
        scoreTextView.setText(String.format("Your Score: %d", score));

        // Display the high score
        TextView highScoreTextView = findViewById(R.id.tv_high_score);
        highScoreTextView.setText(String.format("High Score: %d", storedHighScore));

        Button btnRestart = findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to GameActivity when the button is clicked.
                Intent gameIntent = new Intent(GameOverActivity.this, GameActivity.class);
                startActivity(gameIntent);

                // Close the current GameOverActivity
                finish();
            }
        });
    }

}
