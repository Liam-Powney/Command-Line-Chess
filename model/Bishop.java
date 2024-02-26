package model;

public class Bishop extends Piece {

    public Bishop(boolean white) {
        super(white, "bishop", new int[][][] {{{ 1, 1}, { 2, 2}, { 3, 3}, { 4, 4}, { 5, 5}, { 6, 6}, { 7, 7}},
                                    {{-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7}},
                                    {{ 1,-1}, { 2,-2}, { 3,-3}, { 4,-4}, { 5,-5}, { 6,-6}, { 7,-7}},
                                    {{-1,-1}, {-2,-2}, {-3,-3}, {-4,-4}, {-5,-5}, {-6,-6}, {-7,-7}}});
    }

    public Bishop(Bishop b) {
        super(b);
    }
}
