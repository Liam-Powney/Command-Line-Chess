package model;

import java.util.Stack;

public class Game {
    
    private Stack<GameState> gamestateStack;

    public Game() {
        this.gamestateStack = new Stack<>();
        gamestateStack.push(new WelcomeScreen(this));
    }

    public GameState getCurrentState() {return gamestateStack.peek();}

    public void startNewChessGame() {
        goToWelcomeScreen();
        gamestateStack.push(new ChessGame());
    }
    public void startNewChessGame(String in) {
        goToWelcomeScreen();
        gamestateStack.push(new ChessGame(in));
    }
    public void goToWelcomeScreen() {
        while (gamestateStack.size()>1) {gamestateStack.pop();}
        if (!(gamestateStack.peek() instanceof WelcomeScreen)) {throw new Error("The first screen should always be a welcome screen. What the hell did you do?!");}
    }
    public void goBack() {gamestateStack.pop();}

}
