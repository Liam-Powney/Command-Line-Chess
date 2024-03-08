package model;

public abstract class GameState {

    private boolean showHelp;

    // super constructor
    public GameState(){
        this.showHelp=false;
    }

    public boolean getShowHelp() {return showHelp;}
    public void setShowHelp(boolean b) {showHelp=b;}
}
