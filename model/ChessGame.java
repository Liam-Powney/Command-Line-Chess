package model;

import java.util.ArrayList;

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
        this.board[1][0] = new Pawn(true);
        this.board[1][1] = new Pawn(true);
        this.board[1][2] = new Pawn(true);
        this.board[1][3] = new Pawn(true);
        this.board[1][4] = new Pawn(true);
        this.board[1][5] = new Pawn(true);
        this.board[1][6] = new Pawn(true);
        this.board[1][7] = new Pawn(true);
        // black pieces
        this.board[7][0] = new Rook(false);
        this.board[7][1] = new Knight(false);
        this.board[7][2] = new Bishop(false);
        this.board[7][3] = new King(false);
        this.board[7][4] = new Queen(false);
        this.board[7][5] = new Bishop(false);
        this.board[7][6] = new Knight(false);
        this.board[7][7] = new Rook(false);
        this.board[6][0] = new Pawn(false);
        this.board[6][1] = new Pawn(false);
        this.board[6][2] = new Pawn(false);
        this.board[6][3] = new Pawn(false);
        this.board[6][4] = new Pawn(false);
        this.board[6][5] = new Pawn(false);
        this.board[6][6] = new Pawn(false);
        this.board[6][7] = new Pawn(false);
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

    // uses the board to find the relevant piece to a given move and sets the piece, start col and start row values for move m
    public Move findPiece(Move m) {

        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int squareRow=0; squareRow<8; squareRow++) {
            for (int squareCol=0; squareCol<8; squareCol++) {
                // search columns and rows only that we need to given any disambig data present in the move
                if ( (m.getStartCol()==-1 || squareCol==m.getStartCol()) && (m.getStartRow()==-1 || squareRow==m.getStartRow()) ) {
                    Piece square = board[squareRow][squareCol];
                    if (square!=null && square.getType().equals(m.getPieceType()) && square.getWhite()==whitesTurn) {
                        Move testMove = new Move(m, squareCol, squareRow, square);
                        if (isMoveInRange(testMove)) {
                            possibleMoves.add(testMove);
                        }
                    }
                }
            }
        }
        if (possibleMoves.size()==1) {
            return possibleMoves.get(0);
        }
        m.setPiece(null);
        return m;
    }

    public void performMove(Move m) {
        board[m.getEndRow()][m.getEndCol()] = m.getPiece();
        board[m.getStartRow()][m.getStartCol()] = null;
        m.getPiece().setMoved();
        nextTurn();
    }

    public boolean isMovePossible(Move m) {
        if (m.getPiece()==null) {return false;}
        if (isMoveInRange(m) && !moveCheckChecker(m, whitesTurn)) {return true;}
        return false;
    }

    // for a given move m, sees whether the piece can make it to the end position square
    public boolean isMoveInRange(Move m) {

        // for each possible move vector in each direction for the piece
        for (int i=0; i<m.getPiece().getMoves().length; i++) {
            for (int j=0; j<m.getPiece().getMoves()[i].length; j++) { 
                int[] possibleVec = m.getPiece().getMoves()[i][j];
                // make sure we aren't going off the board :)
                Piece currentSquare;
                try {
                    currentSquare = board[m.getStartRow()+possibleVec[1]][m.getStartCol()+possibleVec[0]];
                // catch block stops the direction when indices start going off the board
                } catch (Exception e) {
                    break;
                }
                // if the current square is NOT the target square 
                if ( m.getStartCol()+possibleVec[0]!=m.getEndCol() || m.getStartRow()+possibleVec[1]!=m.getEndRow() ) {
                    // if it's empty, continue in the current direction
                    if (currentSquare==null) {
                        // make sure pawn can't move forwards 2 spaces if it's already moved
                        if (m.getPiece() instanceof Pawn && m.getPiece().getHasMoved() && i==0) {
                            break;
                        }
                    }
                    // if it's taken, move to the next direction
                    else {
                        break;
                    }
                }
                // if the current square IS the target square
                else {
                    // if the piece isn't a pawm
                    if (!(m.getPiece() instanceof Pawn)) {
                        // if the square is either empty or occupied by an enemy piece
                        if (currentSquare==null || currentSquare.getWhite()!=m.getPiece().getWhite()) {
                            return true;
                        }
                        else {return false;}
                    }
                    //piece is a pawn
                    else {
                        //if the square is either 1. empty and the pawn is moving forwards, or 2. the square is taken and the pawn is moving diagonally 
                        if ((currentSquare==null && i==0) || (currentSquare!=null && i==1 && currentSquare.getWhite()!=m.getPiece().getWhite())) {
                            return true;
                        }
                        else {return false;}
                    }
                }
            } 
        }
        return false;
    }

    public boolean moveCheckChecker(Move m, boolean myKing) {
        return false;
    }

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    public boolean boardCheckChecker(Piece[][] b, boolean whiteCheck) {
        return false;
    }





}
