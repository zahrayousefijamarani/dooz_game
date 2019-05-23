package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MakeText {
    static Text textMaker(int x, int y, Group root,String input){
        Text text =new Text(input);
        text.relocate(x,y);
        text.setFont(Font.font(20));
        text.setFill(Color.BLACK);
        root.getChildren().add(text);
        return text;
    }

}
