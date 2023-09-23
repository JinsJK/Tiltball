//Authors: Jins & Sailesh

package com.example.tiltball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.util.ArrayList;
import java.util.List;

public class GameView extends View implements SensorEventListener {
    private Vibrator vibrator;
    private Paint ballPaint;
    private Bitmap backgroundImage;
    private Paint wallPaint;
    private Paint targetPaint;
    private Ball blueBall;
    private int score = 0;
    private float ballRadius = 50f; // example size
    private float targetRadius = 60f;
    private float sensitivityFactor = 4.0f;  // Adjust this value as needed
    private Paint scorePaint;
    private float ballX, ballY;
    private float targetX, targetY;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private List<Ball> redBalls = new ArrayList<>();
    private static final String PREFS_NAME = "TiltBallHighScore";
    private static final String HIGH_SCORE_KEY = "HighScore";
    private boolean gameStarted = false;


    // Method to get the HighScore
    private int getHighScore() {
        SharedPreferences preferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(HIGH_SCORE_KEY, 0);
    }

    //Method to set the HighScore
    private void setHighScore(int score) {
        SharedPreferences preferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(HIGH_SCORE_KEY, score);
        editor.apply();
    }


    //Constructor for Gameview with Context as parameter
    public GameView(Context context) {
        super(context);
        init(context);
        resetGame();
    }

    //Constructor for Gameview with Context and AttributeSet as parameters
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        resetGame();
    }

    //Constructor for GameView with Context , AttributeSet and defStyleAttr as parameters
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        resetGame();
    }

    // Method to reset the Game
    private void resetGame() {
        score = 0;
        // other initializations or resets as needed
        ballX = getWidth() / 2;
        ballY = getHeight() - ballRadius * 2;
        //initializeRedBalls();
        initializeBlueBall();
    }

    // method for initialisation
    private void init(Context context) {

        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background_image);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        ballPaint = new Paint();
        ballPaint.setColor(Color.GREEN);

        wallPaint = new Paint();
        wallPaint.setColor(Color.RED);

        targetPaint = new Paint();
        targetPaint.setColor(Color.RED);

        targetX = getWidth() - targetRadius * 3;
        targetY = getHeight() - targetRadius * 3;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(40);
        scorePaint.setTextAlign(Paint.Align.RIGHT);

        initializeBlueBall();
    }

    // To initialize the Blue Ball
    private void initializeBlueBall() {
        float buffer = 100;
        float desiredSpeed = 20;
        float distanceToGreen;

        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);

        do {
            float x = buffer + (float) (Math.random() * (getWidth() - 2 * buffer));
            float y = buffer + (float) (Math.random() * buffer);

            // Calculating the distance to the green ball
            float dx = x - ballX;
            float dy = y - ballY;
            distanceToGreen = (float) Math.sqrt(dx * dx + dy * dy);

            blueBall = new Ball(x, y, ballRadius, bluePaint, desiredSpeed);
        } while (distanceToGreen < buffer + ballRadius + blueBall.radius);

        // Normalizing blue ball velocity for consistent speed
        float magnitude = (float) Math.sqrt(blueBall.dx * blueBall.dx + blueBall.dy * blueBall.dy);
        blueBall.dx = (blueBall.dx / magnitude) * desiredSpeed;
        blueBall.dy = (blueBall.dy / magnitude) * desiredSpeed;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, getWidth(), getHeight(), true);
        super.onSizeChanged(w, h, oldw, oldh);

        // Initializing the green ball's position to down of the screen
        ballX = w / 2;
        ballY = h - ballRadius * 2;

        startRedBallSpawning();
        initializeBlueBall();
        gameStarted = true;
    }

    //To spawn the red ball
    private void startRedBallSpawning() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeRedBalls();
                invalidate();
            }
        }, 2000);
    }

    // Initialize the Red Balls
    private void initializeRedBalls() {
        redBalls.clear();

        float buffer = 100;
        float desiredSpeed = 10;
        for (int i = 0; i < 4; i++) {
            float x = buffer + (float) (Math.random() * (getWidth() - 2 * buffer));
            float y = buffer + (float) (Math.random() * buffer);
            Paint redPaint = new Paint();
            redPaint.setColor(Color.RED);

            Ball redBall = new Ball(x, y, targetRadius, redPaint, desiredSpeed);
            redBalls.add(redBall);
        }
    }

    //Method to draw the circle
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgroundImage, 0, 0, null);

        super.onDraw(canvas);

        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);

        canvas.drawCircle(targetX, targetY, targetRadius, targetPaint);

        for (Ball ball : redBalls) {
            canvas.drawCircle(ball.x, ball.y, ball.radius, ball.paint);
        }
        //blue ball
        if (blueBall != null) {
            canvas.drawCircle(blueBall.x, blueBall.y, blueBall.radius, blueBall.paint);
        }

        // Show score at the top right
        canvas.drawText("Score: " + score, getWidth() - 10, 50, scorePaint);
    }


    //Method to change the balls according to the motion of the phone
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float deltaX = -event.values[0]  * sensitivityFactor;
            float deltaY = event.values[1]  * sensitivityFactor;

            float nextX = ballX + deltaX;
            float nextY = ballY + deltaY;

            // Checking for collisions with the screen boundaries

            if (nextX - ballRadius < 0 || nextX + ballRadius > getWidth()) {
                ballX -= deltaX;
            } else {
                ballX = nextX;
            }

            // For Y-axis
            if (nextY - ballRadius < 0 || nextY + ballRadius > getHeight()) {
                ballY -= deltaY;
            } else {
                ballY = nextY;
            }

            postInvalidate();
        }

        updateBalls();
        if (gameStarted) {
            checkGameStatus();
        }
    }

    //Method to check the Game Status
    private void checkGameStatus() {
        float distanceToTarget = (float) Math.sqrt((ballX - targetX) * (ballX - targetX) + (ballY - targetY) * (ballY - targetY));
        if (distanceToTarget < ballRadius + targetRadius) {
            endGame();
            return;
        }

        for (Ball redBall : redBalls) {
            float distance = (float) Math.sqrt((ballX - redBall.x) * (ballX - redBall.x) + (ballY - redBall.y) * (ballY - redBall.y));
            if (distance < ballRadius + redBall.radius) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(500);
                }
                endGame();
                return;
            }
        }

        // Check for collision with blue ball and update score
        if (blueBall != null) {
            float distanceToBlueBall = (float) Math.sqrt((ballX - blueBall.x) * (ballX - blueBall.x) + (ballY - blueBall.y) * (ballY - blueBall.y));
            if (distanceToBlueBall < ballRadius + blueBall.radius) {
                // The ball has collided with the blue ball, so increase the score.
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(200);
                }
                score += 50;
                // To Remove the current blue ball and spawn a new one.
                blueBall = null;
                initializeBlueBall();
            }
        }
    }

    // Method to end the Game
    private void endGame() {
        int highScore = getHighScore();
        if(score > highScore) {
            setHighScore(score);
            highScore = score;
        }
        Intent intent = new Intent(getContext(), GameOverActivity.class);
        intent.putExtra("SCORE", score);  // Passing the score to GameOverActivity
        getContext().startActivity(intent);
        score = 0;  // Reset the score
        sensorManager.unregisterListener(this);
    }


    //Method to avoid the collision
    private void resolveBallCollision(Ball a, Ball b) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0.0) return;

        float nx = dx / distance;
        float ny = dy / distance;

        float vx = b.dx - a.dx;
        float vy = b.dy - a.dy;

        float dot = vx * nx + vy * ny;

        if (dot > 0) return;

        float restitution = 1.0f;

        float impulse = (-(1 + restitution) * dot) / (1/a.radius + 1/b.radius);

        a.dx -= (impulse / a.radius) * nx;
        a.dy -= (impulse / a.radius) * ny;
        b.dx += (impulse / b.radius) * nx;
        b.dy += (impulse / b.radius) * ny;

        float overlap = 0.5f * (distance - a.radius - b.radius);
        a.x -= overlap * nx;
        a.y -= overlap * ny;
        b.x += overlap * nx;
        b.y += overlap * ny;
    }

    //Method to update the Balls to avoid the collision
    private void updateBalls() {
        for (Ball ball : redBalls) {
            ball.x += ball.dx;
            ball.y += ball.dy;
            handleWallCollision(ball);
        }

        if (blueBall != null) {
            blueBall.x += blueBall.dx;
            blueBall.y += blueBall.dy;

            handleWallCollision(blueBall);

            for (Ball redBall : redBalls) {
                if (ballsAreColliding(blueBall, redBall)) {
                    resolveBallCollision(blueBall, redBall);
                }
            }
        }

        // For collisions between red balls
        for (int i = 0; i < redBalls.size(); i++) {
            for (int j = i + 1; j < redBalls.size(); j++) {
                Ball ballA = redBalls.get(i);
                Ball ballB = redBalls.get(j);

                if (ballsAreColliding(ballA, ballB)) {
                    resolveBallCollision(ballA, ballB);
                }
            }
        }
    }

    //To avoid  the ball collision with the wall
    private void handleWallCollision(Ball ball) {
        if (ball.x - ball.radius < 0) {
            ball.x = ball.radius;
            ball.dx = -ball.dx;
        }
        if (ball.x + ball.radius > getWidth()) {
            ball.x = getWidth() - ball.radius;
            ball.dx = -ball.dx;
        }
        if (ball.y - ball.radius < 0) {
            ball.y = ball.radius;
            ball.dy = -ball.dy;
        }
        if (ball.y + ball.radius > getHeight()) {
            ball.y = getHeight() - ball.radius;
            ball.dy = -ball.dy;
        }
    }

    //Method to check whether the balls are colliding
    private boolean ballsAreColliding(Ball a, Ball b) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < a.radius + b.radius;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
     }
}