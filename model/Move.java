package model;

public class Move {

    private String pieceType;
    private Integer startCol, startRow, endCol, endRow;
    private boolean capture, check, checkmate;

    private String promoPieceType;
    private Boolean castleShort;

    // CONSTRUCTORS
    // castling move constructor
    public Move(boolean castleShort, boolean check, boolean checkmate) {
        this.pieceType=null;
        this.startCol=null;
        this.startRow=null;
        this.endCol=null;
        this.endRow=null;
        this.capture=false;
        this.check = check;
        this.checkmate = checkmate;
        this.promoPieceType=null;
        this.castleShort = castleShort;
    }

    // pawn move constructor (enter 'null' for promo piece type if no promo happening)
    public Move(int endCol, int endRow, boolean capture, boolean check, boolean checkmate, String promoPieceType) {
        this.pieceType="pawn";
        if (capture==false) {this.startCol=endCol;}
        else {this.startCol=null;}
        this.startRow=null;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=capture;
        this.check = check;
        this.checkmate = checkmate;
        this.promoPieceType=promoPieceType;
        this.castleShort=null;
    }

    // other piece move constructor
    public Move(String pieceType, int endCol, int endRow, boolean capture, boolean check, boolean checkmate) {
        this.pieceType=pieceType;
        this.startCol=null;
        this.startRow=null;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=capture;
        this.check=check;
        this.checkmate=checkmate;
        this.promoPieceType=null;
        this.castleShort=null;
    }

    // other piece move constructor with start position info
    public Move(String pieceType, int startCol, int startRow, int endCol, int endRow, boolean capture, boolean check, boolean checkmate) {
        this.pieceType=pieceType;
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=capture;
        this.check=check;
        this.checkmate=checkmate;
        this.promoPieceType=null;
        this.castleShort=null;
    }

    // move cloner with added start position data and piece reference
    public Move(Move m, int startCol, int startRow) {
        this.pieceType=m.getPieceType();
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=m.getEndCol();
        this.endRow=m.getEndRow();
        this.capture=m.getCapture();
        this.check=m.getCheck();
        this.checkmate=m.getCheckmate();
        this.promoPieceType=m.getPromoPieceType();
        this.castleShort=m.getCastleShort();
    }

    public Boolean getCastleShort() {return castleShort;}
    public String getPieceType() {return pieceType;}
    public Integer getStartCol() {return startCol;}
    public Integer getStartRow() {return startRow;}
    public int getEndCol() {return endCol;}
    public int getEndRow() {return endRow;}
    public boolean getCapture() {return capture;}
    public boolean getCheck() {return check;}
    public boolean getCheckmate() {return checkmate;}
    public String getPromoPieceType() {return promoPieceType;}

    //public void setStartCol(Integer s) {startCol = s;}
    //public void setStartRow(Integer s) {startRow = s;}
}
