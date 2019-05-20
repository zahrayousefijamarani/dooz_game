package dooz;


import java.net.Socket;
import java.text.Format;
import java.util.Formatter;

public class Player {
    String name;
    int won = 0;
    int loss = 0;
    int draw = 0;
    private boolean isPlaying = false;
   private Formatter serverFormatter;
   private String state = "menu";
   int n=3,m=3;
   private  Menu menu;

//    public void setTable(Table table) {
//        this.table = table;
//    }
//
//    public Table getTable() {
//        return table;
//    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
    public void setTable(int m, int n) {
        this.n = n;
        this.m = m;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public void setServerFormatter(Formatter serverFormatter) {
        this.serverFormatter = serverFormatter;
    }

    public Formatter getServerFormatter() {
        return serverFormatter;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean getIsPlaying(){
        return isPlaying;
    }

    public static Player makeNewPlayer(String name){
        return new Player(name);
    }
    private Player(String name){
        this.name= name;
    }


}
