package multimine.app.multimineapp;

//Truth - Domingo, Esteban, Rahon
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(loader.load(), 474, 330);
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);   
        primaryStage.setMaximized(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
