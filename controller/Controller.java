package controller;

import model.Game;
import model.WelcomeScreen;
import view.CommandLineView;

public class Controller {

    private final Game game;
    private final CommandLineView view;

    public Controller(Game game, CommandLineView view) {
        this.game = game;
        this.view = view;
    }


    public void executeCmd(String command) {

        // update model based on command
        if (game.getLastState() instanceof WelcomeScreen) {
            switch (command) {
                case "1":
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
    }

    // contact view to print the game to screen
    public void printGame() {
        var gs = game.getLastState();
        view.print(gs);
    }


}
