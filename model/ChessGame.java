package model;

public class ChessGame extends GameState{
    
    private Piece[][] board = new Piece[8][8];
    private boolean whitesTurn;

    public ChessGame() {
        this.whitesTurn = true;
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

    // row character to int converter
    public int columnToInt(char ch) {
        if (ch < 'a' || ch > 'h') {
            throw new IllegalArgumentException("Character must be in the range 'a' to 'z'");
        }
        return ch - 'a' + 1;
    }

    // attempts to make a move on the board by parsing the inputted PGN command. 
    // Returns true if the move is succesful, returns false if the move is unsuccesful.
    public boolean attemptMove(String c) {

        //SPECIAL CASES
        // castling short
        if (c.equals("0-0")) {
            System.out.println("You will castle short :)");
            return true;
        }
        // castling long
        else if (c.equals("0-0-0")) {
            System.out.println("You will castle long :)");
            return true;
        }

        //normal command parsing
        char movingPieceType;
        boolean attemptedPawnPromo = false;
        char pawnColumn = '0';
        char promoPiece = '0';
        boolean captureAttempt = false;
        char rowDisambig = '0', colDisambig = '0';
        char attemptxPos, attemptyPos;
        boolean attemptCheck = false, attemptCheckmate = false;


        try {
            // is the move an attempted check or checkmate?
            if (c.substring(c.length()-1).equals("+")) {
                // King can't check or checkmate!!
                if (c.charAt(0) == 'K') {
                    return false;
                }
                if (c.substring(c.length()-2).equals("++")) {
                    attemptCheckmate = true;
                    c = c.substring(0, c.length()-2);
                }
                else {
                    attemptCheck = true;
                    c = c.substring(0, c.length()-1);
                }
            }



            // which type of piece is moving?
            // is it a pawn
            var char1 = c.charAt(0);
            if ('a' <= char1 && 'h' >= char1) {
                movingPieceType = ' ';
                pawnColumn = c.charAt(0);
                // is it a pawn capture? If so, need to take note of the first character which specifies the column of departure and strip it from the command
                if (c.contains("x")) {
                    c = c.substring(1, c.length());
                }
                // is it a pawn promo attempt?
                try {
                    var promoSubstring = c.substring(c.length()-4, c.length());
                    // check piece promo type and ensure pawn has reached the end of the board
                    if ( (promoSubstring.equals("8(Q)")) || (promoSubstring.equals("8(R)")) || (promoSubstring.equals("8(N)")) || (promoSubstring.equals("8(B)"))) {
                        attemptedPawnPromo = true;
                        promoPiece = promoSubstring.charAt(2);
                        c = c.substring(0, c.length()-3);
                    }
                } catch (Exception e) {}
            }
            // if it's not a pawn but a valid piece
            else if ( char1 == 'K' || char1 == 'Q' || char1 == 'R' || char1 == 'N' || char1 == 'B' ) {
                movingPieceType = char1;
                c = c.substring(1, c.length());
            }
            // it's an invalid piece
            else {
                return false;
            }


            // what's the destination square?
            char ch1 = c.charAt(c.length()-2), ch2 = c.charAt(c.length()-1);
            // are the destination values valid?
            if ( ((ch1>='a') && ( ch1<='h') ) && ((ch2>='1') && ( ch2<='8')) ) {
                attemptxPos = ch1;
                attemptyPos = ch2;
                c = c.substring(0, c.length()-2);
            }
            else {
                return false;
            }
            
            // is the move a capture?
            try {
                if (c.substring(c.length()-1).equals("x")) {
                    c = c.replace("x", "");
                    captureAttempt = true;
                }
            } catch (Exception e) {}


            // are there any disambiguation inputs?
            // 2 disambig inputs - must be queen!!
            if ((c.length() == 2) && (movingPieceType == 'Q')) {
                ch1 = c.charAt(0);
                ch2 = c.charAt(1);
                // are the disambiguation values valid?
                if ( ((ch1>='a') && ( ch1<='h') ) && ((ch2>='1') && ( ch2<='8')) ) {
                    colDisambig = ch1;
                    rowDisambig = ch2;
                }
                // invalid input
                else {
                    return false;
                }
            }
            // 1 disambiguation input - code will only reach here if movingPieceType != ' ' aka not a pawn
            else if (c.length() == 1 && movingPieceType !='K') {
                ch1 = c.charAt(0);
                // is it a column disambiguation input? aka a char between a and h?
                if ( (ch1>='a') && ( ch1<='h') ) {
                    colDisambig = ch1;
                }
                // is it a row disambiguation input? aka a number between 1 and 8?
                else if ( (ch1>='1') && ( ch1<='8') ) {
                    rowDisambig = c.charAt(0);
                }
                // invalid input
                else {
                    return false;
                }
            }
            // more than two chars long in which case, instruction was invalid
            else if (c.length() != 0) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        String moveChecker = "";
        if (movingPieceType == ' ')
        {
            moveChecker+=("Move info is: " + Character.toString(pawnColumn) + "Pawn");
        }
        else {
            moveChecker+=("Move info is: Piece " + Character.toString(movingPieceType));
        }
        if (rowDisambig != '0') {
            moveChecker+=(" (row " + (rowDisambig) + ")");
        }
        if (colDisambig != '0') {
            moveChecker+=(" (col " + (colDisambig) + ")");
        }
        if (captureAttempt) {
            moveChecker+=(" wants to take a piece on ");
        }
        else {
            moveChecker+=(" wants to move to ");
        }
        moveChecker+=(Character.toString(attemptxPos) + Character.toString(attemptyPos));
        if (attemptCheck) {
            moveChecker+=(" and check");
        }
        else if (attemptCheckmate) {
            moveChecker+=(" and checkmate");
        }
        if (attemptedPawnPromo) {
            moveChecker+=(" (pawn promotion to " + Character.toString(promoPiece) + ")");
        }
        System.out.println(moveChecker);
        // if code as got this far without returning false then the instruction made sense. But is the requested move valid?
        return true;
    }
}
