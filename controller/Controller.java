package controller;
import model.*;
import view.*;

public class Controller {

    private final Game game;
    private final View view;

    public Controller(Game game, View view) {
        this.game = game;
        this.view = view;
    }

    public void executeCmd(String command) {

        //universal commands
        if (command.equals("back")) {game.goBack(); return;}
        if (command.equals("help")) {game.getCurrentState().setShowHelp(true); return;}

        
        GameState cs = game.getCurrentState();
        cs.setPlayerMessage(null);
        if (cs instanceof WelcomeScreen) {

            // welcome screen command parser
            WelcomeScreen ws = (WelcomeScreen)cs;
            if (!ws.getReceivingString()) {
                switch (command) {
                    case "1":
                        game.startNewChessGame();
                        break;
                    case "2":
                        ws.setReceiveingString(true);
                        ws.setPlayerMessage("Please enter your PGN string:");
                        break;
                    default:
                        ws.setPlayerMessage("Please enter a valid command");
                        break;
                }
            }
            else {
                try {
                    game.startNewChessGame(command);
                } catch (Exception e) {
                    ws.setPlayerMessage("Not a valid pgn or fen string");
                } finally {
                    ws.setReceiveingString(false);
                }
            }
        }
        
        // chessgame command parser
        else if (cs instanceof ChessGame) {
            ChessGame cg = (ChessGame) cs;
            if (cg.getResult()==null) {
                switch (command) {
                    case "undo":
                        cg.undo();
                        break;
                    case "redo":
                        cg.redo();
                        break;
                    default:
                    try {
                        cg.attemptMove(command);
                    } catch (Exception e) {
                        cg.setPlayerMessage(e.getMessage());
                    }
                        break;
                }
            }
            else {
                if (cg.getResult().equals("white")) {

                }
                else if (cg.getResult().equals("black")) {

                }
                else if (cg.getResult().equals("draw")) {

                }
                switch (command) {
                    case "1":
                        game.goToWelcomeScreen();
                        break;
                    case "2":
                        game.startNewChessGame();
                        break;
                    default:
                        break;
                }

            }
        }
    }

    public void drawGame() {
        view.draw(game.getCurrentState());
    }
}
