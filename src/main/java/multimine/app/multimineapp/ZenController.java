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
    private ZenBoard board;
    private int minesCount = 0;

    private Stopwatch stopwatch = new Stopwatch();

    private Image hiddenTile =
            new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/img/tile.png")));

    private Image bombTile =
            new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/img/explode.png")));

    @FXML
    public void initialize() {

        board = new ZenBoard(SIZE);

        board.createBoard(hiddenTile, grid);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                int r = row;
                int c = col;

                board.getTile(r,c).getView().setOnMouseClicked(e -> handleClick(r,c));
            }
        }
        for (int i = 0; i <= 8; i++) {
            numberTiles[i] = new Image(
                    getClass().getResourceAsStream("/img/" + i + ".png")
            );
        }
        board.placeMines(10);
        stopwatch.start(() ->
                timerText.setText(stopwatch.getFormattedTime())
        );
    }

    private void handleClick(int row, int col) {

        Tile tile = board.getTile(row, col);

        if (tile.isRevealed()) return;

        tile.reveal();

        if (tile.hasMine()) {

            tile.getView().setImage(bombTile);

            minesCount++;
            updateHearts();

            if (minesCount >= 3) {
                stopwatch.stop();
                showZenSummary(tile.getView());
            }

        } else {

            int count = board.countAdjacentMines(row, col);

            tile.getView().setImage(numberTiles[count]);
        }

        tile.getView().setDisable(true);
    }

    private void updateHearts() {

        if (minesCount == 1) heart3.setVisible(false);
        if (minesCount == 2) heart2.setVisible(false);
        if (minesCount == 3) heart1.setVisible(false);
    }

    private void showZenSummary(ImageView tile) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("zensum.fxml"));

            Parent root = loader.load();

            ZenSumController controller = loader.getController();
            controller.setTime(stopwatch.getFormattedTime());

            Stage stage = (Stage) tile.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Zen Summary");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
