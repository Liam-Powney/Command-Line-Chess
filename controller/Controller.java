package controller;
import model.*;
import view.*;

public class Controller {

    private final Game game;
    private final CommandLineView view;

    public Controller(Game game, CommandLineView view) {
        this.game = game;
        this.view = view;
    }


    public void executeCmd(String command) {

        // update model based on command and current game state

        // create var for the current state (cs)
        var cs = game.getLastState();
        if (cs instanceof WelcomeScreen) {
            switch (command) {
                case "1":
                    System.out.print("\nStarting new game...\n\n");
                    game.pushChessGame();
                    break;
                case "2":

                    break;
                case "help":

                    break;
                default:
                    System.out.println("Command not recognised. Enter 'help' for assistance.");
                    break;
            }
        }
 
        if (cs instanceof ChessGame) {
            var cg = (ChessGame) cs;

            if (cg.attemptMove(command)) {
                System.out.println("That was a valid instruction");
            }
            else {
                System.out.println("Invalid move. Please make a valid move.");
            }
            

            
        }
    }

    // contact view to print the game to screen
    public void printGame() {
        var gs = game.getLastState();
        view.print(gs);
    }


}
