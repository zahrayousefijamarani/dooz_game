package sample;

import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Client;
import java.util.Formatter;
import java.util.Scanner;

import static sample.Client.serverAnswer;

public class ThreadForStartAGame extends Thread {
    private Stage primaryStage;
    private Scene gameScene;
    private Formatter formatter;


    ThreadForStartAGame(Scene gameScene, Stage primaryStage,Formatter formatter){
        this.primaryStage = primaryStage;
        this.gameScene = gameScene;
        this.formatter = formatter;
    }

    public void run() {
        while (true) {
            //System.out.println("sout");
            //formatter.flush();
            if (serverAnswer != null && serverAnswer.equals("start a game")) {
                primaryStage.setScene(gameScene);
                Client.gameStart = true;
                serverAnswer = null;
            }
        }
    }

}