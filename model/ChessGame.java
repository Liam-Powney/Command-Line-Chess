package model;

public class ChessGame extends GameState{
    
    private Piece[][] board = new Piece[8][8];
    private boolean whitesTurn;

    public ChessGame() {
        this.whitesTurn = true;
        this.board[0][4] = new King(true);
    }

    public Piece[][] getBoard() {
        return board;
    }


}
