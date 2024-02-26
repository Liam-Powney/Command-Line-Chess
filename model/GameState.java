package model;

public abstract class GameState {

    protected String errorMessage;

    // super constructor
    public GameState() {
        this.errorMessage="";
    }

    public String getErrorMessage() {return errorMessage;}
    public void setErrorMessage(String s) {errorMessage=s;}
    public void clearErrorMessage() {errorMessage="";}


    
    
}
