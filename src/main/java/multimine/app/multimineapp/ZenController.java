package multimine.app.multimineapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ZenController {
    @FXML
    private GridPane grid; // from fxml

    private ImageView[][] board; // 2d arrray initializationfgfdd

    @FXML
    public void initialize() {

    int rows = 15;
    int cols = 15;

        board = new ImageView[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                ImageView imageView = new ImageView();
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);

                board[row][col] = imageView;

                grid.add(imageView, col, row); 
            }
        }
}
    
}
