package multimine.app.multimineapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
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

    public void openZen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("zen.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Zen");
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
    public void openMultiplayer(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("multiplayer.fxml")
        );

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Multiplayer");
        stage.show();
    }   

}
