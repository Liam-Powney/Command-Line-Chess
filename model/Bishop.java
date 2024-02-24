package model;

public class Bishop extends Piece {

    private final char pieceChar = 'B';
    private final int[][][] moves = {{{ 1, 1}, { 2, 2}, { 3, 3}, { 4, 4}, { 5, 5}, { 6, 6}, { 7, 7}},
                                            {{-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7}},
                                            {{ 1,-1}, { 2,-2}, { 3,-3}, { 4,-4}, { 5,-5}, { 6,-6}, { 7,-7}},
                                            {{-1,-1}, {-2,-2}, {-3,-3}, {-4,-4}, {-5,-5}, {-6,-6}, {-7,-7}}};

    public Bishop(boolean white) {
        super(white);
    }
    
    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    @Override
    public int[][][] getMoves() {return moves;}
}
