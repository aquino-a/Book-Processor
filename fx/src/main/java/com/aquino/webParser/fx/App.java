package com.aquino.webParser.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource("view/main.fxml"));
        var scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Jeein's Web Parser");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
