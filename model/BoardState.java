package model;

public class BoardState {

    private Piece[][] board;
    private boolean whitesTurn;
    private boolean wCastleS, wCastleL, bCastleS, bCastleL;
    private int halfMove;
    private int fullMove;
    private int[] enPassantSquare;

    public BoardState() {initialiseBoard();}

    public BoardState(Piece[][] board, boolean whitesTurn, boolean wCastleS, boolean wCastleL, boolean bCastleS, boolean bCastleL, int halfMove, int fullMove, int[] enPassantSquare) {
        this.board=board;
        this.whitesTurn=whitesTurn;
        this.wCastleS=wCastleS;
        this.wCastleL=wCastleL;
        this.bCastleS=bCastleS;
        this.bCastleL=bCastleL;
        this.halfMove=halfMove;
        this.fullMove=fullMove;
        this.enPassantSquare=enPassantSquare;
    }

    private void initialiseBoard() {
        this.whitesTurn=true;
        this.halfMove=0;
        this.fullMove=1;
        this.wCastleS=true;
        this.wCastleL=true;
        this.bCastleS=true;
        this.bCastleL=true;
        this.enPassantSquare=null;
        Piece[][] board = new Piece[8][8];
        // white pieces
        board[0][0] = new Rook(true);
        board[0][1] = new Knight(true);
        board[0][2] = new Bishop(true);
        board[0][3] = new Queen(true);
        board[0][4] = new King(true);
        board[0][5] = new Bishop(true);
        board[0][6] = new Knight(true);
        board[0][7] = new Rook(true);
        board[1][0] = new Pawn(true);
        board[1][1] = new Pawn(true);
        board[1][2] = new Pawn(true);
        board[1][3] = new Pawn(true);
        board[1][4] = new Pawn(true);
        board[1][5] = new Pawn(true);
        board[1][6] = new Pawn(true);
        board[1][7] = new Pawn(true);
        // black pieces
        board[7][0] = new Rook(false);
        board[7][1] = new Knight(false);
        board[7][2] = new Bishop(false);
        board[7][3] = new Queen(false);
        board[7][4] = new King(false);
        board[7][5] = new Bishop(false);
        board[7][6] = new Knight(false);
        board[7][7] = new Rook(false);
        board[6][0] = new Pawn(false);
        board[6][1] = new Pawn(false);
        board[6][2] = new Pawn(false);
        board[6][3] = new Pawn(false);
        board[6][4] = new Pawn(false);
        board[6][5] = new Pawn(false);
        board[6][6] = new Pawn(false);
        board[6][7] = new Pawn(false);
        this.board=board;
    }

    public Piece[][] getBoard() {return board;}
    public boolean getWhitesTurn() {return whitesTurn;}
    public boolean getWCastleS() {return wCastleS;}
    public boolean getWCastleL() {return wCastleL;}
    public boolean getBCastleS() {return bCastleS;}
    public boolean getBCastleL() {return bCastleL;}
    public int[] getEnPassantSquare() {return enPassantSquare;}
    public int getHalfMove() {return halfMove;}
    public int getFullMove() {return fullMove;}
    
}
