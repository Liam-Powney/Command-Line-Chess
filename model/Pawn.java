package model;

public class Pawn extends Piece{
    
    private boolean enPassantable;

    private static int[][][] moves(boolean white) {
        if (white) {
            return new int[][][]  {{{ 0, 1}, {0, 2}},
                                    {{-1, 1}, {1, 1}}};
        }
        else {
            return new int[][][]  {{{ 0,-1}, {0,-2}},
                                    {{-1,-1}, {1,-1}}};
        }
    }

    public Pawn(boolean white) {
        super(white, "pawn", moves(white));
        enPassantable=false;
    }

    public Pawn(Pawn p) {
        super(p);
        this.enPassantable=p.enPassantable;
    }

    public boolean getEnPassantable() {return enPassantable;}
    public void setEnPassantable(boolean b) {this.enPassantable=b;}
}
