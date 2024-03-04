package model;

import java.util.Stack;

public class Game {
    
    private Stack<GameState> gamestateStack;

    public Game() {
        this.gamestateStack = new Stack<>();
        gamestateStack.push(new WelcomeScreen(this));
    }

    public GameState getCurrentState() {return gamestateStack.peek();}

    public void startNewChessGame() {gamestateStack.push(new ChessGame());}
    public void startNewChessGame(String in) {gamestateStack.push(new ChessGame(in));}
    public void goToWelcomeScreen() {while (gamestateStack.size()>1) {gamestateStack.pop();}}
    public void goBack() {gamestateStack.pop();}

}
