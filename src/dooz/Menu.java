package dooz;

import sample.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;

import static sample.Server.games;
import static sample.Server.players;

public class Menu {
    Game game;
    private Formatter formatter;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Menu(Formatter formatter){
        this.formatter= formatter;
    }

    public static Player haveThePlayer(String playerName) {
        for (Player player : players) {
            if (player.name.equals(playerName))
                return player;
        }
        return null;
    }

    public void deleteGame() {
        games.remove(game);
    }

    public Game newGame(Player player1, Player player2) {
        game = new Game(player1, player2, player1.getM(),player1.getN() ,formatter);
        games.add(game);
        game.showTheGame();
        return game;
    }


    public void sortPlayers() {
        int length = players.size();
        for (int i = 0; i < length - 1; i++) {
            for (int j = 0; j < length - 1; j++) {
                if (players.get(j).won < players.get(j + 1).won)
                    Collections.swap(players, j, j + 1);
                else if (players.get(j).won == players.get(j + 1).won) {
                    if (players.get(j).loss > players.get(j + 1).loss)
                        Collections.swap(players, j, j + 1);
                    else if (players.get(j).loss == players.get(j + 1).loss) {
                        if (players.get(j).draw > players.get(j + 1).draw)
                            Collections.swap(players, j, j + 1);
                        else if (players.get(j).draw == players.get(j + 1).draw) {
                            if (players.get(j).name.compareTo(players.get(j + 1).name) > 0) {
                                Collections.swap(players, j + 1, j);
                            }
                        }

                    }

                }
            }
        }
    }

    public void scoreBoard() {
        sortPlayers();
        for (Player player : players) {
            formatter.format("%s\n",player.name + " " + player.won + " " + player.loss + " " + player.draw);
        }
        formatter.flush();

    }
    private ArrayList<Game> passYourGame(Player me){
        ArrayList<Game> myGames = new ArrayList<>();
        for(Game game : games){
            if(game.getPlayerForOneGame()[0].equals(me) || game.getPlayerForOneGame()[1].equals(me)){
                myGames.add(game);
            }
        }
        return myGames;
    }

    public void resume(Player me) {
        ArrayList<Game > myGame = passYourGame(me);
        int counter = 1;
        formatter.format("%s\n","Your paused games :");
        for (int i = myGame.size() - 1; i >= 0; i--) {
            formatter.format("%s\n",counter + ". " + myGame.get(i).getPlayerForOneGame()[0].name +
                    " " + myGame.get(i).getPlayerForOneGame()[1].name);
            counter++;
        }
        formatter.flush();

    }

    public boolean resumeNumber(int number) {
        if (number < 1 || number > games.size()) {
            formatter.format("%s\n","Invalid number");
            formatter.flush();
            return false;
        }
//        game = games.get(games.size() - number);
//        game.showTheGame();
//        games.remove(games.size() - number);
//        games.add(game);
        return true;


    }

    public Game presentGame() {
        return game;
    }

}
