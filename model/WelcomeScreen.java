package model;

public class WelcomeScreen extends GameState{

    private boolean receivingString;

    public WelcomeScreen(Game game) {
        receivingString=false;
    }

    public boolean getReceivingString() {return receivingString;}
    public void setReceiveingString(boolean b) {receivingString=b;}
}
