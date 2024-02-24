package model;


public abstract class Piece {
    
    protected boolean white;
    protected boolean hasMoved;

    // Constructor
    public Piece(boolean white){
        this.white = white;
        this.hasMoved = false;
    }

    public abstract char getPieceChar();
    public abstract int[][][] getMoves();

    public boolean getWhite() {
        return white;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }
    public void setMoved() {
        hasMoved = true;
    }


}
