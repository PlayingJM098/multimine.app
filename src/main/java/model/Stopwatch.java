package model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Stopwatch {

    private int seconds;
    private Timeline timeline;

    public Stopwatch() {
        seconds = 0;
    }

    public void start(Runnable onTick) {

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            onTick.run();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public String getFormattedTime() {

        int mins = seconds / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d", mins, secs);
    }

    public void reset() {
        seconds = 0;
    }
}
