package model;

public class Rook extends Piece{

    private final char pieceChar = 'R';
    private final int[][][] moves = {{ { 0, 1}, { 0, 2}, { 0, 3}, { 0, 4}, { 0, 5}, { 0, 6}, { 0, 7} },
                                            { { 0,-1}, { 0,-2}, { 0,-3}, { 0,-4}, { 0,-5}, { 0,-6}, { 0,-7} },
                                            { { 1, 0}, { 2, 0}, { 3, 0}, { 4, 0}, { 5, 0}, { 6, 0}, { 7, 0} },
                                            { {-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0} }};

    public Rook(boolean white) {
        super(white);
    }
    
    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    @Override
    public int[][][] getMoves() {return moves;}
}
