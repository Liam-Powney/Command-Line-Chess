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
                    System.out.println("Not implemented yet, please choose something else :)");
                    break;
                case "help":
                    System.out.println("When in a game, use command 'quit' to return to the main menu :)");
                    break;
                default:
                    System.out.println("Command not recognised. Enter 'help' for assistance.");
                    break;
            }
        }
 
        if (cs instanceof ChessGame) {
            var cg = (ChessGame) cs;
            // parse command
            if (command.equalsIgnoreCase("quit")) {
                System.out.println("Quitting game and tkaing you to the main menu...");
                game.popGamestateStack();
                return;
            }
            PGNChessMove move = pgnParser(command);
            //move.printMoveInfo();
            if (move.isValidMove()) {
                cg.attemptMove(move);
            }
            else {
                System.out.println("Move invalid");
            }
        }
    }

    // contact view to print the game to screen
    public void printGame() {
        var gs = game.getLastState();
        view.print(gs);
    }




    //
    // FUNCTIONS THAT PARSE PGN NOTATION
    //
    // returns a corresponding int value for a char row value of a-h (0-7)
    public int columnToInt(char ch) {
        if (ch < 'a' || ch > 'h') {
            throw new IllegalArgumentException("Character must be in the range 'a' to 'h'");
        }
        return ch - 'a';
    }

    // extract and validate target coords from a PGN move, returns {-1, -1} if invalid
    public int[] targetCoords(String c) {
        try {
            char ch1 = c.charAt(c.length()-2), ch2 = c.charAt(c.length()-1);
            // are the destination values valid?
            if ( ((ch1>='a') && ( ch1<='h') ) && ((ch2>='1') && ( ch2<='8')) ) {
                int x = columnToInt(ch1);
                int y = Character.getNumericValue(ch2)-1;
                return new int[]{x, y};
            }
            else { throw new IllegalArgumentException("Last two characters of string are not valid board coordinates"); }
        } catch (Exception e) {
            throw new IllegalArgumentException("Not enough chars in the string to extract info");
        }
    }
    // PGN instruction parser - decodes a PGN command from user and returns an object with all decoded information including whether the instruction is valid
    public PGNChessMove pgnParser(String c) {
        try {
            // is it an attempted check or checkmate?
            boolean check = false;
            boolean checkmate = false;
            if (c.charAt(c.length()-1)=='#') {
                checkmate = true;
                c = c.substring(0, c.length()-1);
            }
            else if (c.charAt(c.length()-1)=='+') {
                check = true;
                c = c.substring(0, c.length()-1);
            }

            // SPECIAL CASES
            // castling short
            if (c.equals("0-0-0")) {
                return new PGNChessMove(false, check, checkmate);
            }
            else if (c.equals("0-0")) {
                return new PGNChessMove(true, check, checkmate);
            }

            // move info vars
            char ch0 = c.charAt(0);
            char movingPieceType;
            char promoPiece = ' ';
            boolean cap = false;
            int[] startPos = new int[]{-1, -1};
            int[] newPos = new int[]{-1,-1};

            // what is the piece type?
            // pawn
            if (ch0>='a' && ch0<='h') {
                movingPieceType = 'p';
                startPos[0] = columnToInt(ch0);
                try {
                    var temp = c.substring(c.length()-4);
                    if ( temp.equals("8(Q)") || temp.equals("8(R)") || temp.equals("8(N)") || temp.equals("8(B)") ) {
                        promoPiece = temp.charAt(2);
                        c = c.substring(0, c.length()-3);
                    }
                    else { promoPiece = ' ';}
                } catch (Exception e) {
                    promoPiece = ' ';
                }
            }
            // non pawn
            else if (ch0 == 'K' || ch0 == 'Q' || ch0 == 'R' || ch0 == 'N' || ch0 == 'B') {
                movingPieceType = ch0;
                c = c.substring(1);
            }
            // invalid character
            else {
                return new PGNChessMove();
            }
            // newPos
            newPos = targetCoords(c);
            c = c.substring(0, c.length()-2);
            // any more info?
            if (c.length() == 0) {
                return new PGNChessMove(false, movingPieceType, promoPiece, cap, startPos, newPos, check, checkmate);
            }
            // capture?
            if (c.charAt(c.length()-1)=='x') {
                cap = true;
                c = c.substring(0, c.length()-1);
            }
            // any more info?
            if (c.length()==0) {
                return new PGNChessMove(false, movingPieceType, promoPiece, cap, startPos, newPos, check, checkmate);
            }
            //disambig co-ords? (for non-pawn/king)
            if (movingPieceType == 'K') {
                return new PGNChessMove();
            }
            else {
                // 2 disambigs
                if (c.length() == 2) {
                    System.out.println("c is now " + c);
                    startPos = targetCoords(c);
                }
                // 1 disambig
                else if (c.length() == 1) {
                    ch0 = c.charAt(0);
                    int temp = Character.getNumericValue(ch0)-1;
                    if (ch0>='a' && ch0<='h'){
                        startPos[0] = columnToInt(c.charAt(0));
                    }
                    else if ( temp>=0 && temp<=7 ) {
                        startPos[1] = temp;
                    }
                    else {
                        return new PGNChessMove();
                    }
                }
                // anything else is invalid
                else {
                    return new PGNChessMove();
                }
            }
            // return all the info :)
            return new PGNChessMove(false, movingPieceType, promoPiece, cap, startPos, newPos, check, checkmate);
        }
        // any error indicates an invalid command 
        catch (Exception e) {
            return new PGNChessMove();
        }
    }

}
