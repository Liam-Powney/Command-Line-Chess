package model;

public class King extends Piece{
    
    private final char pieceChar = 'K';
    private final static int[][][] moves = {{{0, 1}}, {{1, 1}}, {{1, 0}}, {{-1, 0}}, {{0, -1}}, {{-1, -1}}, {{-1, 1}}, {{1, -1}}};
    private boolean hasMoved;

    public King(boolean white) {
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

    @Override
    public int[][][] getMoves() {
        return moves;
    }
    
}
