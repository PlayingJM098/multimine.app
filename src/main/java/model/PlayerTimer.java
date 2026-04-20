package model;

import javafx.animation.AnimationTimer;

public class PlayerTimer {
    private double timeLeft;
    private final double initialTime;
    private AnimationTimer timer;
    private Runnable onTimeUpCallback;

    public PlayerTimer(double initialTime, Runnable onTimeUpCallback) {
        this.initialTime = initialTime;
        this.timeLeft = initialTime;
        this.onTimeUpCallback = onTimeUpCallback;
    }

    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                timeLeft -= 1.0 / 60.0; // 60 FPS
                if (timeLeft <= 0) {
                    stop();
                    if (onTimeUpCallback != null) {
                        onTimeUpCallback.run();
                    }
                }
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void addTime(double seconds) {
        timeLeft += seconds;
    }

    public double getTimeLeft() {
        return timeLeft;
    }

    public String getFormattedTime() {
        return String.format("%.1fs", timeLeft);
    }

    public void reset() {
        stop();
        timeLeft = initialTime;
    }

    public boolean isExpired() {
        return timeLeft <= 0;
    }
}
