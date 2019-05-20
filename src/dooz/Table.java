package dooz;
public class Table {
    public int m;
    public int n;
    public char[][] gameTable;

    public Table(int m, int n) {
        this.m = m;
        this.n = n;
        this.gameTable = new char[n][2 * m - 1];
        drawTable(m, n);
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public void drawTable(int m, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2 * m - 1; j += 2) {
                this.gameTable[i][j] = '_';
                if (j != 2 * m - 2)
                    this.gameTable[i][j + 1] = '|';
            }
        }
    }

}

