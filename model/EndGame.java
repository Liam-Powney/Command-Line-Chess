package model;

public class EndGame extends GameState{

    private boolean whiteWon;
    private Piece[][] board;

    public EndGame(Piece[][] board, boolean whiteWon) {
        super();
        this.board=board;
        this.whiteWon=whiteWon;
    }

    public boolean getWhiteWon() {return whiteWon;}
    public Piece[][] board() {return board;}
}
