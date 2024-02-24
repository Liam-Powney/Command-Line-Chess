package model;

import java.util.ArrayList;

import controller.PGNChessMove;

public class ChessGame extends GameState{
    
    private Piece[][] board;
    private boolean whitesTurn;

    // constructor - set up the board and pieces
    public ChessGame() {
        this.whitesTurn = true;
        this.board = new Piece[8][8];
        // white pieces
        this.board[0][0] = new Rook(true);
        this.board[0][1] = new Knight(true);
        this.board[0][2] = new Bishop(true);
        this.board[0][3] = new King(true);
        this.board[0][4] = new Queen(true);
        this.board[0][5] = new Bishop(true);
        this.board[0][6] = new Knight(true);
        this.board[0][7] = new Rook(true);
        /*this.board[1][0] = new Pawn(true);
        this.board[1][1] = new Pawn(true);
        this.board[1][2] = new Pawn(true);
        this.board[1][3] = new Pawn(true);
        this.board[1][4] = new Pawn(true);
        this.board[1][5] = new Pawn(true);
        this.board[1][6] = new Pawn(true);
        this.board[1][7] = new Pawn(true); */
        // black pieces
        this.board[7][0] = new Rook(false);
        this.board[7][1] = new Knight(false);
        this.board[7][2] = new Bishop(false);
        this.board[7][3] = new King(false);
        this.board[7][4] = new Queen(false);
        this.board[7][5] = new Bishop(false);
        this.board[7][6] = new Knight(false);
        this.board[7][7] = new Rook(false);
        /*this.board[6][0] = new Pawn(false);
        this.board[6][1] = new Pawn(false);
        this.board[6][2] = new Pawn(false);
        this.board[6][3] = new Pawn(false);
        this.board[6][4] = new Pawn(false);
        this.board[6][5] = new Pawn(false);
        this.board[6][6] = new Pawn(false);
        this.board[6][7] = new Pawn(false); */
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

    // finds all the possible pieces that can move to the target square for a given PGN move decoded by the controller
    public ArrayList<Piece> possPieces(PGNChessMove m) {
        ArrayList<Piece> out = new ArrayList<Piece>();
        // start pos known
        int startCol = m.startPos()[0];
        int startRow = m.startPos()[1];

        for (int squareRow=0; squareRow<8; squareRow++) {
            for (int squareCol=0; squareCol<8; squareCol++) {
                // search columns and rows only that we need to given any disambig data present in the PGNMove
                if ( (startCol == -1 || squareCol == startCol) && (startRow == -1 || squareRow == startRow) ) {
                    Piece square = board[squareRow][squareCol];
                    if (square==null) {continue;}
                    if (square.getPieceChar()==m.movingPieceType() && square.getWhite()==whitesTurn && isPieceMovePossible(squareCol, squareRow, m)) {
                        out.add(square);
                    }
                }
            }
        }
        return out;
    }


    // can a piece move to the target square given the current board?
    public boolean isPieceMovePossible(int startCol, int startRow, PGNChessMove m) {

        // current piece we are working with
        Piece p = board[startRow][startCol];
        int[] moveVec = new int[] {m.endPos()[0]-startCol, m.endPos()[1]-startRow};

        // is it a pawn?
        if (m.movingPieceType() == 'p') {
            System.out.println("I haven't implemented pawn moves yet :)");
            return false;
        }
        // any other piece
        else {

            // for each direction the piece can move
            for (int[][] direction : p.getMoves()) {
                // for each increment in that direction
                for (int[] possiblePieceVec : direction) {

                    // keep indicies in bounds - omits possible moves that would send the piece off the board :)
                    try {

                        // if the current square that the piece move in the current direction corresponds to ISNT the target square of the move
                        if ( startCol+possiblePieceVec[0] != m.endPos()[0] || startRow+possiblePieceVec[1] != m.endPos()[1]) {
                            Piece square = board[startRow+possiblePieceVec[1]][startCol+possiblePieceVec[0]];
                            // if that square is empty - continue in the direction
                            if (square==null) {
                                continue;
                            }
                            // otherwise - go to the next direction
                            else {
                                break;
                            }
                        }
                         // if the current square that the piece move in the current direction corresponds to IS the target square of the move
                        else {
                            /// if square empty and move - true
                            Piece square = board[startRow+possiblePieceVec[1]][startCol+possiblePieceVec[0]];
                            if (square==null && !m.captureAttempt() && !checkChecker(board[startRow][startCol], m, true)) {
                                return true;
                            }
                            ///else if enemy piece and capture - true
                            else if ( square.getWhite()!=whitesTurn && m.captureAttempt() && !checkChecker(board[startRow][startCol], m, true)) {
                                return true;
                            }
                            /// otherwise the move isn't possible 
                            else {
                                System.out.println("The piece can't go to the target square specified. Please check to make sure the square is empty, or you correctly specified a capture attempt.");
                                return false;
                            }
                        }
                    }
                    catch (Exception e) {
                        break;
                    }
                }
            }
        }
        return false;
    }

    // check checker - does a move leave the player in check? returns true if king is in check, bool 'myKing' is true if checking same colour king as whose turn it is, false if enemy king
    public boolean checkChecker(Piece p, PGNChessMove m, boolean myKing) {
        return false;
    }

    // attempt a PGN move checking against game logic
    public boolean attemptMove(PGNChessMove m) {
        if (!m.isValidMove()) {
            return false;
        }
        if (possPieces(m).size()!=1) {
            return false;
        }
        else {
            Piece p = possPieces(m).get(0);
            Outer:
            for (int row=0; row<8; row++) {
                for (int col=0; col<8; col++) {
                    if (board[row][col] != null && board[row][col] == p) {
                        board[row][col] = null;
                        break Outer;
                    }
                }
            }
            board[m.endPos()[1]][m.endPos()[0]] = p;
            nextTurn();
            return true;
        }
    }

}
