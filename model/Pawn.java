package model;

public class Pawn extends Piece{
    
    private final char pieceChar = ' ';
    private final int[][] moves = { };
    private boolean hasMoved;
    
    public Pawn(boolean white) {
        super(white);
        this.hasMoved = false;
    }

    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved() {
        hasMoved = true;
    }
}
