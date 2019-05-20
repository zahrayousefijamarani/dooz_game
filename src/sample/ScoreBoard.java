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

import java.util.ArrayList;
import java.util.Optional;


class ScoreBoard {
    static void showScoreBoard(Scene menuScene, Group scoreBoardRoot, Stage primaryStage, ArrayList<String> users){
        Rectangle rectangle;
        System.out.println(users.size());
        for(int i = 0 ;i< users.size();i++){
            rectangle = new Rectangle(200,30*i+10,200,25);
            rectangle.setFill(Color.LIGHTGRAY);
            scoreBoardRoot.getChildren().add(rectangle);
        }

        Button quitButton = new Button("QUIT");
        quitButton.setPrefSize(200, 30);
        quitButton.setTextFill(Color.PINK);
        scoreBoardRoot.getChildren().add(quitButton);
        quitButton.relocate(800 / 2 - 100, 700);
        quitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Quit Dialog");
                alert.setHeaderText("Quit from game");
                alert.setContentText("Are you sure?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //Client.scoreOn = false;
                   // users.clear();
                    primaryStage.setScene(menuScene);
                }
            }
        });

    }
}
