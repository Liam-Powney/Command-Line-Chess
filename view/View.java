package view;
import java.util.HashMap;

import model.*;

public class View {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_INVERT = "\u001B[7m";

    private HashMap<String, Character> spriteMap = new HashMap<>();

    public View() {
        spriteMap.put("rook", 'R');
        spriteMap.put("knight", 'N');
        spriteMap.put("bishop", 'B');
        spriteMap.put("king", 'K');
        spriteMap.put("queen", 'Q');
        spriteMap.put("pawn", 'p');
    }

    public void draw(GameState gs) {
        //clearScreen();
        if (gs instanceof WelcomeScreen) {drawWelcomeScreen((WelcomeScreen)gs);}
        else if (gs instanceof ChessGame) {drawChessGame((ChessGame)gs);}
    }

    public void drawWelcomeScreen(WelcomeScreen w) {
        if (!w.getReceivingString()) {
            System.out.println("Welcome to command line chess :) Please choose from the following options:\n" +
                                "1 - New Game\n" +
                                "2 - Input Game History\n" +
                                "\n" +
                                "For more information, please type 'help'\n");
        }
        else {
            System.out.println("Please enter your pgn string:");
        }
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
        outString+="\n\n White's turn: " + cg.getCBS().getWhitesTurn() + ", WCastleRights: " + cg.getCBS().getWCastleS() + " " + cg.getCBS().getWCastleL() + ", BCastleRights: " + cg.getCBS().getBCastleS() + " " + cg.getCBS().getBCastleL() + ", " + cg.getCBS().getHalfMove() + " " + cg.getCBS().getFullMove();

        System.out.println(outString);
    }

    public void drawEndGame(EndGame eg) {
        String out="";
        if (eg.getWhiteWon()) {out+="White";}
        else {out+="Black";}
        out+=" won! \n\n What would you like to do?\n\n 1 - Play again\n2 - Exit\n";
        System.out.println(out);
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
    