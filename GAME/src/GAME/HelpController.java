package GAME;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpController {

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
}