package sample;

import sample.Client;
import java.util.Formatter;
import java.util.Scanner;

public class ThreadForGetFromClient extends Thread {
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
                    formatter.format("%s\n", lineOfOrder);
                    formatter.flush();
                }
        }
    }

}