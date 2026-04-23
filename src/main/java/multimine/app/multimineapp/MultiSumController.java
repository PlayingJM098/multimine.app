package multimine.app.multimineapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MultiSumController {
    @FXML private Text titleText;
    @FXML private Text resultsText;

    public void setResults(String p1Name, double p1Time, String p2Name, double p2Time, boolean teamWin) {
        if (teamWin) {
            titleText.setText("TEAM VICTORY!");
        } else {
            titleText.setText(p1Time < p2Time ? "🏆 " + p1Name + " Wins!" : "🏆 " + p2Name + " Wins!");
        }
    }

    public void handleRematch(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("multiplayer.fxml"));
            Scene mainScene = new Scene(mainRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Multiplayer");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void handleMenu(ActionEvent event) {
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
}
