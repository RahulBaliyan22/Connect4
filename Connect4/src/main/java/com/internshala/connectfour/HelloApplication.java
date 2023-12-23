package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private HelloController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("game.fxml"));
        Parent rootGridPane=fxmlLoader.load();
        controller=fxmlLoader.getController();
        controller.createPlayGround();

        MenuBar mb = createBar();
        mb.prefWidthProperty().bind(stage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildrenUnmodifiable().get(0);
        mb.prefHeightProperty().bind(menuPane.heightProperty());
        menuPane.getChildren().add(mb);

        Scene scene = new Scene(rootGridPane);
        stage.setTitle("Connect Four");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    private MenuBar createBar(){
        Menu file = new Menu("File");
        Menu help = new Menu("Help");

        SeparatorMenuItem separator = new SeparatorMenuItem();
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem play= new MenuItem("New Game");
        play.setOnAction(actionEvent -> resetGame());
        MenuItem reset = new MenuItem("Reset Game");
        reset.setOnAction(actionEvent -> resetGame());
        MenuItem exit = new MenuItem("Exit Game");
        exit.setOnAction(actionEvent -> exitGame());
        MenuItem con = new MenuItem("About Connect4");
        con.setOnAction(actionEvent -> aboutConnect4());
        MenuItem me = new MenuItem("About Me");
        me.setOnAction(actionEvent -> aboutMe());

        file.getItems().addAll(play,reset,separator,exit);
        help.getItems().addAll(con,separatorMenuItem,me);

        MenuBar mb = new MenuBar();
        mb.getMenus().addAll(file,help);
        return mb;
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Rahul Baliyan");
        alert.setContentText("I'm a passionate Programmer");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefHeight(300);
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        controller.resetGame();
    }

    public static void main(String[] args) {
        launch();
    }
}