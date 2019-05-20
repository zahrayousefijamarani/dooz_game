package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Land extends Application {
    private int n, m;
    final static int scale = 20;
    private Cell[][] cells;

    public Land(int m, int n) {
        this.n = n;
        this.m = m;
        cells = new Cell[n][m];

    }

    public Cell[][] getCells() {
        return cells;
    }

    public void start(Stage primaryStage){
        Group root = new Group();
        Scene scene = new Scene(root, m * (scale + 3) + 3, n * (scale + 3) + 3, Color.rgb(176, 242, 242));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                cells[i][j] = new Cell((3 + scale) * i + 3, (3 + scale) * j + 3, ' ',root,scale);
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
