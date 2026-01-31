module multimine.app.multimineapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens multimine.app.multimineapp to javafx.fxml;
    exports multimine.app.multimineapp;
}