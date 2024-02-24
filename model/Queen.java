package model;

public class Queen extends Piece{
    
    private final char pieceChar = 'Q';
    private final static int[][][] moves = {{{ 0, 1}, { 0, 2}, { 0, 3}, { 0, 4}, { 0, 5}, { 0, 6}, { 0, 7}},
                                            {{ 0,-1}, { 0,-2}, { 0,-3}, { 0,-4}, { 0,-5}, { 0,-6}, { 0,-7}},
                                            {{ 1, 0}, { 2, 0}, { 3, 0}, { 4, 0}, { 5, 0}, { 6, 0}, { 7, 0}},
                                            {{-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0}},
                                            {{ 1, 1}, { 2, 2}, { 3, 3}, { 4, 4}, { 5, 5}, { 6, 6}, { 7, 7}},
                                            {{-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7}},
                                            {{ 1,-1}, { 2,-2}, { 3,-3}, { 4,-4}, { 5,-5}, { 6,-6}, { 7,-7}},
                                            {{-1,-1}, {-2,-2}, {-3,-3}, {-4,-4}, {-5,-5}, {-6,-6}, {-7,-7}}};

    public Queen(boolean white) {
        super(white);
    }
    
    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    @Override
    public int[][][] getMoves() {return moves;}
}
