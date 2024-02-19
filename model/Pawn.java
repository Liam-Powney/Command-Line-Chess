package model;

public class Pawn extends Piece{
    
    private final char pieceChar = ' ';
    private final int[][] moves = { };
    
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public char getPieceChar() {
        return pieceChar;
    }
}
