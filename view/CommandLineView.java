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
                for(int j=0; j<=7; j++) {
                    Piece p = ((ChessGame) s).getBoard()[i][j];
                    if (p == null) {
                        outString+="[ ]";
                    }
                    else {
                        if (!p.getWhite()) {
                            outString+="[" + p.getPieceString() + "]";
                        }
                        else {
                            outString+="[" + ANSI_INVERT + p.getPieceString() + ANSI_RESET + "]";
                        }
                    }
                }
                if (i != 0) {
                    outString+="\n";
                }
            }
            System.out.println(outString);
        } 
    }
}
    