package dooz;

import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoozMain {

    public static String main(String lineOfOrder, String state, Formatter formatter,Player me,Menu menuOfTheGame) {
        int x, y, m, n;
        Pattern newGamePattern, setTablePattern, putPattern;
        Matcher newGameMatcher, setTableMatcher, putMatcher;


        newGamePattern = Pattern.compile("new game (\\w+)");
        newGameMatcher = newGamePattern.matcher(lineOfOrder);
        setTablePattern = Pattern.compile("set table( (\\d+)\\*(\\d+)\\b( *\\w+)*)*");
        setTableMatcher = setTablePattern.matcher(lineOfOrder);
        putPattern = Pattern.compile("put\\((\\d+),(\\d+)\\)( *\\w+)*");
        putMatcher = putPattern.matcher(lineOfOrder);


        switch (state) {
            case "scoreBoard":{
                if (lineOfOrder.equals("back"))
                    state = "menu";
                else if (lineOfOrder.equals("quit")) {
                    return "end";
                }
//                else {
//                    formatter.format("%s\n", "Invalid command");
//                    formatter.flush();
//                }
                break;
            }
            case "resume":
                if (lineOfOrder.trim().equals("back"))
                    state = "menu";
                else if (lineOfOrder.trim().matches("\\d+")) {
                    if (menuOfTheGame.resumeNumber(Integer.parseInt(lineOfOrder.trim())))
                        state = "resumeChose";
                }
                break;
            case "menu":
                if (newGameMatcher.find()) {
                   }
                else if (lineOfOrder.equals("resume")) {
                    menuOfTheGame.resume(me);
                    state = "resume";
                } else if (lineOfOrder.equals("scoreboard")) {
                    menuOfTheGame.scoreBoard();
                    state = "scoreBoard";
                } else if (lineOfOrder.trim().equals("quit"))
                    return "end";
                else if (setTableMatcher.find()) {
                    if (setTableMatcher.group(4) != null) {
                        formatter.format("%s\n","Invalid command");
                        formatter.flush();
                        return state;
                    }
                    if (setTableMatcher.group(1) == null) {
                        n = m = 3;
                        me.setTable(m, n);
                        return state;
                    }
                    n = Integer.parseInt(setTableMatcher.group(2));
                    m = Integer.parseInt(setTableMatcher.group(3));
                    me.setTable(m, n);
                }

                break;
            case "game":
                if (putMatcher.find() ) {
                    if (putMatcher.group(3) != null) {
                       // formatter.format("%s\n","Invalid command for put");
                        formatter.flush();
                        menuOfTheGame.presentGame().showTheGame();
                        return state;
                    }
                    x = Integer.parseInt(putMatcher.group(1));
                    y = Integer.parseInt(putMatcher.group(2));
                    if (menuOfTheGame.presentGame().put(x, y)) {
                        state = "winning";
                        menuOfTheGame.deleteGame();
                    }
                } else if (lineOfOrder.trim().equals("undo")) {
                    menuOfTheGame.presentGame().undo();
                } else if (lineOfOrder.trim().equals("pause")) {
                    state = "menu";
                } else if (lineOfOrder.trim().equals("stop")) {
                    menuOfTheGame.deleteGame();
                    state = "menu";
                }
                break;
//                default:
//                    formatter.format("%s\n","Invalid command for whole of game");
//                    formatter.flush();
//                    break;

        }

        return state;
    }
}

