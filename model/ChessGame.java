package model;

import java.util.LinkedList;
import java.util.HashMap;

public class ChessGame extends GameState{
    
    private LinkedList<Piece[][]> boardStack;

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

        boardStack=new LinkedList<Piece[][]>();
        boardStack.add(board);
    }

    //
    // GETTERS AND SETTERS
    //

    public Piece[][] getBoard() {return boardStack.getFirst();}
    public boolean getWhitesTurn() {return whitesTurn;}

    //
    // MOVE MAKER - this is the funciton the controller will call
    //
    public void attemptMove(String input) {

        // parse the instruction
        Move m;
        try {
            m = moveParser(input);
        } catch (Exception e) {
            System.out.println("Input invalid.");
            return;
        }

        // check the instruction against game logic to ensure it only corresponds to one psosible move
        HashMap<Move, Piece[][]> moveList;
        try {
            moveList = possibleMoves(m);
            if (moveList.size()>1) {
                throw new Exception("More than one piece can make that move!");
            }
            else if (moveList.size()==0) {
                throw new Exception("No piece can make that move!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        Move currentMove = moveList.keySet().iterator().next();
        Piece[][] newBoard = moveList.get(currentMove);
        boardStack.push(newBoard);
        whitesTurn=!whitesTurn;
        if (currentMove.getCheckmate()) {
            // TODO: go to endgame screen somehow??
        }
    }

    //
    // GAME LOGIC
    //

    // returns a hashmap of possible moves and their 'outcome' boards based on the information encoded in a PGN instruction parsed into move m.
    // if it is length 0 there are no pieces that can make the move, if it is length>1 then the instruciton is ambiguous.
    // Note: This funciton needs to make the moves and assess their outcomes in order to see if they are legal. Thus, since the work is being
    // done anyway, if it is legal then the outcome Piece[][] is saved in the hashmap, hence the funciton 'performMove' doesn't need to be 
    // called again in 'attempt move' - it's already been called when assessing move validity and it's out-state returned.
    private HashMap<Move, Piece[][]> possibleMoves(Move m) {

        HashMap<Move, Piece[][]> out = new HashMap<Move, Piece[][]>();

        Piece[][] board = boardStack.getFirst();
        // for every square of the board
        for (int squareRow=0; squareRow<8; squareRow++) {
            for (int squareCol=0; squareCol<8; squareCol++) {
                // search columns and rows only that we need to given any disambig data present in the move
                if ( (m.getStartCol()==-1 || squareCol==m.getStartCol()) && (m.getStartRow()==-1 || squareRow==m.getStartRow()) ) {
                    Piece square = board[squareRow][squareCol];
                    // does the square have a piece on that is the correct colour and type?
                    if (square!=null && square.getType().equals(m.getPieceType()) && square.getWhite()==whitesTurn) {
                        // test the piece to see if it is in range of the target square
                        Move testMove = new Move(m, squareCol, squareRow, square);
                        if (isPieceInRange(testMove.getPiece(), squareCol, squareRow, testMove.getEndCol(), testMove.getEndRow())) {
                            // create the board after the move
                            Piece[][] boardAfterMove = performMove(testMove);
                            // check the move doesn't leave the player in check
                            if (checkChecker(boardAfterMove, whitesTurn)) {
                                continue;
                            }
                            // check the move correctly leaves the enemy in check/not in check as indicated in their PGN instruction
                            if (checkChecker(boardAfterMove, !whitesTurn)!=m.getCheck()) {
                                continue;
                            }
                            // check the move correctly leaves the enemy in check/not in check as indicated in their PGN instruction
                            if (checkmateChecker(boardAfterMove, !whitesTurn)!=m.getCheckmate()) {
                                continue;
                            }
                            out.put(m, boardAfterMove);
                        }
                    }
                }
            }
        }
        return out;
    }

    // states whether a piece can 'reach' a given end co-ordinate given a start co-ordinate for a given board
    private boolean isPieceInRange(Piece p, int startCol, int startRow, int endCol, int endRow) {

        Piece[][] board = boardStack.getFirst();
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

    // performs move m on current board and returns a new Piece[][] of the board of the resulting state
    private Piece[][] performMove(Move m) {

        // clone board for the output 
        Piece[][] out = cloneBoard(getBoard());
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

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    public boolean checkChecker(Piece[][] board, boolean whitesKing) {
        
        Piece king=null;
        int kingCol=-1, kingRow=-1;

        Outer:
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (board[i][j] instanceof King && board[i][j].getWhite()==whitesKing) {
                    king = board[i][j];
                    kingCol=j;
                    kingRow=i;
                    break Outer;
                }
            }
        }
        try {
            if (king==null) {
                throw new Exception("Couldn't find king?? :S");
            }
            for (int i=0; i<8; i++) {
                for (int j=0; j<8; j++) {
                    Piece square = board[i][j];
                    if (square!=null && square.getWhite()!=whitesKing) {
                        if (isPieceInRange(square, j, i, kingCol, kingRow)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean checkmateChecker(Piece[][] board, boolean whiteKing) {

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

        // try all possible moves and see if there are any that get the king out of check
        // for (all possible moves)
            // if (boardAfterMove.checkChecker==false)
                // return false
        // return true;
        return false;
    }

    //
    // BOARD CLONER
    //
    private Piece[][] cloneBoard(Piece[][] board) {
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
    //
    // PGN STRING PARSER
    //

    // returns a corresponding int value for a char row value of a-h (0-7)
    public int columnToInt(char ch) {
        if (ch < 'a' || ch > 'h') {
            throw new IllegalArgumentException("Character must be in the range 'a' to 'h'");
        }
        return ch - 'a';
    }

    // converts the last two chars in a string into square co-ordinates if they are valid inputs, returns {-1, -1} if invalid
    public int[] targetCoords(String c) {
        try {
            char ch1 = c.charAt(c.length()-2), ch2 = c.charAt(c.length()-1);
            // are the destination values valid?
            if ( ((ch1>='a') && ( ch1<='h') ) && ((ch2>='1') && ( ch2<='8')) ) {
                int x = columnToInt(ch1);
                int y = Character.getNumericValue(ch2)-1;
                return new int[]{x, y};
            }
            else { throw new IllegalArgumentException("Last two characters of string are not valid board coordinates"); }
        } catch (Exception e) {
            throw new IllegalArgumentException("Not enough chars in the string to extract info");
        }
    }

    // converts a command into a Move if possible
    public Move moveParser(String c) {
        
        Move m;

        String pieceType="";
        int startCol=-1;
        int startRow=-1;
        int endCol;
        int endRow;
        boolean capture=false;;
        boolean check=false, checkmate=false;
        boolean pawnPromo=false;
        String promoPieceType="";

        // see if move wants to check or checkmate
        char ch0 = c.charAt(c.length()-1);
        if (ch0=='#') {
            checkmate=true;
            c = c.substring(0, c.length()-1);
        }
        else if (ch0=='+') {
            check=true;
            c = c.substring(0, c.length()-1);
        }

        // see if move is castling
        if (c.equals("0-0-0")) {return new Move(false, check, checkmate);}
        else if (c.equals("0-0")) {return new Move(true, check, checkmate);}

        // piece type?
        ch0 = c.charAt(0);
        if (ch0>='a' && ch0<='h') {
            pieceType = "pawn";
            startCol=columnToInt(ch0);
            // check if it's a pawn promo
            if (c.length()>3) {
                String temp = c.substring(c.length()-4);
                if ( temp.equals("8(Q)") || temp.equals("8(R)") || temp.equals("8(N)") || temp.equals("8(B)") ) {
                    pawnPromo=true;
                    if (temp.charAt(2) == 'Q') {promoPieceType = "queen";}
                    else if (temp.charAt(2) == 'R') {promoPieceType = "rook";}
                    else if (temp.charAt(2) == 'N') {promoPieceType = "knight";}
                    else if (temp.charAt(2) == 'B') {promoPieceType = "bishop";}
                    c = c.substring(0, c.length()-3);
                }
                
            }
        }
        else if (ch0 == 'K' ) {pieceType = "king";}
        else if (ch0 == 'Q' ) {pieceType = "queen";}
        else if (ch0 == 'R' ) {pieceType = "rook";}
        else if (ch0 == 'N' ) {pieceType = "knight";}
        else if (ch0 == 'B' ) {pieceType = "bishop";}
        else {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid piece type at command string index 0");
            }
        }
        // as long as the command isn't (pawn move + length=2)
        if (!( (pieceType.equals("pawn")) && (c.length()==2) )) {
            c = c.substring(1);
        }
        // extract end position co-ordinates from the string
        var temp=targetCoords(c);
        endCol=temp[0];
        endRow=temp[1];
        c = c.substring(0, c.length()-2);
        //is the move a capture?
        if (c.length()!=0 && c.charAt(c.length()-1)=='x') {
            capture=true;
            c = c.substring(0, c.length()-1);
        }
        //disambiguation info
        if (c.length()==2) {
            temp = targetCoords(c);
            startCol=temp[0];
            startRow=temp[1];
        }
        else if (c.length()==1) {
            ch0 = c.charAt(0);
            if (ch0>='a' && ch0<='h'){
                startCol = columnToInt(c.charAt(0));
            }
            else if (Character.getNumericValue(ch0)>=1 && Character.getNumericValue(ch0)<=8) {
                startRow = Character.getNumericValue(ch0)-1;
            }
            else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    System.out.println("Invalid command");
                }
            }
        }
        else if (c.length()!=0){
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid piece type at command string index 0");
            }
        }

        // return the move :)
        if (pieceType.equals("pawn")) {
            m = new Move(endCol, endRow, capture, check, checkmate, pawnPromo, promoPieceType);
            if (startCol!=-1) {m.setStartCol(startCol);}
            if (startRow!=-1) {m.setStartRow(startRow);}
            return m;
        }
        else {
            m = new Move(pieceType, endCol, endRow, capture, check, checkmate);
            if (startCol!=-1) {m.setStartCol(startCol);}
            if (startRow!=-1) {m.setStartRow(startRow);}
            return m;
        }
    }
}
