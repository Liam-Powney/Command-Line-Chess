package model;
public class King extends Piece{
    
    private final String pieceString = "K";
    private final int[][] moves = { {0, 1}, {1, 1}, {1, 0}, {-1, 0}, {0, -1}, {-1, -1}, {-1, 1}, {1, -1} };

    public King(boolean white) {
        super(white);
    }
    
    @Override
    public String getPieceString() {
        return pieceString;
    }
    
}
