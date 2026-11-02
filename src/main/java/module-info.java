module com.example.taberna {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.taberna to javafx.fxml;
    exports com.example.taberna;
}