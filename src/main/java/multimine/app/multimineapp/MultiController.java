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
    @FXML private Text player1NameText, player2NameText; 
    @FXML private ImageView heart1, heart2, heart3;  
    @FXML private ImageView heart4, heart5, heart6;  

    private int SIZE = 15;
    private Image[] numberTiles = new Image[9];
    private Image flagTile, hiddenTile, bombTile, heartImage;
    private MultiBoard board;
    private AnimationTimer gameTimer;
    private final double BOARD_WIDTH = 360.0;  
    private double TILE_SIZE;
    @FXML
    public void initialize() {
        loadImages();
        SIZE = getSizeForDifficulty();
        TILE_SIZE = BOARD_WIDTH / SIZE;
        String p1Name = SettingsController.getPlayer1Name();  
        String p2Name = SettingsController.getPlayer2Name();  

        board = new MultiBoard(SIZE, hiddenTile, flagTile, bombTile, heartImage, 
                              numberTiles, grid, this);
        board.setPlayer1Name(p1Name); 
        board.setPlayer2Name(p2Name);  

        player1NameText.setText(p1Name);    
        player2NameText.setText(p2Name);     
        board.setTileSize(TILE_SIZE);  
        board.initializeBoard(getMinesForDifficulty());
        board.initializeBoard(20);
        board.resetGameState();
        startPlayerTimer();
    }
    private int getMinesForDifficulty() {
        return switch(SettingsController.getDifficulty()) {
            case "Easy" -> 20;
            case "Medium" -> 40;
            case "Hard" -> 60;
            default -> 20;
        };
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
        if (gameTimer != null) gameTimer.stop();

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (board.isPlayer1Turn()) {
                    double newTime = board.getPlayer1Time() - 1.0 / 60.0;
                    board.setPlayer1Time(newTime);
                    timerText.setText(String.format("P1: %.1fs", newTime));
                    if (newTime <= 0) {
                        endGame("Player 1", 0.0, "Player 2", board.getPlayer2Time(), false);
                        return;
                    }
                } else {
                    double newTime = board.getPlayer2Time() - 1.0 / 60.0;
                    board.setPlayer2Time(newTime);
                    player2TimerText.setText(String.format("P2: %.1fs", newTime));
                    if (newTime <= 0) {
                        endGame("Player 1", board.getPlayer1Time(), "Player 2", 0.0, false);
                        return;
                    }
                }
            }
        };
        gameTimer.start();
    }

    public void updateUI() {
        turnText.setText("It's " + board.getCurrentPlayerName() + "'s turn");

        player2TimerText.setText(String.format("P2: %.1fs", board.getPlayer2Time())); 
        timerText.setText(String.format("P1: %.1fs", board.getPlayer1Time()));         
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
    private int getSizeForDifficulty() {
        return switch (SettingsController.getDifficulty()) {
            case "Easy" -> 15;    
            case "Medium" -> 20;  
            case "Hard" -> 25;    
            default -> 15;
        };
    }
    public void restartBoard() {
        SIZE = getSizeForDifficulty();
        TILE_SIZE = BOARD_WIDTH / SIZE;

        grid.getChildren().clear();

        board = new MultiBoard(
            SIZE,
            hiddenTile,
            flagTile,
            bombTile,
            heartImage,
            numberTiles,
            grid,
            this
        );

        board.setTileSize(TILE_SIZE);
        board.initializeBoard(getMinesForDifficulty());
        board.resetGameState();
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
