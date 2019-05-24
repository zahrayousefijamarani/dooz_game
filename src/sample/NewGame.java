package sample;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Formatter;
import java.util.Scanner;

class NewGame {
    static void getAccount(Group getNameRoot, Formatter formatter, Stage primaryStage,
                           Scene menuScene, Scene gameScene) {

        GridPane grid = new GridPane();
        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.relocate(300, 300);
//Defining the Name text field
        final TextField name = new TextField();
        name.setPromptText("Enter userName");
        name.setPrefColumnCount(10);
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

        getNameRoot.getChildren().add(grid);
        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);
        submit.setOnAction(event -> {
            if (name.getText().trim().equals("")) {
                label.setText("Invalid");
            } else {
                formatter.format("%s\n", "new game " + name.getText());
                formatter.flush();
                grid.getChildren().remove(label);
                primaryStage.setScene(menuScene);

            }
        });

    }
}
