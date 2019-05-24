package sample;


import java.util.Scanner;

import static sample.Client.serverAnswer;

class ThreadForGetInputFromServer extends Thread {
    private Scanner scanner;


    ThreadForGetInputFromServer(Scanner scanner) {
        this.scanner = scanner;

    }

    public void run() {
        try {
            while (true) {
                if (scanner.hasNextLine()) {
                    serverAnswer = scanner.nextLine();
                    if (serverAnswer.equals("start a game")) {
                        while (true) {
                            if (scanner.hasNextLine()) {
                                serverAnswer = scanner.nextLine();
                                break;
                            }
                        }
                        Client.gameStart = true;
                    }
                    if (serverAnswer.equals("end game")) {
                        Client.endGame = true;
                        serverAnswer = null;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}