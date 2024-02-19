package view;
import model.*;

public class CommandLineView {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_INVERT = "\u001B[7m";

    public CommandLineView() {}

    public void print(GameState s) {
        if (s instanceof WelcomeScreen) {
            System.out.println("Welcome to command line chess :) Please choose from the following options:\n1 - New Game\n2 - Input Game History\n\nFor more information, please type 'help'\n");
        }
        else if (s instanceof ChessGame) {
            String outString = "";
            for ( int i=7; i>=0; i--) {
                outString+= i+1 + "  ";
                for(int j=0; j<=7; j++) {
                    Piece p = ((ChessGame) s).getBoard()[i][j];
                    if (p == null) {
                        outString+="[ ]";
                    }
                    else {
                        if (!p.getWhite()) {
                            outString+="[" + p.getPieceChar() + "]";
                        }
                        else {
                            outString+="[" + ANSI_INVERT + p.getPieceChar() + ANSI_RESET + "]";
                        }
                    }
                }
                if (i != 0) {
                    outString+="\n";
                }
            }
            outString+="\n    a  b  c  d  e  f  g  h";
            System.out.println(outString);
        } 
    }
}
    