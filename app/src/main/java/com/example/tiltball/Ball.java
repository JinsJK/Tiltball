//Authors: Jins & Sailesh

package com.example.tiltball;

import android.graphics.Paint;

// Class for Ball generation
public class Ball {
    public float x, y, dx, dy, radius;
    public Paint paint;

    public float speed;

    // Constructor for Ball
    public Ball(float x, float y, float radius, Paint paint, float speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.paint = paint;
        this.speed = speed;

        // Initialize random directions for the balls
        double angle = 2 * Math.PI * Math.random();
        this.dx = (float) Math.cos(angle) * speed;
        this.dy = (float) Math.sin(angle) * speed;
    }
}
