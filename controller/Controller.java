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

        // update model based on command and current game state
        // create var for the current state (cs)
        GameState cs = game.getCurrentState();
        cs.clearErrorMessage();
        if (cs instanceof WelcomeScreen) {
            switch (command) {
                case "1":
                    System.out.print("\nStarting new game...\n\n");
                    game.startNewChessGame();
                    break;
                case "2":
                    System.out.println("Not implemented yet, please choose something else :)");
                    break;
                case "help":
                    System.out.println("When in a game, use command 'quit' to return to the main menu :)");
                    break;
                default:
                    game.getCurrentState().setErrorMessage("Command not recognised. Enter 'help' for assistance.");
                    break;
            }
        }
 
        else if (cs instanceof ChessGame) {
            ChessGame cg = (ChessGame) cs;
            // parse command
            if (command.equalsIgnoreCase("quit")) {
                System.out.println("Quitting game and taking you to the main menu...");
                game.goToWelcomeScreen();
                return;
            }
            try {
                cg.attemptMove(command);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        else if (cs instanceof EndGame) {
            //EndGame eg=(EndGame)cs;
            switch (command) {
                // start new game
                case "1":
                    game.startNewChessGame();
                    break;
                    // go to welcome screen
                case "2":
                    game.goToWelcomeScreen();
                    break;
                case "help":
                    System.out.println("Select '2' to return to the main menu :)");
                    break;
                default:
                    game.getCurrentState().setErrorMessage("Command not recognised. Enter 'help' for assistance.");
                    break;
            }
        }
    }

    public void drawGame() {
        view.draw(game.getCurrentState());
    }
}
