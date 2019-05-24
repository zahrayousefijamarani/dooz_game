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

class SetTable {
    static void getTable(Group setTableRoot, Formatter formatter, Scene menuScene, Stage primaryStage) {


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
        final TextField n = new TextField();
        n.setPromptText("Enter N");
        n.setPrefColumnCount(10);
        GridPane.setConstraints(n, 0, 0);
        grid.getChildren().add(n);
//Defining n text field
        final TextField m = new TextField();
        m.setPromptText("Enter M");
        GridPane.setConstraints(m, 0, 1);
        grid.getChildren().add(m);

        setTableRoot.getChildren().add(grid);
        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);
        submit.setOnAction(event -> {
            if (n.getText().trim().equals("") || m.getText().trim().equals("")) {
                label.setText("Invalid");
            } else if (!m.getText().matches("\\d+") || !n.getText().matches("\\d+")) {
                label.setText("Please enter correct number");
            } else {
                formatter.format("%s\n", "set table " + n.getText() + "*" + m.getText());
                formatter.flush();
                grid.getChildren().clear();
                primaryStage.setScene(menuScene);
            }

        });

    }
}
