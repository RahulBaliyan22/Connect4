package com.internshala.connectfour;


import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import java.util.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.out;

public class HelloController implements Initializable {

    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final int CIRCLE_DIAMETER=80;
    private static final String DISC_COLOR1= "#24303E";
    private static final String DISC_COLOR2= "#4CAA88";
    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";
    private boolean isPlayerOneTurn = true;
    private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];
    private boolean isAllowedToInsert=true;

    public GridPane rootGridPane;
    public Pane insertedDiscPane;
    public Label playerName;
    public TextField playerOneTextField;
    public TextField playerTwoTextField;
    public Button setNameButton;


    public void createPlayGround(){
        Shape rectangleWithHoles = createGameStructureGrid();
        rootGridPane.add(rectangleWithHoles,0,1);
        List<Rectangle> rectangle = createClickableColumns();
        for(Rectangle i: rectangle){
            rootGridPane.add(i,0,1);
        }
        setNameButton.setOnAction(actionEvent -> {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
            playerName.setText(PLAYER_ONE);
        });


    }
    private Shape createGameStructureGrid(){
        Shape rectangleWithHoles = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        for(int row = 0;row<ROWS;row++){
            for(int column = 0; column<COLUMNS;column++){

                Circle circle = new Circle();

                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);

                circle.setSmooth(true);
                circle.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

                rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);

            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }
    private List<Rectangle> createClickableColumns(){
        List<Rectangle> rectangleList = new ArrayList<>();
        for(int column = 0; column<COLUMNS;column++){
        Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setTranslateX(column*(CIRCLE_DIAMETER+5)+(CIRCLE_DIAMETER/4));
        rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
        rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT) );

        final int Column = column;
        rectangle.setOnMouseClicked(mouseEvent -> {
            if( isAllowedToInsert){
                isAllowedToInsert=false;
                insertDisc(new Disc(isPlayerOneTurn), Column);
            }
        });
        rectangleList.add(rectangle);
        }

        return rectangleList;

    }
    private void insertDisc(Disc disc, int column) {

        int row =ROWS-1;
        while(row>=0){
            if(getDiscPresent(row,column)==null)
                break;
            row--;
        }
        if(row<0){
            //nothing
            return;
        }
        insertedDiscArray[row][column]=disc;
        insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+(CIRCLE_DIAMETER/4));

        int currentRow = row;
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        translateTransition.setOnFinished(actionEvent -> {
            isAllowedToInsert=true;
            if(gameEnded(currentRow,column)){
                gameOver();
                return;
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerName.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
        });
        translateTransition.play();

    }
    private void gameOver() {
        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;

        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setTitle("Connect Four");
        alert1.setHeaderText("The Winner is "+winner);
        alert1.setContentText("Want to play again?");
        ButtonType yes=new ButtonType("Yes");
        ButtonType no=new ButtonType("No,Exit");
        alert1.getButtonTypes().setAll(yes,no);

        Platform.runLater(()->{
            Optional<ButtonType> btnClicked = alert1.showAndWait();

            if(btnClicked.isPresent()&&btnClicked.get()==yes){
                resetGame();
            }
            if(btnClicked.isPresent()&&btnClicked.get()==no){
                Platform.exit();
                System.exit(0);
            }
        });


    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();
        for (int row = 0;row<insertedDiscArray.length;row++){
            for(int col = 0;col<insertedDiscArray[row].length;col++){
                insertedDiscArray[row][col]=null;
            }
        }
        isPlayerOneTurn=true;
        playerName.setText(PLAYER_ONE);
        createPlayGround();
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove=isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove?Color.valueOf(DISC_COLOR1):Color.valueOf(DISC_COLOR2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }
    private boolean gameEnded(int row, int column){
        //vertical Points point 2D class
        List<Point2D> verticalPoints=IntStream.rangeClosed(row-3,row+3).mapToObj(r->new Point2D(r,column)).collect(Collectors.toList());
        //horizontal points
        List<Point2D> horizontalPoints=IntStream.rangeClosed(column-3,column+3).mapToObj(col->new Point2D(row,col)).collect(Collectors.toList());
        //diagonal points
        Point2D startPoint1=new Point2D(row-3,column+3);
        List<Point2D>diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i->startPoint1.add(i,-i)).collect(Collectors.toList());
        Point2D startPoint2=new Point2D(row-3,column-3);
        List<Point2D>diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i->startPoint2.add(i,i)).collect(Collectors.toList());
        return checkCombination(verticalPoints)||checkCombination(horizontalPoints)||checkCombination(diagonal1Points)||checkCombination(diagonal2Points);
    }
    private boolean checkCombination(List<Point2D> points) {
        int chain = 0;
        for (Point2D point : points) {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscPresent(rowIndexForArray, columnIndexForArray);
            if ((disc != null) && (disc.isPlayerOneMove == isPlayerOneTurn)) {
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }

    private Disc getDiscPresent(int row,int column){
        if(row>=ROWS||row<0||column>=COLUMNS||column<0){
            return null;
        }
        return insertedDiscArray[row][column];
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}