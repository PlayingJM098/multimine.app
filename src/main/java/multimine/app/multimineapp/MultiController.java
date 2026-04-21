package multimine.app.multimineapp;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.MultiBoard;
import java.util.Objects;

public class MultiController {
    @FXML private Text timerText;
    @FXML private Text player2TimerText; 
    @FXML private GridPane grid;
    @FXML private Text turnText;
    
    @FXML private ImageView heart1, heart2, heart3;  // Player 1 hearts
    @FXML private ImageView heart4, heart5, heart6;  // Player 2 hearts

    private final int SIZE = 15;
    private Image[] numberTiles = new Image[9];
    private Image flagTile, hiddenTile, bombTile, heartImage;
    private MultiBoard board;
    private AnimationTimer gameTimer;

    @FXML
    public void initialize() {
        loadImages();
        board = new MultiBoard(SIZE, hiddenTile, flagTile, bombTile, heartImage, 
                             numberTiles, grid, this);
        board.initializeBoard(10);
    }

    private void loadImages() {
        hiddenTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tile.png")));
        bombTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/bomb.png")));
        flagTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/flag.png")));
        heartImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/heart.png")));
        
        for (int i = 0; i <= 8; i++) {
            numberTiles[i] = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/" + i + ".png")));
        }
    }

    public void startPlayerTimer() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (board.isPlayer1Turn()) {
                    board.setPlayer1Time(board.getPlayer1Time() - 1.0 / 60.0); // UPDATE MODEL TIME
                    timerText.setText(String.format("P1: %.1fs", board.getPlayer1Time()));

                    if (board.getPlayer1Time() <= 0) {
                        board.setPlayer1CumulativeTime(board.getPlayer1CumulativeTime() + 10.0);
                        endGame(board.getPlayer1Name(), board.getPlayer1CumulativeTime(), 
                               board.getPlayer2Name(), board.getPlayer2CumulativeTime(), false);
                        return;
                    }
                } else {
                    board.setPlayer2Time(board.getPlayer2Time() - 1.0 / 60.0); // UPDATE MODEL TIME
                    player2TimerText.setText(String.format("P2: %.1fs", board.getPlayer2Time()));

                    if (board.getPlayer2Time() <= 0) {
                        board.setPlayer2CumulativeTime(board.getPlayer2CumulativeTime() + 10.0);
                        endGame(board.getPlayer1Name(), board.getPlayer1CumulativeTime(), 
                               board.getPlayer2Name(), board.getPlayer2CumulativeTime(), false);
                        return;
                    }
                }
            }
        };
        gameTimer.start();
    }

    private void updateTimerText(double time, boolean isPlayer1) {
        if (isPlayer1) {
            timerText.setText(String.format("P1: %.1fs", time));
        } else {
            player2TimerText.setText(String.format("P2: %.1fs", time));
        }
    }

    public void updateUI() {
        turnText.setText("It's " + board.getCurrentPlayerName() + "'s turn");
        timerText.setText(String.format("P1: %.1fs", board.getPlayer1Time()));
        player2TimerText.setText(String.format("P2: %.1fs", board.getPlayer2Time()));
    }

    public void updatePlayer1Hearts(int lives) {
        heart1.setVisible(lives >= 1);
        heart2.setVisible(lives >= 2);
        heart3.setVisible(lives >= 3);
    }

    public void updatePlayer2Hearts(int lives) {
        heart4.setVisible(lives >= 1);
        heart5.setVisible(lives >= 2);
        heart6.setVisible(lives >= 3);
    }

    public void endGame(String p1Name, double p1Time, String p2Name, double p2Time, boolean teamWin) {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("multisum.fxml"));
            Parent root = loader.load();
            MultiSumController controller = loader.getController();
            controller.setResults(p1Name, p1Time, p2Name, p2Time, teamWin);

            Stage stage = (Stage) grid.getScene().getWindow();
            if (stage == null) return;
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
