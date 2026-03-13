package multimine.app.multimineapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;

public class MainController {

    public void openHelp(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("help.fxml")
        );

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("How to Play");
        stage.show();
    }
    @FXML
    public void openSettings(MouseEvent event) throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("settings.fxml")
        );

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Settings");
        stage.show();
    }
}


