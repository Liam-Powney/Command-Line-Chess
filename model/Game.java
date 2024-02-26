package model;

import java.util.LinkedList;

public class Game {
    
    private  LinkedList<GameState> gamestateStack;

    public Game() {
        this.gamestateStack = new LinkedList<>();
        gamestateStack.add(new WelcomeScreen());
    }

    public GameState getLastState() {return gamestateStack.getLast();}
    public void pushGameState(GameState gs) {gamestateStack.push(gs);}
    public void popGamestateStack() {gamestateStack.pop();}
}
