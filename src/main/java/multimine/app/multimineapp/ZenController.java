package multimine.app.multimineapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Tile;           
import model.Stopwatch;
import model.ZenBoard;
import java.util.Objects;

public class ZenController {

    @FXML private Text timerText;
    @FXML private GridPane grid;

    @FXML private ImageView heart1;
    @FXML private ImageView heart2;
    @FXML private ImageView heart3;

    private final int SIZE = 15;
    private Image[] numberTiles = new Image[9];
    private Image flagTile;
    private ZenBoard board;
    private Stopwatch stopwatch = new Stopwatch();

    private Image hiddenTile =
            new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/img/tile.png")));

    private Image bombTile =
            new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/img/bomb.png")));

    @FXML
    public void initialize() {
        flagTile = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/img/flag.png")));
        
        loadNumberTiles();
        
        board = new ZenBoard(SIZE, hiddenTile, flagTile, bombTile, numberTiles, grid, 
                           tile -> showZenSummary(tile, stopwatch.getFormattedTime()), 
                           this::updateHearts, stopwatch);
        
        board.initializeBoard(10);
        setupClickHandlers();
        
        stopwatch.start(() ->
                timerText.setText(stopwatch.getFormattedTime())
        );
    }

    private void loadNumberTiles() {
        for (int i = 0; i <= 8; i++) {
            numberTiles[i] = new Image(
                    getClass().getResourceAsStream("/img/" + i + ".png")
            );
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

    public void updateHearts(int minesCount) {
        if (minesCount == 1) heart3.setVisible(false);
        if (minesCount == 2) heart2.setVisible(false);
        if (minesCount == 3) heart1.setVisible(false);
    }

    private void showZenSummary(ImageView tile, String time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("zensum.fxml"));
            Parent root = loader.load();
            ZenSumController controller = loader.getController();
            controller.setTime(time);

            Stage stage = (Stage) tile.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Zen Summary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
