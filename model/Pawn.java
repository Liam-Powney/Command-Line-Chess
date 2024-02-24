package model;

public class Pawn extends Piece{
    
    private final char pieceChar = 'p';
    private final int[][][] moves;
    
    public Pawn(boolean white) {
        super(white);
        this.hasMoved = false;
        if (white) {
            this.moves = new int[][][] {    
                                            // forwards (including double move)                        
                                            {{ 0, 1}, {0, 2}},
                                            // taking moves
                                            {{-1, 1}, {1, 1}}};
        }
        else {
            this.moves = new int[][][] {    
                                            // forwards (including double move)                        
                                            {{ 0,-1}, {0,-2}},
                                            // taking moves
                                            {{-1,-1}, {1,-1}}};
        }
    }

    @Override
    public char getPieceChar() {return pieceChar;}

    @Override
    public int[][][] getMoves() { return moves;}



}
