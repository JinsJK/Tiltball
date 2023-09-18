//GameView.java

package com.example.tiltball;

import android.content.Context;
import android.content.Intent;
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
    private float sensitivityFactor = 10.0f;  // Adjust this value as needed
    private Paint scorePaint;
    private float ballX, ballY;
    private float targetX, targetY;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private List<Ball> redBalls = new ArrayList<>();


    public GameView(Context context) {
        super(context);
        init(context);
        resetGame();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        resetGame();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        resetGame();
    }


    private void resetGame() {
        score = 0;
        // other initializations or resets as needed
        ballX = getWidth() / 2;
        ballY = getHeight() - ballRadius * 2;
        //initializeRedBalls();
        initializeBlueBall();
    }


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

        // Initialize score paint
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);  // set the color as per your requirement
        scorePaint.setTextSize(40);        // set the size as per your requirement
        scorePaint.setTextAlign(Paint.Align.RIGHT);

        // Initialize the blue ball
        initializeBlueBall();
    }

    private void initializeBlueBall() {
        float buffer = 150; // Increased buffer value
        float desiredSpeed = 20; // Adjust this for your desired speed of blue ball
        float distanceTogreen;

        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);

        do {
            float x = buffer + (float) (Math.random() * (getWidth() - 2 * buffer));
            float y = buffer + (float) (Math.random() * buffer);

            // Calculate the distance to the green ball
            float dx = x - ballX;
            float dy = y - ballY;
            distanceTogreen = (float) Math.sqrt(dx * dx + dy * dy);

            blueBall = new Ball(x, y, ballRadius, bluePaint, desiredSpeed);
        } while (distanceTogreen < buffer + ballRadius + blueBall.radius);

        // Normalize blue ball velocity for consistent speed
        float magnitude = (float) Math.sqrt(blueBall.dx * blueBall.dx + blueBall.dy * blueBall.dy);
        blueBall.dx = (blueBall.dx / magnitude) * desiredSpeed;
        blueBall.dy = (blueBall.dy / magnitude) * desiredSpeed;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, getWidth(), getHeight(), true);
        super.onSizeChanged(w, h, oldw, oldh);

        // Initialize the green ball's position here
        ballX = w / 2;
        ballY = h - ballRadius * 2;

        startRedBallSpawning();
        initializeBlueBall();
    }

    private void startRedBallSpawning() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeRedBalls();
                invalidate(); // Redraw the view to show the red balls
            }
        }, 3000); // Delay of 3 seconds
    }


    private void initializeRedBalls() {
        redBalls.clear();  // Clear existing balls

        float buffer = 100; // Buffer value
        float desiredSpeed = 10;   // Adjust this for your desiblue speed of red balls
        for (int i = 0; i < 4; i++) {
            float x = buffer + (float) (Math.random() * (getWidth() - 2 * buffer));
            float y = buffer + (float) (Math.random() * buffer);
            Paint redPaint = new Paint();
            redPaint.setColor(Color.RED);

            Ball redBall = new Ball(x, y, targetRadius, redPaint, desiredSpeed);
            redBalls.add(redBall);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgroundImage, 0, 0, null);

        super.onDraw(canvas);

        // Draw the green player ball
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);

        // Draw the target
        canvas.drawCircle(targetX, targetY, targetRadius, targetPaint);

        // Draw all the red balls
        for (Ball ball : redBalls) {
            canvas.drawCircle(ball.x, ball.y, ball.radius, ball.paint);
        }
        // Draw the blue ball
        if (blueBall != null) {
            canvas.drawCircle(blueBall.x, blueBall.y, blueBall.radius, blueBall.paint);
        }

        // Draw the score at the top right
        canvas.drawText("Score: " + score, getWidth() - 10, 50, scorePaint);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Note: We are multiplying by a factor to give a smoother movement. You might want to adjust this.
            float deltaX = -event.values[0]  * sensitivityFactor;
            float deltaY = event.values[1]  * sensitivityFactor;

            // Pblueict the next position of the ball
            float nextX = ballX + deltaX;
            float nextY = ballY + deltaY;

            // Check for collisions with the screen boundaries

            // For X-axis
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

            postInvalidate(); // blueraw the view
        }

        updateBalls();
        checkGameStatus();
    }
    private void checkGameStatus() {
        float distanceToTarget = (float) Math.sqrt((ballX - targetX) * (ballX - targetX) + (ballY - targetY) * (ballY - targetY));
        if (distanceToTarget < ballRadius + targetRadius) {
            // The ball has reached the target, so end the game.
            endGame();
            return;  // Important to return here, so if this condition is met, the next doesn't get checked.
        }

        for (Ball redBall : redBalls) {
            float distance = (float) Math.sqrt((ballX - redBall.x) * (ballX - redBall.x) + (ballY - redBall.y) * (ballY - redBall.y));
            if (distance < ballRadius + redBall.radius) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(500);
                }
                    // The green ball collided with a red ball, so end the game.
                endGame();
                return;
            }
        }

        // Check for collision with blue ball and update score
        if (blueBall != null) {
            float distanceToBlueBall = (float) Math.sqrt((ballX - blueBall.x) * (ballX - blueBall.x) + (ballY - blueBall.y) * (ballY - blueBall.y));
            if (distanceToBlueBall < ballRadius + blueBall.radius) {
                // The ball has collided with the blue ball, so increase the score.
                score += 50;
                // Remove the current blue ball and spawn a new one.
                blueBall = null;
                initializeBlueBall();
            }
        }
    }


    private void endGame() {
        Intent intent = new Intent(getContext(), GameOverActivity.class);
        intent.putExtra("SCORE", score);  // Pass the score to GameOverActivity
        getContext().startActivity(intent);

        // Reset the score
        score = 0;

        // Unregister the sensor listener to stop updates
        sensorManager.unregisterListener(this);
    }



    private void resolveBallCollision(Ball a, Ball b) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0.0) return; // avoid division by zero

        // Collision normal
        float nx = dx / distance;
        float ny = dy / distance;

        // Relative velocity
        float vx = b.dx - a.dx;
        float vy = b.dy - a.dy;

        // Relative velocity along the normal
        float dot = vx * nx + vy * ny;

        // Only solve if balls are moving towards each other
        if (dot > 0) return;

        float restitution = 1.0f; // perfectly elastic collision

        // Impulse magnitude
        float impulse = (-(1 + restitution) * dot) / (1/a.radius + 1/b.radius);

        // Adjust velocities
        a.dx -= (impulse / a.radius) * nx;
        a.dy -= (impulse / a.radius) * ny;
        b.dx += (impulse / b.radius) * nx;
        b.dy += (impulse / b.radius) * ny;

        // Separate balls to avoid overlap/sticking
        float overlap = 0.5f * (distance - a.radius - b.radius);
        a.x -= overlap * nx;
        a.y -= overlap * ny;
        b.x += overlap * nx;
        b.y += overlap * ny;
    }
    private void updateBalls() {
        for (Ball ball : redBalls) {
            ball.x += ball.dx;
            ball.y += ball.dy;

            // Handle collisions with the screen edges for red balls
            handleWallCollision(ball);
        }

        // Update blue ball if it exists
        if (blueBall != null) {
            blueBall.x += blueBall.dx;
            blueBall.y += blueBall.dy;

            // Handle collisions with the screen edges for the blue ball
            handleWallCollision(blueBall);

            // Check collisions between blue ball and red balls
            for (Ball redBall : redBalls) {
                if (ballsAreColliding(blueBall, redBall)) {
                    resolveBallCollision(blueBall, redBall);
                }
            }
        }

        // Detect and handle collisions between red balls
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
    private void handleWallCollision(Ball ball) {
        // Handle collisions with the screen edges
        if (ball.x - ball.radius < 0) {
            ball.x = ball.radius;  // Ensure it's inside the screen
            ball.dx = -ball.dx;    // Reverse direction
        }
        if (ball.x + ball.radius > getWidth()) {
            ball.x = getWidth() - ball.radius; // Ensure it's inside the screen
            ball.dx = -ball.dx;                // Reverse direction
        }
        if (ball.y - ball.radius < 0) {
            ball.y = ball.radius;  // Ensure it's inside the screen
            ball.dy = -ball.dy;    // Reverse direction
        }
        if (ball.y + ball.radius > getHeight()) {
            ball.y = getHeight() - ball.radius; // Ensure it's inside the screen
            ball.dy = -ball.dy;                // Reverse direction
        }
    }
    private boolean ballsAreColliding(Ball a, Ball b) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < a.radius + b.radius;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // This is unused for the current example, but it's requiblue by the SensorEventListener interface.
    }
}