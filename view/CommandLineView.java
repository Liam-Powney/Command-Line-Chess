package view;
import java.util.HashMap;

import model.*;

public class CommandLineView {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_INVERT = "\u001B[7m";

    private HashMap<String, Character> spriteMap = new HashMap<>();

    public CommandLineView() {
        spriteMap.put("rook", 'R');
        spriteMap.put("knight", 'N');
        spriteMap.put("bishop", 'B');
        spriteMap.put("king", 'K');
        spriteMap.put("queen", 'Q');
        spriteMap.put("pawn", 'p');
    }

    public void draw(GameState gs) {
        clearScreen();
        if (gs instanceof WelcomeScreen) {drawWelcomeScreen((WelcomeScreen)gs);}
        else if (gs instanceof ChessGame) {drawChessGame((ChessGame)gs);}
    }

    public void drawWelcomeScreen(WelcomeScreen w) {
        System.out.println("Welcome to command line chess :) Please choose from the following options:\n" +
                                "1 - New Game\n" +
                                "2 - Input Game History\n" +
                                "\n" +
                                "For more information, please type 'help'\n");
        if (w.getErrorMessage().length()>0) {System.out.println(w.getErrorMessage());}
    }

    public void drawChessGame(ChessGame cg) {
        String outString = "";
        for ( int i=7; i>=0; i--) {
            outString+= i+1 + "  ";
            for(int j=0; j<=7; j++) {
                Piece p = cg.getBoard()[i][j];
                if (p == null) {
                    outString+="[ ]";
                }
                else {

                    if (!p.getWhite()) {
                        outString+="[" + spriteMap.get(p.getType()) + "]";
                    }
                    else {
                        outString+="[" + ANSI_INVERT + spriteMap.get(p.getType()) + ANSI_RESET + "]";
                    }
                }
            }
            if (i != 0) {
                outString+="\n";
            }
        }
        outString+="\n    a  b  c  d  e  f  g  h";

        System.out.println(outString);
        if (cg.getErrorMessage().length()>0) {System.out.println(cg.getErrorMessage());}
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
    