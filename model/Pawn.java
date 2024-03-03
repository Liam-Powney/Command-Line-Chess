package model;

public class Pawn extends Piece{
    
    private boolean enPassantable;

    private static int[][][] moves(boolean white) {
        if (white) {
            return new int[][][]  {{{ 0, 1}, {0, 2}},
                                    {{-1, 1}},
                                    {{ 1, 1}}};
        }
        else {
            return new int[][][]  {{{ 0,-1}, {0,-2}},
                                    {{-1,-1}},
                                    {{ 1,-1}}};
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

    @Override
    public void setMoved(Move m) {
        if (Math.abs(m.getEndRow()-m.getStartRow())==2) {
            this.enPassantable=true;
        }
        this.hasMoved=true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {return false;}
        if (!(obj instanceof Pawn)) {return false;}
        Pawn p = (Pawn)obj;
        if (this.enPassantable==p.getEnPassantable()) {
            return true;
        }
        return false;

    }
}
