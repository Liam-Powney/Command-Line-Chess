package controller;

public class PGNChessMove {

    private boolean isValidMove;
    // is it a castling attempt?
    private boolean castleAttempt;
    // true for short castle, false for long castle
    private boolean castleShort;
    private char movingPieceType;
    private char promoPiece;
    private boolean captureAttempt;
    private int[] startPos;
    private int[] endPos;
    private boolean attemptCheck, attemptCheckmate;


    // CONSTRUCTORS
    // an invalid move
    public PGNChessMove() {
        this.isValidMove = false;
    }
    // castling move constructor
    public PGNChessMove(boolean castleShort, boolean attemptCheck, boolean attemptCheckmate) {
        this.isValidMove = true;
        this.castleAttempt = true;
        this.castleShort = castleShort;
        this.attemptCheck = attemptCheck;
        this.attemptCheckmate = attemptCheckmate;
    }
    // valid move constructor
    public PGNChessMove(boolean castleAttempt, char movingPieceType, char promoPiece, boolean captureAttempt, int[] startPos, int[] endPos, boolean attemptCheck, boolean attemptCheckmate) {
        this.isValidMove = true;
        this.castleAttempt = castleAttempt;
        this.movingPieceType = movingPieceType;
        this.promoPiece = promoPiece;
        this.captureAttempt = captureAttempt;
        this.startPos = startPos;
        this.endPos = endPos;
        this.attemptCheck = attemptCheck;
        this.attemptCheckmate = attemptCheckmate;
    }

    // GETTERS
    public boolean isValidMove() {return isValidMove;}
    public boolean castleAttempt() {return castleAttempt;}
    public boolean castleShort() {return castleShort;}
    public char movingPieceType() {return movingPieceType;}
    public char promoPiece() {return promoPiece;}
    public boolean captureAttempt() {return captureAttempt;}
    public int[] startPos() {return startPos;}
    public int[] endPos() {return endPos;}
    public boolean attemptCheck() {return attemptCheck;}
    public boolean attemptCheckmate() {return attemptCheckmate;}

    public void printMoveInfo() {
        if (!isValidMove) {
            System.out.println("Move is invalid");
        }
        else {
            if (castleAttempt) {
                if (castleShort) {
                    System.out.println("Castling short");
                }
                else {
                    System.out.println("Castling long");
                }
                if (attemptCheck) {System.out.println("giving check");}
                else if (attemptCheckmate) {{System.out.println("giving checkmate");}
                return;
                }
            }
            if (movingPieceType == ' ') {System.out.println("Moving piece is a pawn");System.out.println("promo piece is " + promoPiece);}
            else {System.out.println("Moving piece is " + movingPieceType);}
            if (captureAttempt) {System.out.println("Capture");}
            else {System.out.println("moving");}
            System.out.println("Start pos info: " + startPos[0] + startPos[1]);
            System.out.println("End pos info: " + endPos[0] + endPos[1]);
            if (attemptCheck) {System.out.println("check");}
            if (attemptCheckmate) {System.out.println("checkmate");}
        }
    }
}
