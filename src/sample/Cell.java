package sample;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;


class Cell {
    private Rectangle rectangle;
    private Text text;

    Cell(double x, double y, char a, Group root, double height, double width) {
        rectangle = new Rectangle();
        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.relocate(x, y);
        rectangle.setFill(Color.rgb(44, 247, 255, 0.4));
        text = new Text(a + "");
        text.setFill(Color.BLACK);
        text.setFont(Font.font(50));
        text.relocate(x + width / 2 - 5, y + height / 2 - 5);
        root.getChildren().add(rectangle);
        root.getChildren().add(text);
    }

    void setText(String text) {
        this.text.setText(text);
    }

    Rectangle getRectangle(){
        return rectangle;
    }

    Text getText() {
        return text;
    }
}
