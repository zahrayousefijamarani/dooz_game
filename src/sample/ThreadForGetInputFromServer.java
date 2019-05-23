package sample;

import com.google.gson.Gson;
import dooz.Table;

import java.util.Scanner;

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