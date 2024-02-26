package model;

public class Move {

    private boolean castleAttempt;
    private boolean castleShort;

    private String pieceType;
    private Piece piece;
    private int startCol, startRow, endCol, endRow;
    private boolean capture, check, checkmate;

    private boolean pawnPromo;
    private String promoPieceType;
    private boolean enPassant;

    // CONSTRUCTORS
    // castling move constructor
    public Move(boolean castleShort, boolean check, boolean checkmate) {
        this.castleAttempt = true;
        this.castleShort = castleShort;
        this.check = check;
        this.checkmate = checkmate;
    }
    // pawn move constructor
    public Move(int endCol, int endRow, boolean capture, boolean check, boolean checkmate, boolean pawnPromo, String promoPieceType) {
        this.pieceType = "pawn";
        this.startCol=-1;
        this.startRow=-1;
        this.endCol = endCol;
        this.endRow = endRow;
        this.capture = capture;
        this.check = check;
        this.checkmate = checkmate;
        this.pawnPromo = pawnPromo;
        this.promoPieceType = promoPieceType;
        this.enPassant=false;
    }
    // other piece move constructor
    public Move(String pieceType, int endCol, int endRow, boolean capture, boolean check, boolean checkmate) {
        this.pieceType = pieceType;
        this.startCol=-1;
        this.startRow=-1;
        this.endCol = endCol;
        this.endRow = endRow;
        this.capture = capture;
        this.check = check;
        this.checkmate = checkmate;
    }
    // move cloner with added start position data and piece reference
    public Move(Move m, int startCol, int startRow, Piece p) {
        this.pieceType=m.getPieceType();
        this.piece=p;
        this.startCol=startCol;
        this.startRow=startRow;
        this.endCol=m.getEndCol();
        this.endRow=m.getEndRow();
        this.capture=m.getCapture();
        this.check=m.getCheck();
        this.checkmate=m.getCheckmate();
        if (m.getPieceType().equals("pawn")) {
            this.pawnPromo=m.getPawnPromo();
            this.promoPieceType=m.getPromoPieceType();
            this.enPassant=m.getEnPassant();
        }
    }

    public boolean getCastleAttempt() {return castleAttempt;}
    public boolean getCastleShort() {return castleShort;}
    public String getPieceType() {return pieceType;}
    public Piece getPiece() {return piece;}
    public int getStartCol() {return startCol;}
    public int getStartRow() {return startRow;}
    public int getEndCol() {return endCol;}
    public int getEndRow() {return endRow;}
    public boolean getCapture() {return capture;}
    public boolean getCheck() {return check;}
    public boolean getCheckmate() {return checkmate;}
    public boolean getPawnPromo() {return pawnPromo;}
    public String getPromoPieceType() {return promoPieceType;}
    public boolean getEnPassant() {return enPassant;}

    public void setPiece(Piece p) {piece = p;}
    public void setStartCol(int s) {startCol = s;}
    public void setStartRow(int s) {startRow = s;}
    public void setEnPassant() {enPassant=true;}

}
