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

    public void setResults(String p1Name, double p1TotalTime, String p2Name, double p2TotalTime, boolean teamWin) {
        if (teamWin) {
            titleText.setText("TEAM VICTORY!");
            resultsText.setText(String.format("%s: %.1fs | %s: %.1fs", p1Name, p1TotalTime, p2Name, p2TotalTime));
        } else if (p1TotalTime < p2TotalTime) {  // Lower total time = better (less time wasted)
            titleText.setText(p1Name + " WINS!");
            resultsText.setText(String.format("%s: %.1fs | %s: %.1fs", p1Name, p1TotalTime, p2Name, p2TotalTime));
        } else {
            titleText.setText(p2Name + " WINS!");
            resultsText.setText(String.format("%s: %.1fs | %s: %.1fs", p1Name, p1TotalTime, p2Name, p2TotalTime));
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
