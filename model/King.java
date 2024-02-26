package model;

public class King extends Piece{

    public King(boolean white) {
        super(white, "king", new int[][][] {{{-1,-1}},
                                    {{-1, 0}},
                                    {{-1, 1}},
                                    {{ 0,-1}},
                                    {{ 0, 1}},
                                    {{ 1,-1}},
                                    {{ 1, 0}},
                                    {{ 1, 1}}});
    } 
}
