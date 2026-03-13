package multimine.app.multimineapp;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {
    @FXML
    private void openMain(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("Main.fxml")
        );

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Main");
        stage.show();
    }
}
