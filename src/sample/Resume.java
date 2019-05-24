package sample;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.awt.font.TextMeasurer;
import java.util.ArrayList;
import java.util.Formatter;


class Resume {
    static void showResume(ArrayList<String> strings, Group resumeRoot, Stage primaryStage,
                           Formatter formatter, Scene menuScene) {
        Rectangle rectangle;
        for (int i = 0; i < strings.size(); i++) {
            rectangle = new Rectangle(200, 30 * i + 10, 200, 25);
            rectangle.setFill(Color.LIGHTGRAY);
            resumeRoot.getChildren().add(rectangle);
            MakeText.textMaker(210, 30 * i + 17, resumeRoot, strings.get(i));
        }

        GridPane grid = new GridPane();
        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.relocate(100, 720);
//Defining the Name text field
        final TextField n = new TextField();
        n.setPromptText("Enter Number");
        n.setPrefColumnCount(10);
        GridPane.setConstraints(n, 0, 0);
        grid.getChildren().add(n);

        resumeRoot.getChildren().add(grid);
        Button submit = new Button("Enter");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);
        submit.setOnAction(event -> {
            if (n.getText().trim().equals("")) {
                label.setText("Invalid");
            } else if (!n.getText().matches("\\d+")) {
                label.setText("Please enter correct number");
            } else {
                formatter.format("%s\n", n.getText());
                formatter.flush();
                grid.getChildren().clear();
            }

        });


        Client.makeButton("back", 550, 730, resumeRoot).setOnMouseClicked(event -> {
            resumeRoot.getChildren().clear();
            primaryStage.setScene(menuScene);
            formatter.format("%s\n", "back");
            formatter.flush();
        });
    }


}
