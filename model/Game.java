package model;

import java.util.LinkedList;

public class Game {
    
    private LinkedList<GameState> gamestateStack;

    public Game() {
        this.gamestateStack = new LinkedList<>();
        gamestateStack.push(new WelcomeScreen());
    }

    public GameState getCurrentState() {return gamestateStack.getFirst();}

    public void goToWelcomeScreen() {
        while(gamestateStack.size()>1) {
            gamestateStack.pop();
        }
    }

    public void startNewChessGame() {
        if (!(gamestateStack.getLast() instanceof WelcomeScreen)) {
            goToWelcomeScreen();
        }
        gamestateStack.push(new ChessGame());
    }

    public void goToEndGame(Piece[][] board, boolean whiteWon) {
        try {
            if (!(gamestateStack.getLast() instanceof ChessGame)) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Can't go to end game as we are not in a game right now.");
        }
        gamestateStack.push(new EndGame(board, whiteWon));
    }
}
