package model;

public class King extends Piece{
    
    private final char pieceChar = 'K';
    private final int[][][] moves = {{{0, 1}}, {{1, 1}}, {{1, 0}}, {{-1, 0}}, {{0, -1}}, {{-1, -1}}, {{-1, 1}}, {{1, -1}}};

    public King(boolean white) {
        super(white);
        this.hasMoved = false;
    }

    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    @Override
    public int[][][] getMoves() {
        return moves;
    }
    
}
