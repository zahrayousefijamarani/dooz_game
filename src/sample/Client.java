package sample;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dooz.Table;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class Client extends Application {
    static final Object lock = new Object();
    static final Object lockForStartGame = new Object();
    static final Object lockForCounter = new Object();
    static boolean endOfClient = false;
    private static Scanner scannerInput = new Scanner(System.in);
    private static boolean updateTable = false;
    static Table table;
    static boolean gameStart = false;
    private static Cell[][] cells;
    private static Formatter formatter;
    static int counterForUpdate = 0;
    private static String userName;
    static boolean endGame = false;
    static Scanner inputFromServer;

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

    private static void updateTableF() {
        for (int i = 0; i < table.getN(); i++) {
            for (int j = 0; j < table.getM(); j++) {
                cells[i][j].setText(table.gameTable[i][2 * j] + "");
            }
        }

    }

    static Button makeButton(String input,int x,int y,Group root){
        Button button = new Button(input);
        button.setPrefSize(200, 30);
        button.setTextFill(Color.PINK);
        root.getChildren().add(button);
        button.relocate(x, y);
        return button;
    }

    public void start(Stage primaryStage) {
        Group menuRoot = new Group();
        Scene menuScene = new Scene(menuRoot, 800, 800, Color.rgb(10, 50, 100));
        Group scoreBoardRoot = new Group();
        Scene scoreBoardScene = new Scene(scoreBoardRoot, 800, 800, Color.rgb(50, 171, 200));
        Group resumeRoot = new Group();
        Scene resumeScene = new Scene(resumeRoot, 800, 800, Color.rgb(90, 230, 230, 0.2));
        Group setTableRoot = new Group();
        Scene setTableScene = new Scene(setTableRoot, 800,800, Color.rgb(80, 230, 250, 0.6));
        Group gameRoot = new Group();
        Scene gameScene = new Scene(gameRoot, 800, 800, Color.rgb(77, 255, 255));

        makeButton("New Game",300,200,menuRoot).setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
        makeButton("resume",300,240,menuRoot).setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });
        makeButton("scoreboard",300,280,menuRoot).setOnMouseClicked(event -> {
            formatter.format("%s\n","scoreBoard");
            formatter.flush();
           while (true){
               if(scannerInput.hasNextLine()){
                   Gson gson = new Gson();
                   TypeToken<List<String>> a = new TypeToken<>();
                   ArrayList<String> strings= gson.fromJson(inputFromServer.nextLine(), a.getType());
                   ScoreBoard.showScoreBoard(menuScene,scoreBoardRoot,primaryStage,strings);
                   break;
               }
           }

        });

        makeButton("setTable",300,320,menuRoot).setOnMouseClicked(event -> SetTable.getTable(setTableRoot,formatter,menuScene,primaryStage));

        makeButton("quit",300,360,menuRoot).setOnMouseClicked(event -> {
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


        char[][] tables = table.gameTable;
        cells = new Cell[table.getN()][table.getM()];

        for (int i = 0; i < table.getN(); i++) {
            for (int j = 0; j < table.getM(); j++) {
                int scale = 100;
                cells[i][j] = new Cell((3 + scale) * i + 3, (3 + scale) * j + 3, tables[i][2 * j], gameRoot, scale);
            }
        }

        Text text = new Text(userName);
        text.relocate(350, 710);
        text.setFont(Font.font(50));
        text.setFill(Color.BLACK);
        text.setUnderline(true);
        gameRoot.getChildren().add(text);

        GridPane grid = new GridPane();
        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.relocate(100, 700);
//Defining the Name text field
        final TextField name = new TextField();
        name.setPromptText("Enter command");
        name.setPrefColumnCount(10);
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

        gameRoot.getChildren().add(grid);
        Button submit = new Button("ENTER");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);
        submit.setOnAction(event -> {
            if (name.getText().trim().equals("")) {
                label.setText("You have not enter username");
            } else {
                formatter.format("%s\n", name.getText());
                formatter.flush();
                grid.getChildren().remove(label);
            }

        });
        Button stopButton = new Button("STOP");
        stopButton.setPrefSize(100, 30);
        stopButton.setTextFill(Color.PINK);
        gameRoot.getChildren().add(stopButton);
        stopButton.relocate(670, 610);
        stopButton.setOnMouseClicked(event -> {
            formatter.format("%s\n", "stop");
            formatter.flush();
            primaryStage.close();
        });

        Button undoButton = new Button("UNDO");
        undoButton.setPrefSize(100, 30);
        undoButton.setTextFill(Color.PINK);
        gameRoot.getChildren().add(undoButton);
        undoButton.relocate(670, 670);
        undoButton.setOnMouseClicked(event -> {
            formatter.format("%s\n", "undo");
            formatter.flush();
        });

        Button pauseButton = new Button("PAUSE");
        pauseButton.setPrefSize(100, 30);
        pauseButton.setTextFill(Color.PINK);
        gameRoot.getChildren().add(pauseButton);
        pauseButton.relocate(670, 730);
        pauseButton.setOnMouseClicked(event -> {
            formatter.format("%s\n", "pause");
            formatter.flush();
            primaryStage.close();
        });


        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTableF();
                Client.setUpdateTable(false);
            }
        };
        animationTimer.start();

//        AnimationTimer animationTimer1 = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                if (!Client.gameStart) {
//                    System.out.println("hiii");
//                    primaryStage.close();
//                }
//            }
//        };
//        animationTimer1.start();

        primaryStage.setScene(gameScene);
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


            //for make username
            do {
                System.out.println("Please Enter Your UserName");
                if (!socket.isConnected())
                    return;
                userName = scannerInput.nextLine();
                formatter.format("%s\n", userName);
                formatter.flush();
                while (true) {
                    if (inputFromServer.hasNextLine())
                        break;
                }
            } while (!inputFromServer.nextLine().trim().equals(userName + " accepted"));

           // new ThreadForGetInputFromServer(inputFromServer).start();
            //new ThreadForGetFromClient(scannerInput, formatter).start();
            //

            while (true) {
                synchronized (lockForStartGame) {
                    if (gameStart) {
                        launch(args);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}