package model;

import javafx.animation.AnimationTimer;

/**
 * A timer class for managing player countdown time in a JavaFX application.
 * Uses {@link AnimationTimer} to provide smooth 60 FPS countdown updates.
 * Provides methods to start/stop the timer, add time, reset, and check expiration status.
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * PlayerTimer timer = new PlayerTimer(30.0, () -> {
 *     System.out.println("Time's up!");
 * });
 * timer.start();
 * }</pre>
 * 
 * @author JM Rahon
 * @version 1.0
 */
public class PlayerTimer {
    /** Current time remaining in seconds */
    private double timeLeft;
    
    /** Initial time value used for reset operations */
    private final double initialTime;
    
    /** The underlying JavaFX AnimationTimer instance */
    private AnimationTimer timer;
    
    /** Callback executed when time reaches zero */
    private Runnable onTimeUpCallback;

    /**
     * Constructs a new PlayerTimer with the specified initial time and time-up callback.
     * 
     * @param initialTime the initial countdown time in seconds (must be &gt; 0)
     * @param onTimeUpCallback the callback to execute when time expires, or null if none
     */
    public PlayerTimer(double initialTime, Runnable onTimeUpCallback) {
        this.initialTime = initialTime;
        this.timeLeft = initialTime;
        this.onTimeUpCallback = onTimeUpCallback;
    }

    /**
     * Starts the countdown timer. If already running, this method has no effect.
     * The timer decrements time at 60 FPS (approximately 1/60th of a second per frame).
     */
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

    /**
     * Stops the countdown timer. The current timeLeft value is preserved.
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Adds the specified amount of time to the countdown.
     * 
     * @param seconds the amount of time to add (in seconds)
     */
    public void addTime(double seconds) {
        timeLeft += seconds;
    }

    /**
     * Returns the current time remaining in seconds.
     * 
     * @return time remaining in seconds (may be negative if expired)
     */
    public double getTimeLeft() {
        return timeLeft;
    }

    /**
     * Returns a formatted string representation of the remaining time.
     * Format: "X.Xs" (one decimal place).
     * 
     * @return formatted time string (e.g., "25.3s", "0.0s", "-1.2s")
     */
    public String getFormattedTime() {
        return String.format("%.1fs", timeLeft);
    }

    /**
     * Resets the timer to its initial time value and stops any running timer.
     */
    public void reset() {
        stop();
        timeLeft = initialTime;
    }

    /**
     * Checks if the timer has expired (timeLeft ≤ 0).
     * 
     * @return true if time has expired, false otherwise
     */
    public boolean isExpired() {
        return timeLeft <= 0;
    }
}
