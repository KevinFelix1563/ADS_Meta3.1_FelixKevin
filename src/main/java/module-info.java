module com.example.taberna {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.taberna to javafx.fxml;
    exports com.example.taberna;
    exports com.example.taberna.eventbus;
    exports com.example.taberna.dominio.eventos;
    exports com.example.taberna.dominio.actores;
}