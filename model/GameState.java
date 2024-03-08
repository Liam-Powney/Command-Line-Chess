package model;

public abstract class GameState {

    private boolean showHelp;
    private String playerMessage;

    // super constructor
    public GameState(){
        this.showHelp=false;
        this.playerMessage=null;
    }

    public boolean getShowHelp() {return showHelp;}
    public void setShowHelp(boolean b) {showHelp=b;}
    public String getPlayerMessage() {return playerMessage;}
    public void setPlayerMessage(String s) {playerMessage=s;}
}
