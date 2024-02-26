package model;

public class Knight extends Piece{

    public Knight(boolean white) {
        super(white, "knight", new int[][][] {{{ 2, 1}},
                                    {{-2, 1}},
                                    {{ 2,-1}},
                                    {{-2,-1}},
                                    {{ 1, 2}},
                                    {{-1, 2}},
                                    {{ 1,-2}},
                                    {{-1,-2}}});
    }    
}
