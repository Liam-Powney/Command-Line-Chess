package model;

public class Knight extends Piece{

    private final char pieceChar = 'N';
    private final static int[][][] moves = {{{ 2, 1}},
                                            {{-2, 1}},
                                            {{ 2,-1}},
                                            {{-2,-1}},
                                            {{ 1, 2}},
                                            {{-1, 2}},
                                            {{ 1,-2}},
                                            {{-1,-2}}};

    public Knight(boolean white) {
        super(white);
    }
    
    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    @Override
    public int[][][] getMoves() {return moves;}
    
}
