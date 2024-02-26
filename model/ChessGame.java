package model;

import java.util.ArrayList;

public class ChessGame extends GameState{
    
    private ArrayList<Piece[][]> boardStack;

    private boolean whitesTurn;

    // constructor - set up the board and pieces
    public ChessGame() {
        this.whitesTurn = true;
        Piece[][] board = new Piece[8][8];
        // white pieces
        board[0][0] = new Rook(true);
        board[0][1] = new Knight(true);
        board[0][2] = new Bishop(true);
        board[0][3] = new King(true);
        board[0][4] = new Queen(true);
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
        board[7][3] = new King(false);
        board[7][4] = new Queen(false);
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

        boardStack=new ArrayList<Piece[][]>();
        boardStack.add(board);
    }

    public Piece[][] getBoard() {
        return boardStack.getLast();
    }

    public Piece[][] cloneBoard(Piece[][] board) {
        Piece[][] out = new Piece[8][8];
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                Piece square = board[i][j];
                if (square==null) {
                    out[i][j]=null;
                }
                else if (square instanceof Pawn) {
                    Pawn p = (Pawn)square;
                    out[i][j]= new Pawn(p);
                }
                else if (square instanceof Rook) {
                    Rook r = (Rook)square;
                    out[i][j]= new Rook(r);
                }
                else if (square instanceof Knight) {
                    Knight k = (Knight)square;
                    out[i][j]= new Knight(k);
                }
                else if (square instanceof Bishop) {
                    Bishop b = (Bishop)square;
                    out[i][j]= new Bishop(b);
                }
                else if (square instanceof King) {
                    King k = (King)square;
                    out[i][j]= new King(k);
                }
                else if (square instanceof Queen) {
                    Queen q = (Queen)square;
                    out[i][j]= new Queen(q);
                }
            }
        }
        return out;
    }

    public boolean getWhitesTurn() {
        return whitesTurn;
    }

    // changes the turn
    public void nextTurn() {
        whitesTurn = !whitesTurn;
    }

    public void attemptMove(Move m) {
        // find the relevant piece and start coords for the given move and ensures there is only one piece on the board that can make this move
        Move move = validatMove(m, boardStack.getLast());
        if (move.getPiece()==null) {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Couldn't validate move");
            }
        }
        Piece[][] nwBoard = performMove(move, getBoard());
        if (boardCheckChecker(nwBoard, whitesTurn)) {
            System.out.println("Move leaves king in check");
            return;
        }

        boardStack.add(nwBoard);
        nextTurn();
        
    }

    public Piece[][] performMove(Move m, Piece[][] board) {

        // clone board for the output 
        Piece[][] out = cloneBoard(board);
        // make all pawns non-enpassantable now
        for (Piece[] row : out) {
            for (Piece square : row) {
                if (square!=null && square instanceof Pawn && ((Pawn)square).getEnPassantable()) {
                    ((Pawn)square).setEnPassantable(false);
                }
            }
        }
        
        // move the pieces
        /// if it's an enpassant move
        if (m.getEnPassant()) {
            out[m.getEndRow()][m.getEndCol()] = m.getPiece();
            out[m.getStartRow()][m.getStartCol()] = null;
            out[m.getStartRow()][m.getEndCol()] = null;
        }
        /// otherwise
        else {
            out[m.getEndRow()][m.getEndCol()] = m.getPiece();
            out[m.getStartRow()][m.getStartCol()] = null;
        }
        // set any flags necessary for 'has moved' and 'enpassantable'
        out[m.getEndRow()][m.getEndCol()].setMoved(m);

        return out;
    }

    // uses the board to find the relevant piece to a given move and sets the piece, start col and start row values for move m
    public Move validatMove(Move m, Piece[][] board) {

        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int squareRow=0; squareRow<8; squareRow++) {
            for (int squareCol=0; squareCol<8; squareCol++) {
                // search columns and rows only that we need to given any disambig data present in the move
                if ( (m.getStartCol()==-1 || squareCol==m.getStartCol()) && (m.getStartRow()==-1 || squareRow==m.getStartRow()) ) {
                    Piece square = board[squareRow][squareCol];
                    if (square!=null && square.getType().equals(m.getPieceType()) && square.getWhite()==whitesTurn) {
                        Move testMove = new Move(m, squareCol, squareRow, square);
                        if (isPieceInRange(testMove.getPiece(), squareCol, squareRow, testMove.getEndCol(), testMove.getEndRow(), board)) {
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

    // states whether a piece can 'reach' a given end co-ordinate given a start co-ordinate for a given board
    public boolean isPieceInRange(Piece p, int startCol, int startRow, int endCol, int endRow, Piece[][] board) {

        // for each possible move vector in each direction for the piece
        for (int i=0; i<p.getMoves().length; i++) {
            for (int j=0; j<p.getMoves()[i].length; j++) { 
                int[] possibleVec = p.getMoves()[i][j];
                // make sure we aren't going off the board :)
                Piece currentSquare;
                try {
                    currentSquare = board[startRow+possibleVec[1]][startCol+possibleVec[0]];
                // catch block stops the direction when indices start going off the board
                } catch (Exception e) {
                    break;
                }
                // if the current square is NOT the target square 
                if ( startCol+possibleVec[0]!=endCol || startRow+possibleVec[1]!=endRow ) {
                    // if it's empty, continue in the current direction
                    if (currentSquare==null) {
                        // make sure pawn can't move forwards 2 spaces if it's already moved
                        if (p instanceof Pawn && p.getHasMoved() && i==0) {
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
                    if (!(p instanceof Pawn)) {
                        // if the square is either empty or occupied by an enemy piece
                        if (currentSquare==null || currentSquare.getWhite()!=p.getWhite()) {
                            return true;
                        }
                        else {return false;}
                    }
                    //piece is a pawn
                    else {
                        //if the square is either 
                        ////1. empty and the pawn is moving forwards
                        if (currentSquare==null && i==0) {
                            return true;
                        }
                        ////2. the square is taken by an enemy piece and the pawn is moving diagonally
                        else if( currentSquare!=null && i==1 && currentSquare.getWhite()!=p.getWhite()) {
                            return true;
                        }
                        ////3. the piece is moving diagonally and the square is empty but the piece to the direct side of the pawn is an enpassantable pawn 
                        else if (currentSquare==null && i==1 && (board[startRow][endCol] instanceof Pawn) && ((Pawn)board[startRow][endCol]).getEnPassantable()) {
                            return true;
                        }
                        else {return false;}
                    }
                }
            } 
        }
        return false;
    } 

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    public boolean boardCheckChecker(Piece[][] board, boolean whiteKing) {
        
        int kingCol=-1, kingRow=-1;

        Outer:
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (board[i][j] instanceof King && board[i][j].getWhite()==whiteKing) {
                    kingCol=j;
                    kingRow=i;
                    break Outer;
                }
            }
        }

        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                Piece square = board[i][j];
                if (square!=null && square.getWhite()!=whiteKing) {
                    if (isPieceInRange(square, j, i, kingCol, kingRow, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }





}
