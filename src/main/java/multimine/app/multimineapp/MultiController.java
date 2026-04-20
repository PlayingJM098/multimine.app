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
import model.Tile;
import java.util.Objects;

public class MultiController {
    @FXML private Text timerText;
    @FXML private Text player2TimerText; 
    @FXML private GridPane grid;
    @FXML private Text turnText;
    
    @FXML private ImageView heart1, heart2, heart3;  // Player 1 hearts
    @FXML private ImageView heart4, heart5, heart6;  // Player 2 hearts (right side)

    private final int SIZE = 15;
    private Image[] numberTiles = new Image[9];
    private Image flagTile, hiddenTile, bombTile, heartImage;
    private MultiBoard board;
    private int clicksThisTurn = 0;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private boolean player1Turn = true;
    private int player1Lives = 3;
    private int player2Lives = 3;
    private double player1Time = 10.0;  // 10 seconds
    private double player2Time = 10.0;
    private AnimationTimer gameTimer;
    private double player1CumulativeTime = 0.0;
    private double player2CumulativeTime = 0.0;

    @FXML
    public void initialize() {
        loadImages();
        board = new MultiBoard(SIZE, hiddenTile, flagTile, bombTile, heartImage, 
                             numberTiles, grid, this);
        board.initializeBoard(10);
        setupClickHandlers();
        updateUI();
        startPlayerTimer();
    }

    private void loadImages() {
        hiddenTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/tile.png")));
        bombTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/bomb.png")));
        flagTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/flag.png")));
        heartImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/heart.png")));
        
        for (int i = 0; i <= 8; i++) {
            numberTiles[i] = new Image(getClass().getResourceAsStream("/img/" + i + ".png"));
        }
    }
    public void playerHitMine(int totalMinesHit) {
        if (player1Turn) {
            player1CumulativeTime += (10.0 - player1Time);
            player1Lives--;
            updatePlayer1Hearts();
        } else {
            player2CumulativeTime += (10.0 - player2Time);
            player2Lives--;
            updatePlayer2Hearts();
        }

        if (isGameOver()) {
            endGame(false);
        }
    }

    private void setupClickHandlers() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int r = row, c = col;
                Tile tile = board.getTile(r, c);
                tile.getView().setOnMouseClicked(e -> {
                    if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                        board.handleRightClick(r, c);
                    } else {
                        board.handleClick(r, c);
                    }
                });
            }
        }
    }
    
    public void playerSafeClick() {
        if (player1Turn) {
            player1Time += 0.1;
        } else {
            player2Time += 0.1;
        }
    }

    public boolean isGameOver() {
        return player1Lives <= 0 || player2Lives <= 0;
    }

    private void startPlayerTimer() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (player1Turn) {
                    player1Time -= 1.0 / 60.0;
                    timerText.setText(String.format("P1: %.1fs", player1Time));
                    if (player1Time <= 0) {
                        // ADD time SPENT to cumulative total
                        player1CumulativeTime += 10.0;  // Full 10s used
                        endGame(false);
                        return;
                    }
                } else {
                    player2Time -= 1.0 / 60.0;
                    player2TimerText.setText(String.format("P2: %.1fs", player2Time));
                    if (player2Time <= 0) {
                        player2CumulativeTime += 10.0;  // Full 10s used
                        endGame(false);
                        return;
                    }
                }
            }
        };
        gameTimer.start();
    }
    public void registerClick() {
        clicksThisTurn++;

        if (clicksThisTurn >= 2) {
            clicksThisTurn = 0;
            switchTurn();
        }
    }
    public boolean isPlayer1Turn() {
        return player1Turn;
    }
    private void switchTurn() {
        player1Turn = !player1Turn;
        updateUI();
    }

    private void updateUI() {
        turnText.setText("It's " + (player1Turn ? player1Name : player2Name) + "'s turn");
        timerText.setText(String.format("P1: %.1fs", player1Time));
        player2TimerText.setText(String.format("P2: %.1fs", player2Time));  // UPDATE
    }

    private void updatePlayer1Hearts() {
        heart1.setVisible(player1Lives >= 1);
        heart2.setVisible(player1Lives >= 2);
        heart3.setVisible(player1Lives >= 3);
    }

    private void updatePlayer2Hearts() {
        heart4.setVisible(player2Lives >= 1);
        heart5.setVisible(player2Lives >= 2);
        heart6.setVisible(player2Lives >= 3);
    }

    public void endGame(boolean teamWin) {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("multisum.fxml"));
            Parent root = loader.load();
            MultiSumController controller = loader.getController();
            controller.setResults(player1Name, player1CumulativeTime, player2Name, player2CumulativeTime, teamWin);

            Stage stage = (Stage) grid.getScene().getWindow();
            if (stage == null) return;
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
