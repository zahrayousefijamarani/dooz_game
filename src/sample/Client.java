package sample;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dooz.Table;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class Client extends Application {
    private static boolean endOfClient = false;
    private static Table table;
    static boolean gameStart = false;
    private static Cell[][] cells;
    private static Formatter formatter;
    private static String userName;
    static boolean endGame = false;
    private static Scanner inputFromServer;
    private static char[][] tables;
    static String serverAnswer;
    private static boolean enteredUserName = false;

    private static void updateTableF() {
        Gson gson = new Gson();
        table = gson.fromJson(serverAnswer, Table.class);
        tables = table.gameTable;
        for (int i = 0; i < table.getN(); i++) {
            for (int j = 0; j < table.getM(); j++) {
                cells[i][j].setText(table.gameTable[i][2 * j] + "");
            }
        }

    }

    static Button makeButton(String input, int x, int y, Group root) {
        Button button = new Button(input);
        button.setPrefSize(200, 30);
        button.setTextFill(Color.RED);
        root.getChildren().add(button);
        button.relocate(x, y);
        return button;
    }

    public void start(Stage primaryStage) {
        Group getUserNameRoot = new Group();
        Scene getUserNameScene = new Scene(getUserNameRoot, 800, 800, Color.rgb(65, 80, 249));
        Group menuRoot = new Group();
        Scene menuScene = new Scene(menuRoot, 800, 800, Color.rgb(10, 204, 255));
        Group scoreBoardRoot = new Group();
        Scene scoreBoardScene = new Scene(scoreBoardRoot, 800, 800, Color.rgb(50, 171, 200));
        Group resumeRoot = new Group();
        Scene resumeScene = new Scene(resumeRoot, 800, 800, Color.rgb(90, 230, 230, 0.2));
        Group setTableRoot = new Group();
        Scene setTableScene = new Scene(setTableRoot, 800, 800, Color.rgb(80, 230, 250, 0.6));
        Group gameRoot = new Group();
        Scene gameScene = new Scene(gameRoot, 800, 800, Color.rgb(77, 255, 255));
        Group getNameRoot = new Group();
        Scene getNameScene = new Scene(getNameRoot, 800, 800, Color.rgb(100, 150, 255));


        GridPane gridStart = new GridPane();
        final Label labelStart = new Label();
        labelStart.setTextFill(Color.BLACK);
        GridPane.setConstraints(labelStart, 0, 3);
        GridPane.setColumnSpan(labelStart, 2);
        gridStart.getChildren().add(labelStart);
        gridStart.setPadding(new Insets(10, 10, 10, 10));
        gridStart.setVgap(5);
        gridStart.setHgap(5);
        gridStart.relocate(300, 300);
//Defining the Name text field
        final TextField name = new TextField();
        name.setPromptText("Enter your userName.");
        name.setPrefColumnCount(10);
        GridPane.setConstraints(name, 0, 0);
        gridStart.getChildren().add(name);

        getUserNameRoot.getChildren().add(gridStart);
        Button submitButton = new Button("Submit");
        GridPane.setConstraints(submitButton, 1, 0);
        gridStart.getChildren().add(submitButton);
        submitButton.setOnAction(event -> {
            if (name.getText().trim().equals("")) {
                labelStart.setText("You have not enter username");
            } else {
                userName = name.getText();
                formatter.format("%s\n", userName);
                formatter.flush();
                while (true) {
                    if (serverAnswer != null) {
                        break;
                    }
                }
                if (serverAnswer.trim().equals(userName + " accepted")) {
                    primaryStage.setScene(menuScene);
                    primaryStage.setTitle(userName);
                    enteredUserName = true;
                    serverAnswer = null;
                } else {
                    labelStart.setText("Enter your name");
                }

            }

        });

        new ThreadForGetInputFromServer(inputFromServer, gameScene, primaryStage, formatter).start();


        makeButton("New Game", 300, 200, menuRoot).setOnMouseClicked(event -> {
            primaryStage.setScene(getNameScene);
            NewGame.getAccount(getNameRoot, formatter, primaryStage, menuScene, gameScene);

        });
        makeButton("resume", 300, 240, menuRoot).setOnMouseClicked(event -> {
            formatter.format("%s\n", "resume");
            formatter.flush();
            while (true) {
                try {
                    formatter.flush();
                    if (serverAnswer != null) {
                        Gson gson = new Gson();
                        ArrayList<String> strings = gson.fromJson(serverAnswer, new TypeToken<List<String>>() {
                        }.getType());
                        primaryStage.setScene(resumeScene);
                        Resume.showResume(strings, resumeRoot, primaryStage, formatter, menuScene, gameScene);
                        serverAnswer = null;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        makeButton("scoreboard", 300, 280, menuRoot).setOnMouseClicked(event -> {
            formatter.format("%s\n", "scoreboard");
            formatter.flush();
            while (true) {
                formatter.flush();
                if (serverAnswer != null) {
                    Gson gson = new Gson();
                    ArrayList<String> strings = gson.fromJson(serverAnswer,
                            new TypeToken<List<String>>() {
                            }.getType());
                    primaryStage.setScene(scoreBoardScene);
                    ScoreBoard.showScoreBoard(menuScene, scoreBoardRoot, primaryStage, strings, formatter);
                    serverAnswer = null;
                    break;

                }
            }

        });

        makeButton("setTable", 300, 320, menuRoot).setOnMouseClicked(event -> {
            primaryStage.setScene(setTableScene);
            SetTable.getTable(setTableRoot, formatter, menuScene, primaryStage);

        });

        makeButton("quit", 300, 360, menuRoot).setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quit Dialog");
            alert.setHeaderText("Quit from game");
            alert.setContentText("Are you sure?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                primaryStage.close();
                formatter.format("quit");
            }
        });

        AnimationTimer animationTimer = new AnimationTimer() {
            double NLENGTH, MLENGTH;
            final double DISTANCE = 10;

            @Override
            public void handle(long now) {
                if (gameStart && serverAnswer != null) {
                    Gson gson = new Gson();
                    table = gson.fromJson(serverAnswer, Table.class);
                    tables = table.gameTable;
                    NLENGTH = (700 - DISTANCE * (table.getN() + 2)) / (float) table.getN();
                    MLENGTH = (700 - DISTANCE * (table.getM() + 2)) / (float) table.getM();
                    cells = new Cell[table.getN()][table.getM()];
                    gameStart = false;
                    for (int i = 0; i < table.getN(); i++) {
                        for (int j = 0; j < table.getM(); j++) {
                            cells[i][j] = new Cell((j + 1) * DISTANCE + MLENGTH * j,
                                    (i + 1) * DISTANCE + NLENGTH * i, tables[i][2 * j], gameRoot, NLENGTH,MLENGTH);
                        }
                    }
                    primaryStage.setScene(gameScene);
                    serverAnswer = null;
                }
                if(endGame){
                    primaryStage.setScene(menuScene);
                    endGame = false;
                    serverAnswer = null;
                }
            }


        };
        animationTimer.start();

        makeButton("UNDO", 170, 710, gameRoot).setOnMouseClicked(event -> {
            formatter.format("%s\n","undo");
            formatter.flush();
            while (true){
                if(serverAnswer !=null){
                    updateTableF();
                    serverAnswer = null;
                    break;
                }
            }

        });
        makeButton("PAUSE", 380, 710, gameRoot).setOnMouseClicked(event -> {
            formatter.format("%s\n","pause");
            formatter.flush();
        });
        makeButton("STOP", 590, 710, gameRoot).setOnMouseClicked(event -> {
           formatter.format("%s\n","stop");
           formatter.flush();

        });

        GridPane grid = new GridPane();
        final Label label = new Label();
        label.setTextFill(Color.BLACK);
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.relocate(2, 750);
//Defining the Name text field
        final TextField order = new TextField();
        order.setPromptText("put (x,y)");
        order.setPrefColumnCount(10);
        GridPane.setConstraints(order, 0, 0);
        grid.getChildren().add(order);

        gameRoot.getChildren().add(grid);
        Button button = new Button("Enter");
        GridPane.setConstraints(button, 1, 0);
        grid.getChildren().add(button);
        button.setOnAction(event -> {
            if (name.getText().trim().equals("")) {
                label.setText("You have not enter username");
            } else {
                while (true){
                    if(serverAnswer !=null){
                        updateTableF();
                        serverAnswer = null;
                        break;
                    }
                }
            }

        });


        primaryStage.setScene(getUserNameScene);
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
            inputFromServer = new Scanner(inputStream);

            launch(args);

            //for make username
//            do {
//                System.out.println("Please Enter Your UserName");
//                if (!socket.isConnected())
//                    return;
//                userName = scannerInput.nextLine();
//                formatter.format("%s\n", userName);
//                formatter.flush();
//                while (true) {
//                    if (inputFromServer.hasNextLine())
//                        break;
//                }
//            } while (!inputFromServer.nextLine().trim().equals(userName + " accepted"));

            // new ThreadForGetInputFromServer(inputFromServer).start();
            //new ThreaForGetFromClient(scannerInput, formatter).start();
            //

//            while (true) {
//                synchronized (lockForStartGame) {
//                    if (gameStart) {
//                        launch(args);
//                        break;
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}