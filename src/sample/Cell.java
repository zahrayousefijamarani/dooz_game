package sample;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;


public class Cell {
    private Rectangle rectangle;
    private Text text;

    public Cell(int x, int y, char a, Group root,int scale){
        rectangle = new Rectangle();
        rectangle.setHeight(scale);
        rectangle.setWidth(scale);
        rectangle.relocate(x,y);
        rectangle.setFill(Color.rgb(44, 247, 240,0.4));
        text = new Text(a+"");
        text.setFill(Color.RED);
        text.setFont(Font.font(50));
        text.relocate(x+20,y);
        root.getChildren().add(rectangle);
        root.getChildren().add(text);
    }

    public String getTextChar() {
        return text.getText();
    }

    public void setText(String text){
        this.text.setText(text);
    }
}
