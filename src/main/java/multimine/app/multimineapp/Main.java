package multimine.app.multimineapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
/* Screens submitted
    Main Scene (Main.fxml)
    Zen Game Summary Scene (scree.fxml)
    Help/Instructions Scene (help.fxml)*/
public class Main extends Application {
    @Override
   
    public void start(Stage primaryStage) throws Exception {
                                                  //change the filename to load other screens                               
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(loader.load(), 470, 400);
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
