package controller;
import model.*;
import view.*;
import java.util.ArrayList;

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

        //
        // THIS BIT PARSES THE PGN COMANDS! Very important!!
        // 
        if (cs instanceof ChessGame) {
            var cg = (ChessGame) cs;

            // parse PGN command

            /*

            // details of the move as to be discerned from the PGN notation
            Piece pieceType;
            boolean isCapture = command.contains("x");
            int xdest, ydest;
            boolean disambig1, disambig2, check, checkmate;

            //
            // figuring out the move!!
            //

            //which piece is being moved?
            // is a pawn being moved?
            char ch1 = command.charAt(0);
            if (Character.isLowerCase(ch1)) {
                // it's a pawn, what else??
            }
            // it's another piece type
            else {
                switch (ch1) {
                    case 'K':
                        pieceType = new King(cg.getWhitesTurn());
                        break;
                
                    default:
                        // no piece type found - invalid move
                        System.out.println("Please input a valid move");
                        return;
                }
            }
            // remove piece type info from command
            command = command.substring(1);

            // is the move a check or checkmate?
            if (command.charAt(command.length()-1) == '+') {
                if (command.charAt(command.length()-2) == '+') {
                    check = true;
                    checkmate = true;
                }
                else {
                    checkmate = false;
                    check = true;
                }
            }
            else {
                check = false;
                checkmate = false;
            }

            // remove the '+' characters from the command if they are present
            if (checkmate) {
                command = command.substring(0, command.length()-3);
            }
            else if (check) {
                command = command.substring(0, command.length()-2);
            }

            */

            
        }
    }

    // contact view to print the game to screen
    public void printGame() {
        var gs = game.getLastState();
        view.print(gs);
    }


}
