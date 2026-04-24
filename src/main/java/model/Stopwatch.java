package model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * A stopwatch class for tracking elapsed game time in a JavaFX application.
 * Uses {@link Timeline} to increment time every second with a tick callback.
 * Provides formatted time display in MM:SS format.
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * Stopwatch stopwatch = new Stopwatch();
 * stopwatch.start(() -> {
 *     System.out.println("Time: " + stopwatch.getFormattedTime());
 *     // Update UI label or other display elements
 * });
 * }</pre>
 * 
 * @author JM Rahon
 * @version 1.0
 */
public class Stopwatch {

    /** Current elapsed time in seconds */
    private int seconds;
    
    /** The underlying JavaFX Timeline for 1-second ticks */
    private Timeline timeline;

    /**
     * Constructs a new Stopwatch initialized to 0 seconds.
     */
    public Stopwatch() {
        seconds = 0;
    }

    /**
     * Starts the stopwatch. Increments time every second and calls the provided tick callback.
     * If already running, this method has no effect.
     * 
     * @param onTick callback executed every second after time increments
     */
    public void start(Runnable onTick) {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            onTick.run();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the stopwatch. Preserves the current elapsed time.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Returns the total elapsed time in seconds.
     * 
     * @return elapsed seconds (integer)
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Returns a formatted time string in MM:SS format (e.g., "01:23", "05:07").
     * 
     * @return formatted time string (always 5 characters: MM:SS)
     */
    public String getFormattedTime() {
        int mins = seconds / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d", mins, secs);
    }

    /**
     * Resets the stopwatch to 0 seconds. Does not affect running state.
     */
    public void reset() {
        seconds = 0;
    }
}
