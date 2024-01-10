module Tetris {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens com.danielpclin to javafx.fxml;
    exports com.danielpclin;
}