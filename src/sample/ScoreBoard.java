package sample;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.text.Format;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Optional;


class ScoreBoard {
    static void showScoreBoard(Scene menuScene, Group scoreBoardRoot, Stage primaryStage,
                               ArrayList<String> users, Formatter formatter){
        Rectangle rectangle;
        System.out.println(users.size());
        for(int i = 0 ;i< users.size();i++){
            rectangle = new Rectangle(200,30*i+10,200,25);
            rectangle.setFill(Color.LIGHTGRAY);
            scoreBoardRoot.getChildren().add(rectangle);
        }


        Client.makeButton("back",550,720,scoreBoardRoot).setOnMouseClicked(event -> {
            scoreBoardRoot.getChildren().clear();
            primaryStage.setScene(menuScene);
            formatter.format("%s\n","back");
            formatter.flush();
        });
        Client.makeButton("quit",100,720,scoreBoardRoot).setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quit Dialog");
            alert.setHeaderText("Quit from game");
            alert.setContentText("Are you sure?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                primaryStage.close();
                formatter.format("%s\n","quit");
                formatter.flush();
            }
        });


    }
}
