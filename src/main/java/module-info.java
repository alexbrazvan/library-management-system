module com.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.library to javafx.fxml;
    exports com.library;
}
