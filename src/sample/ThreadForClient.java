package sample;

import dooz.DoozMain;
import dooz.Game;
import dooz.Menu;
import dooz.Player;
import sample.Server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Formatter;

public class ThreadForClient extends Thread {
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public ThreadForClient(Socket socket, InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
        this.socket = socket;

    }

    private boolean canPlay(Menu menu, Player me) {
        Player[] players = menu.getGame().getPlayerForOneGame();
        if (menu.getGame().getTurn() == 1) {
            return me.equals(players[0]);
        } else
            return me.equals(players[1]);

    }

    public void run() {
        BufferedReader reader;
        String line;
        Formatter formatter;
        Player opponentPlayer;
        Player me;
        Game game;

        try {
            reader = new BufferedReader(new InputStreamReader(input));
            formatter = new Formatter(output);
            Menu menu = new Menu(formatter);
            //get username and make player
            do {
                line = reader.readLine();
                if(Menu.haveThePlayer((line))!=null){
                    formatter.format("%s\n","no");
                    formatter.flush();
                    line = null;
                }
            } while (line == null);
            formatter.format("%s accepted\n", line);
            formatter.flush();
            me = Player.makeNewPlayer(line);
            me.setServerFormatter(formatter);
            Server.players.add(me);
            me.setMenu(menu);

            while (!socket.isClosed()) {
                line = reader.readLine();
                if (line != null) {

                    if (me.getState().equals("game") && !line.equals("undo")) {
                        if (!canPlay(menu, me)) {
                            //formatter.format("%s\n", "is not your turn");
                            //formatter.flush();
                            continue;
                        }
                    }

                    me.setState(DoozMain.main(line, me.getState(), formatter, me, menu));
                    if (me.getState().equals("end")) {
                        formatter.format("%s\n", "end");
                        formatter.flush();
                        input.close();
                        output.close();
                        socket.close();
                    }
                    else if (me.getState().equals("resumeChose")) {
                        ArrayList<Game> games = Server.games;
                        int number = Integer.parseInt(line);
                        game = games.get(games.size() - number);

                        if (!game.getPlayerForOneGame()[0].getIsPlaying() && !game.getPlayerForOneGame()[1].getIsPlaying()) {

                            for (int i = 0; i < 2; i++) {
                                game.getPlayerForOneGame()[i].setState("game");
                                game.getPlayerForOneGame()[i].getServerFormatter().format("%s\n", "start a game");

                                System.out.println("start a game");

                                game.getPlayerForOneGame()[i].getServerFormatter().flush();
                                game.getPlayerForOneGame()[i].getMenu().setGame(game);
                                game.getPlayerForOneGame()[i].setPlaying(true);
                            }
                            game.showTheGame();
                            games.remove(games.size() - number);
                            games.add(game);
                        }
                        else{
                            formatter.format("%s\n","can not resume the game");
                            formatter.flush();
                            me.setState("resume");
                        }


                    }
                    else if (line.contains("pause") || line.contains("stop") || me.getState().equals("winning")) {
                        for (int i = 0; i < 2; i++) {
                            menu.getGame().getPlayerForOneGame()[i].getServerFormatter().format("%s\n", "end game");
                            menu.getGame().getPlayerForOneGame()[i].getServerFormatter().flush();
                            menu.getGame().getPlayerForOneGame()[i].setPlaying(false);
                            menu.getGame().getPlayerForOneGame()[i].setState("menu");
                        }
                    }
                    else if (line.matches("new game (\\w+)")) {
                        Player[] players = new Player[2];
                        opponentPlayer = Menu.haveThePlayer(line.split(" ")[2]);
                        if (opponentPlayer != null && !opponentPlayer.getIsPlaying()) {
                            players[0] = me;
                            players[1] = opponentPlayer;
                            for (int i = 0; i < 2; i++) {
                                players[i].getServerFormatter().format("%s\n", "start a game");
                                players[i].getServerFormatter().flush();
                                players[i].setPlaying(true);
                            }
                            opponentPlayer.setState("game");
                            me.setState("game");
                            game = menu.newGame(me, opponentPlayer);
                            players[0].getMenu().setGame(game);
                            players[1].getMenu().setGame(game);// todo make the game null
                            menu.getGame().setPlayerForOneGame(players);
                        } else {
                            formatter.format("%s\n", "can not play");
                            formatter.flush();
                            me.setState("menu");
                        }
                    }
                }
            }


        } catch (Exception e) {
        }
    }
}