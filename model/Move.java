package model;

public class Move {

    private String pieceType;
    private Integer startCol, startRow, endCol, endRow;
    private boolean capture, check, checkmate;

    private Piece promoPiece;
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
        this.promoPiece=null;
        this.castleShort = castleShort;
    }
    // generic move constructors - used for game logic
    public Move(String pieceType, Integer startCol, Integer startRow, Integer endCol, Integer endRow, boolean capture) {
        this.pieceType=pieceType;
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=capture;
        this.check = false;
        this.checkmate = false;
        this.promoPiece=null;
        this.castleShort = null;
    }
    public Move(Integer startCol, Integer startRow, Integer endCol, Integer endRow) {
        this.pieceType=null;
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=false;
        this.check = false;
        this.checkmate = false;
        this.promoPiece=null;
        this.castleShort = null;
    }
    // pawn move constructor (enter 'null' for promo piece type if no promo happening)
    public Move(Integer endCol, Integer endRow, Integer startCol, boolean capture, boolean check, boolean checkmate, Piece promoPiece) {
        this.pieceType="pawn";
        this.startCol=startCol;
        this.startRow=null;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=capture;
        this.check = check;
        this.checkmate = checkmate;
        this.promoPiece=promoPiece;
        this.castleShort=null;
    }

    // other piece move constructor with start position info
    public Move(String pieceType, Integer startCol, Integer startRow, Integer endCol, Integer endRow, boolean capture, boolean check, boolean checkmate) {
        this.pieceType=pieceType;
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=endCol;
        this.endRow=endRow;
        this.capture=capture;
        this.check=check;
        this.checkmate=checkmate;
        this.promoPiece=null;
        this.castleShort=null;
    }

    // move cloner with added start position data and piece reference
    public Move(Move m, Integer startCol, Integer startRow) {
        this.pieceType=m.getPieceType();
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=m.getEndCol();
        this.endRow=m.getEndRow();
        this.capture=m.getCapture();
        this.check=m.getCheck();
        this.checkmate=m.getCheckmate();
        this.promoPiece=m.getPromoPiece();
        this.castleShort=m.getCastleShort();
    }

    public Boolean getCastleShort() {return castleShort;}
    public String getPieceType() {return pieceType;}
    public Integer getStartCol() {return startCol;}
    public Integer getStartRow() {return startRow;}
    public Integer getEndCol() {return endCol;}
    public Integer getEndRow() {return endRow;}
    public boolean getCapture() {return capture;}
    public boolean getCheck() {return check;}
    public boolean getCheckmate() {return checkmate;}
    public Piece getPromoPiece() {return promoPiece;}
}
