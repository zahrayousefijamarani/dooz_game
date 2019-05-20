package sample;


import com.google.gson.Gson;
import dooz.Table;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


class ThreadForGetFromClient extends Thread {
    private Scanner scannerInput;
    private Formatter formatter;

    ThreadForGetFromClient(Scanner scannerInput, Formatter formatter) {
        this.scannerInput = scannerInput;
        this.formatter = formatter;
    }

    public void run() {
        String lineOfOrder;
        while (true) {
            if (Client.endOfClient) {
                return;
            }
            if (!Client.gameStart)
                if (scannerInput.hasNextLine()) {
                    //Client.scoreOn =false;
                    lineOfOrder = scannerInput.nextLine();
                    formatter.format("%s\n", lineOfOrder);//todo
                    formatter.flush();
                }
        }
    }

}

class ThreadForGetInputFromServer extends Thread {
    private Scanner scanner;

    ThreadForGetInputFromServer(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        int counterForUpdate = 0;
        boolean getJson = false;
        String serverAnswer;
        try {
            while (true) {
                if (scanner.hasNextLine()) {
                    serverAnswer = scanner.nextLine();
                    if (serverAnswer == null)
                        continue;
//                    if (Client.getScoreOn()) {
//                        Client.serialInputFromServer.add(serverAnswer);
//                    }
//                    System.out.println("size:"+Client.serialInputFromServer.size());
                    if (getJson) {
                        synchronized (Client.lock) {
                            counterForUpdate++;
                            Client.setUpdateTable(true);
                        }
                        System.out.println("hi");
                        Gson gson = new Gson();
                        Client.table = gson.fromJson(serverAnswer, Table.class);
                        getJson = false;
                        continue;
                    }
                    if (serverAnswer.equals("json")) {
                        getJson = true;
                        continue;
                    }

                    if (counterForUpdate == 1) {
                        synchronized (Client.lockForStartGame) {
                            Client.setGameStart(true);
                        }
                    }

                    if (serverAnswer.contains("end game")) {
                        synchronized (Client.lockForStartGame) {
                            counterForUpdate = 0;
                            Client.setGameStart(false);
                        }
                    }

                    System.out.println(serverAnswer);

//                    if (serverAnswer.equals("start a game")) {//todo in first update on table
//                        synchronized (Client.lockForStartGame) {
//                            Client.setGameStart(true);
//                        }
//                    }
                    if (serverAnswer.equals("end")) {
                        Client.setEndOfClient(true);
                        return;
                    }
//                    else if (serverAnswer.contains("new game")) {
//                        if (serverAnswer.contains("can not play")) {
//                            System.out.println("can not start a game");
//                            break;
//                        } else if (serverAnswer.equals("start a game")) {
//                            System.out.println("you are playing");
//                            break;
//
//                        }
//                    }
                }


            }

        } catch (Exception e) {

        }
    }
}

public class Client extends Application {
    static final Object lock = new Object();
    static final Object lockForStartGame = new Object();
    static boolean endOfClient = false;
    private static Scanner scannerInput = new Scanner(System.in);
    private static boolean updateTable = false;
    static Table table;
    private int scale = 50;
    public static boolean gameStart = false;
    private Group menuRoot = new Group();
    private static Cell[][] cells;
    static Formatter formatter;

    static void setGameStart(boolean gameStart) {
        synchronized (lockForStartGame) {
            Client.gameStart = gameStart;
        }
    }

    static void setUpdateTable(boolean gameStart) {
        synchronized (lock) {
            Client.updateTable = gameStart;
        }
    }

    //    private static void requestForGame(String state
//            ,Formatter formatter,String userName,BufferedReader reader) throws IOException{
//        String lineOfOrder;
//        String opponent;
//        opponent = state.split(" ")[1];
//        formatter.format("game with %s", opponent);//wanna play
//        formatter.flush();
//
//        while (true) {
//            lineOfOrder = reader.readLine();
//            if(lineOfOrder != null) {
//                if (lineOfOrder.equals("game with "+opponent+" accepted")) {
//
//                    while (true) {
//                        if(scanner.hasNextLine()) {
//                            lineOfOrder = scanner.nextLine();
//                            if (reader.readLine().trim().equals(userName)) {
//                                state = game(lineOfOrder,formatter);
//                                if (!state.equals("game")) {
//                                    //formatter.format("end game");
//                                    return;
//                                }
//                            }
//                            else
//                                System.out.println("invalid input");
//                        }
//                    }
//                } else {
//                    System.out.println("can not start game");
//                    return;
//                }
//            }
//
//        }
//
//    }
//
//
//    private static String game(String lineOfOrder,Formatter formatter){
//        Menu menuOfTheGame = Menu.getInstance();
//        String state = "game";
//        int x,y;
//        Pattern putPattern = Pattern.compile("put\\((\\d+),(\\d+)\\)( *\\w+)*");
//        Matcher putMatcher = putPattern.matcher(lineOfOrder);
//
//        if (putMatcher.find()) {
//            if (putMatcher.group(3) != null) {
//                System.out.println("Invalid command");
//                menuOfTheGame.presentGame().showTheGame();
//                return state;
//            }
//            x = Integer.parseInt(putMatcher.group(1));
//            y = Integer.parseInt(putMatcher.group(2));
//            if (menuOfTheGame.presentGame().put(x, y)) {//todo messages for winning should forward to both
//                formatter.format()
//                state = "menu";
//                menuOfTheGame.deleteGame();
//            }
//        } else if (lineOfOrder.trim().equals("undo")) {//todo tell server it is my turn
//            formatter.format("undo");
//            menuOfTheGame.presentGame().undo();
//        } else if (lineOfOrder.trim().equals("pause")) {//todo tell server resume
//            formatter.format("resume");
//            state = "menu";
//        } else if (lineOfOrder.trim().equals("stop")) {//todo tell server end game delete game
//            menuOfTheGame.deleteGame();
//            formatter.format("stop");
//            state = "menu";
//        } else {
//            System.out.println("Invalid command");
//            menuOfTheGame.presentGame().showTheGame();
//        }
//        return state;
//    }
//    public static boolean getScoreOn(){
//        return scoreOn;
//    }
//    public static void setScoreOn(boolean scoreOn) {
//        Client.scoreOn = scoreOn;
//    }

//    public void start(Stage primaryStage) {
//        Group submitRoot = new Group();
//        Scene submitScene = new Scene(submitRoot, 800, 800, Color.rgb(179, 255, 255));
//        Group menuRoot = new Group();
//        Scene menuScene = new Scene(menuRoot, 800, 800, Color.rgb(77, 255, 255));
//        Group scoreBoardRoot = new Group();
//        Scene scoreBoardScene = new Scene(scoreBoardRoot, 800, 800, Color.rgb(0, 230, 230));
//
//        GridPane grid = new GridPane();
//        final javafx.scene.control.Label label = new Label();
//        GridPane.setConstraints(label, 0, 3);
//        GridPane.setColumnSpan(label, 2);
//        grid.getChildren().add(label);
//        grid.setPadding(new Insets(10, 10, 10, 10));
//        grid.setVgap(5);
//        grid.setHgap(5);
//        grid.relocate(300, 300);
//        final javafx.scene.control.TextField name = new TextField();
//        name.setPromptText("Enter your UserName.");
//        name.setPrefColumnCount(10);
//        GridPane.setConstraints(name, 0, 0);
//        grid.getChildren().add(name);
//        submitRoot.getChildren().add(grid);
//        javafx.scene.control.Button submit = new Button("Submit");
//        GridPane.setConstraints(submit, 1, 0);
//        grid.getChildren().add(submit);
//        submit.setOnAction(event -> {
//            if (name.getText().trim().equals("")) {
//                label.setText("You have not enter username");
//            } else {
//                formatter.format("%s\n", name.getText());
//                formatter.flush();
//                Platform.runLater(() -> {
//                    while (true) {
//                        if (scanner.hasNextLine())
//                            break;
//                    }
//                    if (!scanner.nextLine().trim().equals(name.getText() + " accepted")) {
//                        grid.getChildren().remove(label);
//                        primaryStage.setScene(menuScene);
//                    } else {
//                        label.setText("Enter correct name");
//                    }
//                });
//            }
//        });
//
//        Button scoreBoardButton = new Button("ScoreBoard");
//        scoreBoardButton.setPrefSize(200, 30);
//        scoreBoardButton.setTextFill(Color.PINK);
//        menuRoot.getChildren().add(scoreBoardButton);
//        scoreBoardButton.relocate(800 / 2 - 100, 200);
//        scoreBoardButton.setOnAction(event -> {
//            setScoreOn(true);
//            formatter.format("%s\n", "scoreboard");
//            formatter.flush();
////            Platform.runLater(() -> {
////                while (true){
////                    if(scanner.hasNextLine()){
////                        Client.serialInputFromServer.add(scanner.nextLine());
////                        while (scanner.hasNextLine()){
////                            Client.serialInputFromServer.add(scanner.nextLine());
////                        }
////                        break;
////                    }
////                }
////            });
//                try {
//                    Thread.sleep(600);
//                }catch (Exception e){
//                    System.out.println(e);
//                }
//            primaryStage.setScene(scoreBoardScene);
//            ScoreBoard.showScoreBoard(menuScene, scoreBoardRoot, primaryStage, Client.serialInputFromServer);
//        });
//
//        Button quitButton = new Button("QUIT");
//        quitButton.setPrefSize(200, 30);
//        quitButton.setTextFill(Color.PINK);
//        menuRoot.getChildren().add(quitButton);
//        quitButton.relocate(800 / 2 - 100, 240);
//        quitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Quit Dialog");
//                alert.setHeaderText("Quit from game");
//                alert.setContentText("Are you sure?");
//
//                Optional<ButtonType> result = alert.showAndWait();
//                if (result.get() == ButtonType.OK) {
//                    formatter.format("%s\n", "quit");
//                    formatter.flush();
//                    primaryStage.close();
//                    endOfClient = true;
//                }
//            }
//        });
//
//        GridPane gridForSetTable = new GridPane();
//        final javafx.scene.control.Label labelForSetTable = new Label();
//        GridPane.setConstraints(labelForSetTable, 0, 3);
//        GridPane.setColumnSpan(labelForSetTable, 2);
//        gridForSetTable.getChildren().add(labelForSetTable);
//        gridForSetTable.setPadding(new Insets(10, 10, 10, 10));
//        gridForSetTable.setVgap(5);
//        gridForSetTable.setHgap(5);
//        gridForSetTable.relocate(300, 330);
//        final javafx.scene.control.TextField size = new TextField();
//        size.setPromptText("set table in format n*m");
//        size.setPrefColumnCount(10);
//        GridPane.setConstraints(name, 0, 0);
//        gridForSetTable.getChildren().add(size);
//        menuRoot.getChildren().add(gridForSetTable);
//        javafx.scene.control.Button submitForTable = new Button("Submit");
//        GridPane.setConstraints(submitForTable, 1, 0);
//        gridForSetTable.getChildren().add(submitForTable);
//        submitForTable.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                if (size.getText().trim().equals("")) {
//                    labelForSetTable.setText("You have not enter number");
//                } else {
//                    formatter.format("set table %s\n", name.getText());
//                    formatter.flush();
//                }
//            }
//        });
//
//
//        primaryStage.setScene(submitScene);
//        primaryStage.show();
//    }

    private static void updateTableF() {
        for (int i = 0; i < table.getN(); i++) {
            for (int j = 0; j < table.getM(); j++) {
                cells[i][j].setText(table.gameTable[i][2 * j] + "");
            }
        }

    }

    public void start(Stage primaryStage) {

        Scene menuScene = new Scene(menuRoot, 800, 800, Color.rgb(77, 255, 255));

        char[][] tables = table.gameTable;
        cells = new Cell[table.getN()][table.getM()];//todo at first

        for (int i = 0; i < table.getN(); i++) {
            for (int j = 0; j < table.getM(); j++) {
                cells[i][j] = new Cell((3 + scale) * i + 3, (3 + scale) * j + 3, tables[i][2 * j], menuRoot, scale);
            }
        }

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
        name.setPromptText("Enter command");
        name.setPrefColumnCount(10);
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

        menuRoot.getChildren().add(grid);
        Button submit = new Button("ENTER");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (name.getText().trim().equals("")) {
                    label.setText("You have not enter username");
                } else {
                    formatter.format("%s\n", name.getText());
                    formatter.flush();
                    grid.getChildren().remove(label);
                }

            }
        });


        primaryStage.setScene(menuScene);
        primaryStage.show();


    }

    static void setEndOfClient(boolean endOfClient) {
        Client.endOfClient = endOfClient;
    }

    public static void main(String[] args) {
        try {

            Socket socket = new Socket("127.0.0.1", 8888);
            OutputStream outputStream = socket.getOutputStream();
            formatter = new Formatter(outputStream);
            InputStream inputStream = socket.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            String userName;


            //for make username
            do {
                System.out.println("Please Enter Your UserName");
                if (!socket.isConnected())
                    return;
                userName = scannerInput.nextLine();
                formatter.format("%s\n", userName);
                formatter.flush();
                while (true) {
                    if (scanner.hasNextLine())
                        break;
                }
            } while (!scanner.nextLine().trim().equals(userName + " accepted"));

            new ThreadForGetInputFromServer(scanner).start();
            new ThreadForGetFromClient(scannerInput, formatter).start();
            //

            while (true) {
                synchronized (lockForStartGame) {
                    if (gameStart) {
                        launch(args);
                        break;
                    }
                }
            }

            do {
                synchronized (lock) {
                    if (updateTable) {
                        //launch(args);
                        updateTableF();
                        System.out.println("here");
                        updateTable = false;
                    }
                }
            } while (!endOfClient);

        }


//            new Thread(() -> {
//                String string = scanner.nextLine();
//                while (!string.equals("poweroff") && socket.isConnected()) {
//                    formatter.format(clientName + ": " + string + "\n");
//                    formatter.flush();
//                    string = scanner.nextLine();
//                }
//                try {
//                    socket.close();
//                } catch (IOException ignored) {
//                }
//            }).start();
//
//            new Thread(() -> {
//                try {
//                    Scanner scanner = new Scanner(socket.getInputStream());
//
//                    while (true) {
//                        if (scanner.hasNextLine()) {
//                            System.out.println(scanner.nextLine());
//                        }
//                    }
//                } catch (IOException ignored) {
//                }
//            }).start();
//
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}