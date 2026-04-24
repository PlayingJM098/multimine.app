package multimine.app.multimineapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.RadioButton;
public class SettingsController {
    @FXML private TextField player1Field, player2Field;
    @FXML private Button closeBtn;
    @FXML
    private RadioButton easyRadio;

    @FXML
    private RadioButton mediumRadio;

    @FXML
    private RadioButton hardRadio;
    
    private static String player1Name = "Player 1";
    private static String player2Name = "Player 2";
    private static String difficulty = "Easy";
    public static String getDifficulty() { return difficulty; }

    @FXML
    public void initialize() {
        player1Field.setText(player1Name);
        player2Field.setText(player2Name);
        player1Field.selectAll();
    }
    
    @FXML
    private void openMain() {
        player1Name = player1Field.getText().trim().isEmpty() ? "Player 1" : player1Field.getText().trim();
        player2Name = player2Field.getText().trim().isEmpty() ? "Player 2" : player2Field.getText().trim();
        
        saveDifficulty();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(loader.load());
            
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Main Menu");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveDifficulty() {
        if (easyRadio.isSelected()) difficulty = "Easy";
        else if (mediumRadio.isSelected()) difficulty = "Medium";
        else if (hardRadio.isSelected()) difficulty = "Hard";
        else difficulty = "Easy";
    }
    public static String getPlayer1Name() { 
        return player1Name; 
    }
    
    public static String getPlayer2Name() { 
        return player2Name; 
    }
    
    public static void setPlayer1Name(String name) { 
        player1Name = name.isEmpty() ? "Player 1" : name; 
    }
    
    public static void setPlayer2Name(String name) { 
        player2Name = name.isEmpty() ? "Player 2" : name; 
    }
}
