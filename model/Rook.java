package model;

public class Rook extends Piece{

    private final char pieceChar = 'R';
    private final static int[][][] moves = {{ { 0, 1}, { 0, 2}, { 0, 3}, { 0, 4}, { 0, 5}, { 0, 6}, { 0, 7} },
                                            { { 0,-1}, { 0,-2}, { 0,-3}, { 0,-4}, { 0,-5}, { 0,-6}, { 0,-7} },
                                            { { 1, 0}, { 2, 0}, { 3, 0}, { 4, 0}, { 5, 0}, { 6, 0}, { 7, 0} },
                                            { {-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0} }};
    private boolean hasMoved;

    public Rook(boolean white) {
        super(white);
        this.hasMoved = false;
    }
    
    @Override
    public char getPieceChar() {
        return pieceChar;
    }

    public boolean getHasMoved() {return hasMoved;}
    public void setHasMoved() {hasMoved = true;}

    @Override
    public int[][][] getMoves() {return moves;}
}
