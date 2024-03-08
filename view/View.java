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
        clearScreen();
        if (gs instanceof WelcomeScreen) {drawWelcomeScreen((WelcomeScreen)gs);}
        else if (gs instanceof ChessGame) {drawChessGame((ChessGame)gs);}
        if (gs.getPlayerMessage()!=null) {System.out.println(gs.getPlayerMessage());}
    }

    public void drawWelcomeScreen(WelcomeScreen w) {
        String outString = "";
        if (!w.getReceivingString()) {
            outString+="Welcome to command line chess :) Please choose from the following options:\n" +
                                "1 - New Game\n" +
                                "2 - Input Game History\n" +
                                "exit - Exit Game\n\n";
            if (!w.getShowHelp()) {outString+="For more information, please type 'help'\n";}
            else {
                outString+="\nWelcome to Command Line Chess by Liam :)\n\n"+
                        "You can start a new chess game by pressing "+ 
                        "1, or input a valid PGN or FEN string by "+ 
                        "entering 2. For more help in game, start a "+
                        "game and then enter the help command again.";
                w.setShowHelp(false);
            }
        }
        else {
            outString+="Please enter your pgn string:";
        }
        if (w.getPlayerMessage()!=null) {
            outString+="\n\n" + w.getPlayerMessage();
            w.setPlayerMessage(null);
        }
        System.out.println(outString);
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
        outString+="\n\n";
        if (cg.getResult()==null) {
            if (cg.getCBS().getWhitesTurn()) {outString+="White";}
            else {outString+="Black";}
            outString+=" to move";
            if (cg.getShowHelp()) {
                outString+="\n\nPlease enter your moves using standard PGN notation excluding '!' and '?' characters";
                outString+="\n\nOther options include:\nundo - undoes the last move\nredo - redoes the last undone move\nback - go back to the main menu\nquit - quit game";
                cg.setShowHelp(false);
            }
        }
        else {
            if (cg.getResult().equals("draw")) {
                outString+="It's a draw!";
            }
            else {
                if (cg.getResult().equals("white")) {outString+="White";}
                else if(cg.getResult().equals("black")) {outString+="Black";}
                outString+=" wins!";
            }
            outString+="\n\nWhat would you like to do?\n\n1 - Go to Main Menu\n2 - Start new game\nexit - close program";
            if (cg.getShowHelp()) {
                outString+="\n\nPlease choose from the options above :)";
                cg.setShowHelp(false);
            }
        }
        System.out.println(outString);
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
    