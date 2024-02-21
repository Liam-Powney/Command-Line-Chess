package model;

import controller.PGNChessMove;

public class ChessGame extends GameState{
    
    private Piece[][] board;
    private boolean whitesTurn;

    // constructor - set up the board and pieces
    public ChessGame() {
        this.whitesTurn = true;
        this.board = new Piece[8][8];
        this.board[0][4] = new King(true);
        this.board[7][4] = new King(false);
    }

    public Piece[][] getBoard() {
        return board;
    }

    public boolean getWhitesTurn() {
        return whitesTurn;
    }

    // changes the turn
    public void nextTurn() {
        whitesTurn = !whitesTurn;
    }
    /*
    // returns a corresponding int value for a char row value of a-h (1-8)
    public int columnToInt(char ch) {
        if (ch < 'a' || ch > 'h') {
            throw new IllegalArgumentException("Character must be in the range 'a' to 'h'");
        }
        return ch - 'a' + 1;
    }

    // extract and validate target xcoord form a PGN move, returns {-1, -1} if invalid
    public int[] targetCoords(String c) {
        try {
            char ch1 = c.charAt(c.length()-2), ch2 = c.charAt(c.length()-1);
            // are the destination values valid?
            if ( ((ch1>='a') && ( ch1<='h') ) && ((ch2>='1') && ( ch2<='8')) ) {
                int x = columnToInt(ch1);
                int y = Character.getNumericValue(ch2);
                return new int[]{x, y};
            }
            else { throw new IllegalArgumentException("Last two characters of string are not valid board coordinates"); }
        } catch (Exception e) {
            throw new IllegalArgumentException("Not enough chars in the string to extract info");
        }
    }


    // PGN instruction parser - decodes a PGN command from user and returns an object with all decoded information including whether the instruction is valid
    public PGNChessMove pgnParser(String c) {
        try {
            // is it an attempted check or checkmate?
            boolean check = false;
            boolean checkmate = false;
            if (c.charAt(c.length()-1)=='#') {
                checkmate = true;
                c = c.substring(0, c.length()-1);
            }
            else if (c.charAt(c.length()-1)=='+') {
                check = true;
                c = c.substring(0, c.length()-1);
            }

            // SPECIAL CASES
            // castling short
            if (c.equals("0-0-0")) {
                return new PGNChessMove(false, check, checkmate);
            }
            else if (c.equals("0-0")) {
                return new PGNChessMove(true, check, checkmate);
            }

            // move info vars
            char ch0 = c.charAt(0);
            char movingPieceType;
            char promoPiece = ' ';
            boolean cap = false;
            int[] startPos = new int[]{-1, -1};
            int[] newPos = new int[]{-1,-1};

            // what is the piece type?
            // pawn
            if (ch0>='a' && ch0<='h') {
                movingPieceType = ' ';
                startPos[0] = columnToInt(ch0);
                try {
                    var temp = c.substring(c.length()-4);
                    if ( temp.equals("8(Q)") || temp.equals("8(R)") || temp.equals("8(N)") || temp.equals("8(B)") ) {
                        promoPiece = temp.charAt(2);
                        c = c.substring(0, c.length()-3);
                    }
                    else { promoPiece = ' ';}
                } catch (Exception e) {
                    promoPiece = ' ';
                }
            }
            // non pawn
            else if (ch0 == 'K' || ch0 == 'Q' || ch0 == 'R' || ch0 == 'N' || ch0 == 'B') {
                movingPieceType = ch0;
                c = c.substring(1);
            }
            // invalid character
            else {
                return new PGNChessMove();
            }
            // newPos
            newPos = targetCoords(c);
            c = c.substring(0, c.length()-2);
            // any more info?
            if (c.length() == 0) {
                return new PGNChessMove(false, movingPieceType, promoPiece, cap, startPos, newPos, check, checkmate);
            }
            // capture?
            if (c.charAt(c.length()-1)=='x') {
                cap = true;
                c = c.substring(0, c.length()-1);
            }
            // any more info?
            if (c.length()==0) {
                return new PGNChessMove(false, movingPieceType, promoPiece, cap, startPos, newPos, check, checkmate);
            }
            //disambig co-ords? (for non-pawn/king)
            if (movingPieceType == 'K') {
                return new PGNChessMove();
            }
            else {
                // 2 disambigs
                if (c.length() == 2) {
                    System.out.println("c is now " + c);
                    startPos = targetCoords(c);
                }
                // 1 disambig
                else if (c.length() == 1) {
                    ch0 = c.charAt(0);
                    int temp = Character.getNumericValue(ch0);
                    if (ch0>='a' && ch0<='h'){
                        startPos[0] = columnToInt(c.charAt(0));
                    }
                    else if ( temp>=1 && temp<=8 ) {
                        startPos[1] = temp;
                    }
                    else {
                        return new PGNChessMove();
                    }
                }
                // anything else is invalid
                else {
                    return new PGNChessMove();
                }
            }
            // return all the info :)
            return new PGNChessMove(false, movingPieceType, promoPiece, cap, startPos, newPos, check, checkmate);
        }
        // any error indicates an invalid command 
        catch (Exception e) {
            return new PGNChessMove();
        }
    }
    */
}
