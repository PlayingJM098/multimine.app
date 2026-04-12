package multimine.app.multimineapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ZenSumController {
    @FXML private Text finalTime;
    @FXML private Text titleText;
    @FXML private Text messageText;
    
    public void setTime(String time, boolean isWin) {
        finalTime.setText(time);
        if (isWin) {
            titleText.setText("YOU WIN!");
            messageText.setText("All safe tiles are cleared!");
        } else {
            titleText.setText("GAME OVER");
            messageText.setText("You clicked on a mine 3 times!");
        }
    }
    public void handleCloseButtonAction(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene mainScene = new Scene(mainRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRetryButtonAction(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("zen.fxml"));
            Scene mainScene = new Scene(mainRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Zen");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
