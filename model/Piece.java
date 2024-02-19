package model;


public abstract class Piece {
    
    protected boolean white;

    // Constructor
    public Piece(boolean white){
        this.white = white;
    }

    public abstract String getPieceString();

    public boolean getWhite() {
        return white;
    }


}
