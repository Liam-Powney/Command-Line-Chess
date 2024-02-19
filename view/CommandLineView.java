package view;
import model.*;

public class CommandLineView {

    public CommandLineView() {}

    /*
    public void print(WelcomeScreen ws) {
        System.out.println("Welcome to command line chess :) Please choose from the following options:\n1 - New Game\n2 - Input Game History\n\nFor more information, please type 'help'\n");
    }

    public void print(ChessGame cg) {

    }
    */

    public void print(GameState s) {
        if (s instanceof WelcomeScreen) {
            System.out.println("Welcome to command line chess :) Please choose from the following options:\n1 - New Game\n2 - Input Game History\n\nFor more information, please type 'help'\n");
        }
        else if (s instanceof ChessGame) {
            String outString = "";
            for ( int i=7; i>=0; i--) {
                for(int j=0; j<=7; j++) {
                    if (((ChessGame) s).getBoard()[i][j] == null) {
                        outString+="[ ]";
                    }
                    else {
                        outString+="[" + ((ChessGame) s).getBoard()[i][j].getPieceString() + "]";
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
    