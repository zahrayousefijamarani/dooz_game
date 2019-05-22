package sample;


import com.google.gson.Gson;
import dooz.Table;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;


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
        boolean getJson = false;
        String serverAnswer;
        Client.counterForUpdate = 0;
        try {
            while (true) {
                if (scanner.hasNextLine()) {
                    serverAnswer = scanner.nextLine();
                    if (serverAnswer == null)
                        continue;

                    if (serverAnswer.equals("start a game")) {
                        Client.endGame = false;
                    }

                    if (getJson) {
                        synchronized (Client.lockForCounter) {
                            Client.counterForUpdate++;
                        }
                        synchronized (Client.lock) {
                            Client.setUpdateTable(true);
                        }
                        Gson gson = new Gson();
                        Client.table = gson.fromJson(serverAnswer, Table.class);
                        getJson = false;
                        if (Client.counterForUpdate == 1)
                            synchronized (Client.lockForStartGame) {
                                Client.setGameStart(true);
                            }
                        continue;

                    }
                    if (serverAnswer.equals("json")) {
                        getJson = true;
                        continue;
                    }
                    if (serverAnswer.contains("end game")) {
                        synchronized (Client.lockForStartGame) {
                            Client.counterForUpdate = 0;
                            Client.setGameStart(false);
                            Client.setUpdateTable(false);
                        }
                        Client.endGame = true;
                    }

                    System.out.println(serverAnswer);

                    if (serverAnswer.equals("end")) {
                        Client.setEndOfClient(true);
                        return;
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Client extends Application {
    static final Object lock = new Object();
    static final Object lockForStartGame = new Object();
    static final Object lockForCounter = new Object();
    static boolean endOfClient = false;
    private static Scanner scannerInput = new Scanner(System.in);
    private static boolean updateTable = false;
    static Table table;
    static boolean gameStart = false;
    private Group menuRoot = new Group();
    private static Cell[][] cells;
    private static Formatter formatter;
    static int counterForUpdate = 0;
    private static String userName;
    static boolean endGame = false;

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

    public void start(Stage primaryStage) {
        Scene menuScene = new Scene(menuRoot, 800, 800, Color.rgb(77, 255, 255));

        char[][] tables = table.gameTable;
        cells = new Cell[table.getN()][table.getM()];

        for (int i = 0; i < table.getN(); i++) {
            for (int j = 0; j < table.getM(); j++) {
                int scale = 100;
                cells[i][j] = new Cell((3 + scale) * i + 3, (3 + scale) * j + 3, tables[i][2 * j], menuRoot, scale);
            }
        }

        Text text = new Text(userName);
        text.relocate(350, 710);
        text.setFont(Font.font(50));
        text.setFill(Color.BLACK);
        text.setUnderline(true);
        menuRoot.getChildren().add(text);

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

        menuRoot.getChildren().add(grid);
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
        menuRoot.getChildren().add(stopButton);
        stopButton.relocate(670, 610);
        stopButton.setOnMouseClicked(event -> {
            formatter.format("%s\n", "stop");
            formatter.flush();
            primaryStage.close();
        });

        Button undoButton = new Button("UNDO");
        undoButton.setPrefSize(100, 30);
        undoButton.setTextFill(Color.PINK);
        menuRoot.getChildren().add(undoButton);
        undoButton.relocate(670, 670);
        undoButton.setOnMouseClicked(event -> {
            formatter.format("%s\n", "undo");
            formatter.flush();
        });

        Button pauseButton = new Button("PAUSE");
        pauseButton.setPrefSize(100, 30);
        pauseButton.setTextFill(Color.PINK);
        menuRoot.getChildren().add(pauseButton);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}