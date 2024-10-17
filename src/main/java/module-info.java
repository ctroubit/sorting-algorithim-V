module se2203.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens se2203.sorting to javafx.fxml;
    exports se2203.sorting;
}