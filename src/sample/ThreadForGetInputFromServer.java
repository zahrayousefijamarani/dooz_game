package sample;

import com.google.gson.Gson;
import dooz.Table;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Formatter;
import java.util.Scanner;

import static sample.Client.serverAnswer;

class ThreadForGetInputFromServer extends Thread {
    private Scanner scanner;
    private Stage primaryStage;
    private Scene gameScene;
    private Formatter formatter;

    ThreadForGetInputFromServer(Scanner scanner,Scene gameScene, Stage primaryStage,Formatter formatter) {
        this.scanner = scanner;
        this.primaryStage = primaryStage;
        this.gameScene = gameScene;
        this.formatter = formatter;
    }

    public void run() {
        try {
            while (true) {
                if (scanner.hasNextLine()) {
                    serverAnswer = scanner.nextLine();
                    if (serverAnswer.equals("start a game")) {
                        while (true){
                            if(scanner.hasNextLine()){
                                serverAnswer = scanner.nextLine();
                                break;
                            }
                        }
                        Client.gameStart = true;
                    }
                    if(serverAnswer.equals("end game")){
                        Client.endGame = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//                    if (serverAnswer.equals("start a game")) {
//                        Client.endGame = false;
//                    }
//
//                    if (getJson) {
//                        synchronized (Client.lockForCounter) {
//                            Client.counterForUpdate++;
//                        }
//                        synchronized (Client.lock) {
//                           // Client.setUpdateTable(true);
//                        }
//                        Gson gson = new Gson();
//                        Client.table = gson.fromJson(serverAnswer, Table.class);
//                        getJson = false;
//                        if (Client.counterForUpdate == 1)
//                            synchronized (Client.lockForStartGame) {
//                                //Client.setGameStart(true);
//                            }
//                        continue;
//
//                    }
//                    if (serverAnswer.equals("json")) {
//                        getJson = true;
//                        continue;
//                    }
//                    if (serverAnswer.contains("end game")) {
//                        synchronized (Client.lockForStartGame) {
//                            Client.counterForUpdate = 0;
//                           // Client.setGameStart(false);
//                            Client.setUpdateTable(false);
//                        }
//                        Client.endGame = true;
//                    }
//
//                    System.out.println(serverAnswer);
//
//                    if (serverAnswer.equals("end")) {
//                        Client.setEndOfClient(true);
//                        return;
//                    }