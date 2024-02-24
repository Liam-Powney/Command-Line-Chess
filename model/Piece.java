package model;


public abstract class Piece {
    
    protected boolean white;

    // Constructor
    public Piece(boolean white){
        this.white = white;
    }

    public abstract char getPieceChar();
    public abstract int[][][] getMoves();

    public boolean getWhite() {
        return white;
    }


}
