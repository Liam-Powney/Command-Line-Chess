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
                    game.getLastState().setErrorMessage("Command not recognised. Enter 'help' for assistance.");
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
            Move m;
            try {
                m = moveParser(command);
            } catch (Exception e) {
                cg.setErrorMessage("Move invalid");
                return;
            }
            m = cg.findPiece(m);
            if (m.getPiece()==null) {
                cg.setErrorMessage("There are either no pieces or multiple pieces that can make that move");
                return;
            }
            if (cg.isMovePossible(m)) {
                cg.performMove(m);
                return;
            }
            cg.setErrorMessage("Move not possible.");
        }
    }

    // contact view to print the game to screen
    public void printGame() {
        GameState gs = game.getLastState();
        if (gs instanceof WelcomeScreen) {view.drawWelcomeScreen((WelcomeScreen)gs);}
        else if(gs instanceof ChessGame) {view.drawChessGame((ChessGame)gs);}
        else {System.out.println("No draw method for this gamestate :(");}
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

    // converts the last two chars in a string into square co-ordinates if they are valid inputs, returns {-1, -1} if invalid
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

    // converts a command into a Move if possible
    public Move moveParser(String c) {
        
        Move m;

        String pieceType="";
        int startCol=-1;
        int startRow=-1;
        int endCol;
        int endRow;
        boolean capture=false;;
        boolean check=false, checkmate=false;
        boolean pawnPromo=false;
        String promoPieceType="";

        // see if move wants to check or checkmate
        char ch0 = c.charAt(c.length()-1);
        if (ch0=='#') {
            checkmate=true;
            c = c.substring(0, c.length()-1);
        }
        else if (ch0=='+') {
            check=true;
            c = c.substring(0, c.length()-1);
        }

        // see if move is castling
        if (c.equals("0-0-0")) {return new Move(false, check, checkmate);}
        else if (c.equals("0-0")) {return new Move(true, check, checkmate);}

        // piece type?
        ch0 = c.charAt(0);
        if (ch0>='a' && ch0<='h') {
            pieceType = "pawn";
            startCol=columnToInt(ch0);
            // check if it's a pawn promo
            if (c.length()>3) {
                String temp = c.substring(c.length()-4);
                if ( temp.equals("8(Q)") || temp.equals("8(R)") || temp.equals("8(N)") || temp.equals("8(B)") ) {
                    pawnPromo=true;
                    if (temp.charAt(2) == 'Q') {promoPieceType = "queen";}
                    else if (temp.charAt(2) == 'R') {promoPieceType = "rook";}
                    else if (temp.charAt(2) == 'N') {promoPieceType = "knight";}
                    else if (temp.charAt(2) == 'B') {promoPieceType = "bishop";}
                    c = c.substring(0, c.length()-3);
                }
                
            }
        }
        else if (ch0 == 'K' ) {pieceType = "king";}
        else if (ch0 == 'Q' ) {pieceType = "queen";}
        else if (ch0 == 'R' ) {pieceType = "rook";}
        else if (ch0 == 'N' ) {pieceType = "knight";}
        else if (ch0 == 'B' ) {pieceType = "bishop";}
        else {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid piece type at command string index 0");
            }
        }
        // as long as the command isn't (pawn move + length=2)
        if (!( (pieceType.equals("pawn")) && (c.length()==2) )) {
            c = c.substring(1);
        }
        // extract end position co-ordinates from the string
        var temp=targetCoords(c);
        endCol=temp[0];
        endRow=temp[1];
        c = c.substring(0, c.length()-2);
        //is the move a capture?
        if (c.length()!=0 && c.charAt(c.length()-1)=='x') {
            capture=true;
            c = c.substring(0, c.length()-1);
        }
        //disambiguation info
        if (c.length()==2) {
            temp = targetCoords(c);
            startCol=temp[0];
            startRow=temp[1];
        }
        else if (c.length()==1) {
            ch0 = c.charAt(0);
            if (ch0>='a' && ch0<='h'){
                startCol = columnToInt(c.charAt(0));
            }
            else if (Character.getNumericValue(ch0)>=1 && Character.getNumericValue(ch0)<=8) {
                startRow = Character.getNumericValue(ch0)-1;
            }
            else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    System.out.println("Invalid command");
                }
            }
        }
        else if (c.length()!=0){
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid piece type at command string index 0");
            }
        }

        // return the move :)
        if (pieceType.equals("pawn")) {
            m = new Move(endCol, endRow, capture, check, checkmate, pawnPromo, promoPieceType);
            if (startCol!=-1) {m.setStartCol(startCol);}
            if (startRow!=-1) {m.setStartRow(startRow);}
            return m;
        }
        else {
            m = new Move(pieceType, endCol, endRow, capture, check, checkmate);
            if (startCol!=-1) {m.setStartCol(startCol);}
            if (startRow!=-1) {m.setStartRow(startRow);}
            return m;
        }
    }
}
