package model;

public class Rook extends Piece{

    public Rook(boolean white) {
        super(white, "rook", new int[][][] {{{ 0, 1}, { 0, 2}, { 0, 3}, { 0, 4}, { 0, 5}, { 0, 6}, { 0, 7} },
                                    { { 0,-1}, { 0,-2}, { 0,-3}, { 0,-4}, { 0,-5}, { 0,-6}, { 0,-7} },
                                    { { 1, 0}, { 2, 0}, { 3, 0}, { 4, 0}, { 5, 0}, { 6, 0}, { 7, 0} },
                                    { {-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0} }});
    }

    public Rook(Rook r) {
        super(r);
    }
}
