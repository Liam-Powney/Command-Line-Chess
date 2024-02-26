package model;


public abstract class Piece {
    
    protected boolean white;
    protected boolean hasMoved;
    protected final int[][][] moves;
    protected String type;

    // Constructors
    public Piece(boolean white, String type, int[][][] moves){
        this.hasMoved=false;
        this.white=white;
        this.type=type;
        this.moves=moves;
    }

    // clone constructor
    public Piece(Piece p) {
        this.white = p.getWhite();
        this.hasMoved=p.getHasMoved();
        this.moves=p.getMoves();
        this.type=p.getType();
    }

    public boolean getWhite() {return white;}
    public boolean getHasMoved() {return hasMoved;}
    public String getType() {return type;}
    public int[][][] getMoves() {return moves;}

    public void setMoved(Move m) {
        // if the move is a pawn doing a double move
        if (m.getPiece() instanceof Pawn) {
            if (Math.abs(m.getEndRow()-m.getStartRow())==2) {
                ((Pawn)m.getPiece()).setEnPassantable(true);
            }
        }
        hasMoved = true;
    }


}
