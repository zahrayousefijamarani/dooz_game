package dooz;

import com.google.gson.Gson;

import java.util.Formatter;

public class Game {
    private Player[] playerForOneGame = new Player[2];
    private Table table;
    private int m, n;
    private int turn = 1;
    private int[] lastMoveOfFirst = new int[2];
    private int[] lastMoveOfSecond = new int[2];
    private boolean undoForPlayer1 = false, undoForPlayer2 = false;
    private Formatter formatter ;

    public int getTurn() {
        return turn;
    }

    public Player[] getPlayerForOneGame() {
        return playerForOneGame;
    }

    public void setPlayerForOneGame(Player[] playerForOneGame) {
        this.playerForOneGame = playerForOneGame;
    }

    Game(Player player1, Player player2, int m, int n, Formatter formatter) {
        lastMoveOfFirst[0] = lastMoveOfFirst[1] = -1;
        lastMoveOfSecond[0] = lastMoveOfSecond[1] = -1;
        this.m = m;
        this.n = n;
        this.formatter = formatter;
        this.playerForOneGame[0]= player1;
        this.playerForOneGame[1] = player2;
        //land = new Land(m,n);
        table = new Table(m, n);
    }

    private boolean fullTable() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2 * m - 1; j++)
                if (table.gameTable[i][j] == '_')
                    return false;
        }
        return true;
    }

    private char checkEachRow(int a, int b, int k) {
        boolean win = false;
        char checker = table.gameTable[a][b];
        if (checker == '_')
            return 'N';
        while (!win && b + 2 * k - 1 <= 2 * m - 1) {
            win = true;
            checker = table.gameTable[a][b];
            if (checker == '_')
                return 'N';
            for (int j = b; j < b + 2 * k - 1; j += 2) {
                if (checker != table.gameTable[a][j])
                    win = false;

            }
            b += 2;
        }
        if (win)
            return checker;
        else
            return 'N';

    }

    private char checkEachColumn(int a, int b, int k) {
        boolean win = false;
        char checker = table.gameTable[a][b];
        if (checker == '_')
            return 'N';
        while (!win && a + k - 1 < n) {
            win = true;
            checker = table.gameTable[a][b];
            if (checker == '_')
                return 'N';
            for (int j = a; j < a + k; j++) {
                if (checker != table.gameTable[j][b])
                    win = false;

            }
            a += 1;
        }
        if (win)
            return checker;
        else
            return 'N';
    }

    private char checkEachDia(int a, int b, int k) {
        boolean win = true;
        char checker;
        int i = a, j = b, counter = 0;
        checker = table.gameTable[a][b];
        if (checker == '_')
            return 'N';
        if (a + k > n || b + 2 * k - 1 > 2 * m - 1)
            return 'N';
        while (counter < k && j < 2 * m - 1 && i < n) {
            if (checker != table.gameTable[i][j])
                win = false;
            j += 2;
            i++;
            counter++;
        }
        if (win)
            return checker;
        else
            return 'N';
    }

    private char checkEachDiaLeft(int a, int b, int k) {
        boolean win = true;
        char checker;
        int i = a, j = b, counter = 0;
        checker = table.gameTable[a][b];
        if (checker == '_')
            return 'N';
        if (a + k > n || b - 2 * k + 2 < 0)
            return 'N';
        while (counter < k && j >= 0 && i < n) {
            if (checker != table.gameTable[i][j])
                win = false;
            j -= 2;
            i++;
            counter++;
        }
        if (win)
            return checker;
        else
            return 'N';
    }

    private char checkRow() {
        int k = 3;
        char output = 'N';
        if (m > 3 && n > 3)
            k = 4;
        for (int i = 0; i < n; i++) {
            output = checkEachRow(i, 0, k);
            if (output != 'N')
                return output;
        }
        return output;
    }

    private char checkColumn() {
        int k = 3;
        char output = 'N';
        if (m > 3 && n > 3)
            k = 4;
        for (int i = 0; i < 2 * m - 1; i += 2) {
            output = checkEachColumn(0, i, k);
            if (output != 'N')
                return output;
        }
        return 'N';
    }

    private char checkDia() {
        int k = 3;
        char output = 'N';
        if (m > 3 && n > 3)
            k = 4;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2 * m - 1; j += 2) {
                output = checkEachDia(i, j, k);
                if (output != 'N')
                    return output;
                output = checkEachDiaLeft(i, j, k);
                if (output != 'N')
                    return output;
            }
        }
        return output;
    }

    boolean put(int x, int y) {
        if (turn == 1) {
            if (y < 1 || x < 1 || x > n || 2 * y - 1 > 2 * m - 1) {
                playerForOneGame[0].getServerFormatter().format("%s\n","Invalid coordination");
                playerForOneGame[0].getServerFormatter().flush();
                showTheGame();
                return false;
            }
            x -= 1;
            if (table.gameTable[x][2 * (y - 1)] != '_') {
                playerForOneGame[0].getServerFormatter().format("%s\n","Invalid coordination");
                playerForOneGame[0].getServerFormatter().flush();
                showTheGame();
                return false;
            }
            table.gameTable[x][2 * (y - 1)] = 'X';
            lastMoveOfFirst[0] = x;
            lastMoveOfFirst[1] = 2 * y - 1;
            this.turn = 2;

        } else {
            if (y < 1 || x < 1 || x > n || 2 * y - 1 > 2 * m - 1) {
                playerForOneGame[1].getServerFormatter().format("%s\n","Invalid coordination");
                playerForOneGame[1].getServerFormatter().flush();
                showTheGame();
                return false;
            }
            x -= 1;
            if (table.gameTable[x][2 * (y - 1)] != '_') {
                playerForOneGame[1].getServerFormatter().format("%s\n","Invalid coordination");
                playerForOneGame[1].getServerFormatter().flush();
                showTheGame();
                return false;
            }
            table.gameTable[x][2 * (y - 1)] = 'O';
            lastMoveOfSecond[0] = x;
            lastMoveOfSecond[1] = 2 * y - 1;
            this.turn = 1;
        }

        if (!checkTheWinner())
            showTheGame();
        else
            return true;
        return false;
    }

    private void printWinner(Player winner,Player loser){

        winner.getServerFormatter().format("%s\n","Player " + winner.name + " won");
        loser.getServerFormatter().format("%s\n","Player " + winner.name + " won");
        winner.getServerFormatter().flush();
        winner.getServerFormatter().flush();
    }

    private boolean checkTheWinner() {
        char output;
        output = checkRow();
        if (output != 'N') {
            if (output == 'X') {
                playerForOneGame[0].won++;
                playerForOneGame[1].loss++;
                printWinner(playerForOneGame[0],playerForOneGame[1]);

            }
            if (output == 'O') {
                playerForOneGame[1].won++;
                playerForOneGame[0].loss++;
                printWinner(playerForOneGame[1],playerForOneGame[0]);
            }

            return true;
        }
        output = checkColumn();
        if (output != 'N') {
            if (output == 'X') {
                playerForOneGame[0].won++;
                playerForOneGame[1].loss++;
                printWinner(playerForOneGame[0],playerForOneGame[1]);
            }
            if (output == 'O') {
                playerForOneGame[1].won++;
                playerForOneGame[0].loss++;
                printWinner(playerForOneGame[1],playerForOneGame[0]);
            }
            return true;
        }
        output = checkDia();
        if (output != 'N') {
            if (output == 'X') {
                playerForOneGame[0].won++;
                playerForOneGame[1].loss++;
                printWinner(playerForOneGame[0],playerForOneGame[1]);
            }
            if (output == 'O') {
                playerForOneGame[1].won++;
                playerForOneGame[0].loss++;
                printWinner(playerForOneGame[1],playerForOneGame[0]);
            }
            return true;
        }
        if (fullTable()) {
            playerForOneGame[1].draw++;
            playerForOneGame[0].draw++;
            playerForOneGame[0].getServerFormatter().format("%s\n","Draw");
            playerForOneGame[0].getServerFormatter().flush();
            playerForOneGame[1].getServerFormatter().format("%s\n","Draw");
            playerForOneGame[1].getServerFormatter().flush();
            return true;

        }
        return false;

    }

    public void showTheGame() {
        Gson gson = new Gson();
        String json = gson.toJson(table);
        playerForOneGame[0].getServerFormatter().format("%s\n",json);
        playerForOneGame[1].getServerFormatter().format("%s\n",json);
        playerForOneGame[0].getServerFormatter().flush();
        playerForOneGame[1].getServerFormatter().flush();

//        if (turn == 1) {
//            playerForOneGame[0].getServerFormatter().format("%s\n is your turn",playerForOneGame[0].name);
//            playerForOneGame[1].getServerFormatter().format("%s\n is your turn",playerForOneGame[0].name);
//        }
//        if (turn == 2) {
//            playerForOneGame[0].getServerFormatter().format("%s\n is your turn",playerForOneGame[1].name);
//            playerForOneGame[1].getServerFormatter().format("%s\n is your turn",playerForOneGame[1].name);
//        }

        playerForOneGame[0].getServerFormatter().flush();
        playerForOneGame[1].getServerFormatter().flush();
    }


    private boolean checkHasOneBead() {
        int counter = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2 * m - 1; j += 2) {
                if (table.gameTable[i][j] == '_') {
                    counter++;
                }
            }
        }
        return counter == m * n - 1;
    }

    void undo() {
        if ((turn == 1 && undoForPlayer2) || (turn == 2 && undoForPlayer1)) {//had undo
            formatter.format("%s\n","Invalid undo");
            formatter.flush();
            showTheGame();
            return;

        } else if (lastMoveOfFirst[0] == -1 || lastMoveOfSecond[0] == -1) {//with no movement
            formatter.format("%s\n","Invalid undo");
            formatter.flush();
            showTheGame();
            return;
        }
        if (turn == 1 && !undoForPlayer2) {//undo for player2
            if (checkHasOneBead()) {
                formatter.format("%s\n","Invalid undo");
                formatter.flush();
                showTheGame();
                return;
            }
            table.gameTable[lastMoveOfSecond[0]][lastMoveOfSecond[1] - 1] = '_';
            undoForPlayer2 = true;
            turn = 2;
        } else if (turn == 2 && !undoForPlayer1) {//undo for player1
            if (checkHasOneBead()) {
                formatter.format("%s\n","Invalid undo");
                formatter.flush();
                showTheGame();
                return;
            }
            table.gameTable[lastMoveOfFirst[0]][lastMoveOfFirst[1] - 1] = '_';
            undoForPlayer1 = true;
            turn = 1;
        }
        showTheGame();

    }

}
