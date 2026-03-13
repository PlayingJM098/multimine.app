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
import java.util.Objects;
import model.Stopwatch;

public class ZenController {
    @FXML
    private Text timerText;
    @FXML
    private ImageView heart1;
    @FXML
    private ImageView heart2;
    @FXML
    private ImageView heart3;
    private Stopwatch stopwatch = new Stopwatch();
    @FXML
    private GridPane grid;

    private final int SIZE = 15;

    private Tile[][] board;

    private Image hiddenTile = new Image(getClass().getResourceAsStream("/img/tile.png"));
    private Image revealedTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/revealed.png")));
    private Image bombTile = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/explode.png")));

    @FXML
    public void initialize() {

        board = new Tile[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                ImageView view = new ImageView(hiddenTile);
                view.setFitWidth(24);
                view.setFitHeight(24);
                view.setPickOnBounds(true);


                Tile tile = new Tile(row, col, view);
                board[row][col] = tile;

                int r = row;
                int c = col;

                view.setOnMouseClicked(e -> handleClick(r, c));

                grid.add(view, col, row);
            }
        }
        stopwatch.start(() -> {
            timerText.setText(stopwatch.getFormattedTime());
        });
        placeMines();
    }

    private void placeMines() {

        board[2][3].setMine(true);
        board[5][5].setMine(true);
        board[7][10].setMine(true);
        board[10][1].setMine(true);
        board[12][12].setMine(true);
    }
    int minesCount = 0;
    private void handleClick(int row, int col) {

        Tile tile = board[row][col];

        if (tile.isRevealed()) return;

        tile.reveal();

        if (tile.hasMine()) {

            tile.getView().setImage(bombTile);

            minesCount++;
            switch (minesCount){
                case 1:
                    heart3.setVisible(false);
                    break;
                case 2:
                    heart2.setVisible(false);
                    break;
                case 3:
                    heart1.setVisible(false);
                    break;
            }
            if (minesCount >= 3) {
                stopwatch.stop();
                showZenSummary(tile.getView());
            }

        } else {

            tile.getView().setImage(revealedTile);

        }

        tile.getView().setDisable(true);
    }
    private void showZenSummary(ImageView tile) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("zensum.fxml")
            );

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
