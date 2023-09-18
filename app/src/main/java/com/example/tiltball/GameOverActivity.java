package com.example.tiltball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Make sure to import TextView

public class GameOverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Retrieve the score from the Intent
        int score = getIntent().getIntExtra("SCORE", 0);

        // Set the score to the TextView
        TextView scoreTextView = findViewById(R.id.tv_score);
        scoreTextView.setText(String.format("Your Score: %d", score));

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
