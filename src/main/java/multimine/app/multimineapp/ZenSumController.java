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
    @FXML
    private Text finalTime;

    public void setTime(String time) {
        finalTime.setText(time);
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
