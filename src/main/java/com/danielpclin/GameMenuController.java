package com.danielpclin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
public class GameMenuController{

    @FXML private VBox root;

    @FXML
    private void startServer(ActionEvent event) throws IOException{
        GameController gameController = switchToGameView();
        if (gameController!=null){
            gameController.startServer();
        }
    }

    @FXML
    private void startClient(ActionEvent event) throws IOException{
        GameController gameController = switchToGameView();
        if (gameController!=null){
            gameController.startClient();
        }
    }

    private GameController switchToGameView(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/gameLayout.fxml"));
            BorderPane root = loader.load();
            this.root.getScene().setRoot(root);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something wrong with fxml file.");
            return null;
        }
    }
}
