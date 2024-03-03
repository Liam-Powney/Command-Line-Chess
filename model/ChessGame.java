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
    public void attemptMove(String input) throws IllegalArgumentException {

        // parse the instruction
        Move m;
        try {
            m = moveParser(input);
        } catch (Exception e) {
            throw new IllegalArgumentException("That input is invalid. Reason give: " + e.getMessage());
        }

        // castling special case
        if (m.getCastleShort()!=null) {attemptCastle(m); return;}

        // check the instruction against game logic to ensure it only corresponds to one possible move for the current board
        HashMap<Move, Piece[][]> moveList = possibleMovesForDecodedPGN(m, getBoard());
        if (moveList.size()>1) {
            throw new IllegalArgumentException("More than one piece can make that move!");
        }
        else if (moveList.size()==0) {
            throw new IllegalArgumentException("No piece can make that move!");
        }
        Move currentMove = moveList.keySet().iterator().next();
        Piece[][] newBoard = moveList.get(currentMove);

        if (currentMove.getCheckmate()) {
            System.out.println("That's checkmate :)");
            // TODO: go to end game somehow?
        }
        else if(currentMove.getCheck()) {
            System.out.println("That's check :/");
        }

        boardStack.push(newBoard);
        whitesTurn=!whitesTurn;
    }

    // special case - see if castling is valid and if so, perform it
    private void attemptCastle(Move m) throws IllegalArgumentException {

        int kingCol=3;
        int kingRow=0;
        int rookCol=0;
        int f=-1;
        // if it's black's move
        if (!whitesTurn) {
            kingRow=7;
        }
        // if we are castling long
        if (!m.getCastleShort()) {
            rookCol=7;
            f=1;
        }
        Piece[][] cb = getBoard();
        Piece king = cb[kingRow][kingCol];
        Piece rook = cb[kingRow][rookCol];
        // if the king and rook are in their start positions and haven't moved
        if (king!=null && king instanceof King && !king.getHasMoved() && rook!=null && rook instanceof Rook && !rook.getHasMoved()) {

            // for all the squares inbetween the king and the rook
            for (int i=kingCol; i>0||i<7; i+=(1*f)) {
                if (isSquareThreatened(cb, i, kingRow, whitesTurn)) {
                    throw new IllegalArgumentException("Can't castle out of/through check!");
                }
                if (i==kingCol) {continue;}
                //if the square isn't empty
                if (cb[kingRow][i]!=null) {
                    throw new IllegalArgumentException("Can't castle, there are pieces in the way!");
                }
            }
        }

        Piece[][] newBoard = cloneBoard(cb);

        // move king
        newBoard[kingRow][kingCol+2*f]=newBoard[kingRow][kingCol];
        newBoard[kingRow][kingCol+2*f].setMoved(m);
        newBoard[kingRow][kingCol]=null;

        // move rook
        newBoard[kingRow][kingCol+f]=newBoard[kingRow][rookCol];
        newBoard[kingRow][kingCol+f].setMoved(m);
        newBoard[kingRow][rookCol]=null;
    }


    //
    // GAME LOGIC
    //

    // This function intakes a decoded pgn move as a 'Move' and returns a hashmap of all possible moves it could correspond to paired with
    // their 'outcome' boards i.e. the board after the move. 
    // If the hashmap is length 0, then there are no possible corresponding moves. If it is length>1, then the pgn instruction was ambiguous.
    // Note: This function needs to make the moves on copies of the board in order to assess the legality of the outcome board (and hence the 
    // move itself). This is why the function returns a hashmap with new boards - the work is being done anyway so we may as well save the 
    // outcome to avoid repeated computation.
    private HashMap<Move, Piece[][]> possibleMovesForDecodedPGN(Move pgn, Piece[][] board) {

        HashMap<Move, Piece[][]> out = new HashMap<Move, Piece[][]>();

        // for every square of the board
        for (int squareRow=0; squareRow<8; squareRow++) {
            for (int squareCol=0; squareCol<8; squareCol++) {
                // search columns and rows only that we need to given any disambig data present in the move
                if ( (pgn.getStartCol()==-1 || squareCol==pgn.getStartCol()) && (pgn.getStartRow()==-1 || squareRow==pgn.getStartRow()) ) {
                    Piece square = board[squareRow][squareCol];
                    // does the square have a piece on that is the correct colour and type?
                    if (square!=null && square.getType().equals(pgn.getPieceType()) && square.getWhite()==whitesTurn) {
                        // test the piece to see if it is in range of the target square
                        Move testMove = new Move(pgn, squareCol, squareRow);
                        if (isSquareInPieceMovingRange(board, squareCol, squareRow, testMove.getEndCol(), testMove.getEndRow())) {
                            // create the board after the move
                            Piece[][] boardAfterMove = performMove(testMove);
                            // check the move doesn't leave the player in check
                            if (checkChecker(boardAfterMove, whitesTurn)) {
                                continue;
                            }
                            // check the move correctly leaves the enemy in check/not in check as indicated in their PGN instruction
                            if ( checkmateChecker(boardAfterMove, !whitesTurn) == pgn.getCheckmate() ) {
                                continue;
                            }
                            // check the move correctly leaves the enemy in check/not in check as indicated in their PGN instruction
                            else if ( checkChecker(boardAfterMove, !whitesTurn) != pgn.getCheck() ) {
                                continue;
                            }
                            out.put(pgn, boardAfterMove);
                        }
                    }
                }
            }
        }
        return out;
    }

    // checks if a given square is in moving range for a given piece on a given board
    public boolean isSquareInPieceMovingRange(Piece[][] board, int pieceCol, int pieceRow, int squareCol, int squareRow) {

        Piece p = board[pieceRow][pieceCol];
        if (p==null) {throw new IllegalArgumentException("There is no valid piece on those square co-ordinates :(");}

        // if piece is a pawn - check the forward moves
        if (p instanceof Pawn) {
            if (squareCol==pieceCol) {
                int[][] moves = p.getMoves()[0];
                for (int i=0; i<moves.length; i++) {
                    Piece square = board[pieceRow+moves[i][1]][pieceCol];
                    if (square==null) {return true;}
                    else if (p.getHasMoved()) {
                        break;
                    }
                }
                return false;
            }
        }
        // any other piece
        return isSquareInPieceCaptureRange(board, pieceCol, pieceRow, squareCol, squareRow);
    }

    // checks if a given square is in the CAPTURE range of a piece for a given board
    private boolean isSquareInPieceCaptureRange(Piece[][] board, int pieceCol, int pieceRow, int squareCol, int squareRow) {

        Piece p = board[pieceRow][pieceCol];
        if (p==null) {throw new IllegalArgumentException("There is no valid piece on those square co-ordinates :(");}

        int[] moveVector = new int[] {pieceCol-squareCol, pieceRow-squareRow};

        int a=0;
        if (p instanceof Pawn) {a=1;}

        // search for a move vector match between the input move and the piece's move set
        int[][][] moves = p.getMoves();
        for (int i=a; i<moves.length; i++) {
            for (int j=0; j<moves[i].length; j++) {

                // we've found a match!
                if (moves[i][j][0]==moveVector[0] && moves[i][j][1]==moveVector[1]) {

                    // for every increment in the direction for which there was a vector match
                    for (int x=0; x<j; x++) {
                        // square we are checking
                        Piece square = board[pieceRow+moves[i][x][1]][pieceCol+moves[i][x][0]];
                        // if it's the target square
                        if (square==board[squareRow][squareCol]) {
                            return true;
                        }
                        // if it's a square inbetween
                        if (square!=null) {
                            return false;
                        }
                    }
                }
            }
        }
        // no vector match
        return false;
    }

    // is the square [row][col] threatened by the enemy (colour !white)?
    private boolean isSquareThreatened(Piece[][] board, int col, int row, boolean white) {
        // for every enemy piece
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                Piece p = board[i][j];
                if (p!=null && p.getWhite()!=white) {
                    if (isSquareInPieceCaptureRange(board, col, row, j, i)) {
                        return true;
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
        if (m.getCapture() && m.getPieceType()=="pawn" && out[m.getEndRow()][m.getEndCol()]==null && out[m.getStartRow()][m.getEndCol()] instanceof Pawn && ((Pawn)out[m.getStartRow()][m.getEndCol()]).getEnPassantable()) {
            out[m.getEndRow()][m.getEndCol()] = out[m.getStartRow()][m.getStartCol()];
            out[m.getStartRow()][m.getStartCol()] = null;
            out[m.getStartRow()][m.getEndCol()] = null;
        }
        /// otherwise
        else {
            out[m.getEndRow()][m.getEndCol()] = out[m.getStartRow()][m.getStartCol()];
            out[m.getStartRow()][m.getStartCol()] = null;
        }
        // set any flags necessary for 'has moved' and 'enpassantable'
        out[m.getEndRow()][m.getEndCol()].setMoved(m);

        return out;
    }

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    private boolean checkChecker(Piece[][] board, boolean whitesKing) {
        int[] kingCoords = getKingsCoords(board, whitesKing);
        return isSquareThreatened(board, kingCoords[0], kingCoords[1], whitesKing);
    }

    private boolean checkmateChecker(Piece[][] board, boolean whitesKing) {

        int[] kingCoords = getKingsCoords(board, whitesKing);
        int kingCol=kingCoords[0];
        int kingRow=kingCoords[1];

        for (int i=kingRow-1; i<=kingRow+1; i++) {
            for (int j=kingCol-1; j<=kingCol+1; j++) {

                if ( i<0 || i>7 || j<0 || j>7) {continue;}
                Piece square = board[i][j];
                // for the king's square, empty squares, or squares with enemy pieces on them...
                if ( (i==kingCol && j==kingRow) || square==null || square.getWhite()!=whitesKing) {
                    if (isSquareThreatened(board, j, i, whitesKing)) {
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private int[] getKingsCoords(Piece[][] board, boolean white) {
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                Piece square = board[row][col];
                if (square!=null && square instanceof King && square.getWhite()==white) {
                    return new int[] {col, row};
                }
            }
        }
        throw new IllegalArgumentException("I couldn't find a king on the board??! :S");
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

    // converts the last two chars in a string into square co-ordinates if they are valid inputs, returns {-1, -1} if invalid
    public int[] targetCoords(String c) {
        char ch1, ch2;
        try {
            ch1 = c.charAt(c.length()-2);
            ch2 = c.charAt(c.length()-1);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not enough chars in the string to extract info");
        }

        // are the destination values valid?
        if ( ((ch1>='a') && ( ch1<='h') ) && ((ch2>='1') && ( ch2<='8')) ) {
            int x = ch1-'a';
            int y = Character.getNumericValue(ch2)-1;
            return new int[]{x, y};
        }
        else {
            throw new IllegalArgumentException("Last two characters of string are not valid board coordinates");
        }
    }

    // TODO: Debug parser (command Qf2 is throwing error, decoding to incorrect end coordinates :L)
    // converts a command into a Move if possible
    public Move moveParser(String c) throws IllegalArgumentException {
        
        String pieceType;
        Integer startCol=null, startRow=null, endCol, endRow;
        boolean capture=false, check=false, checkmate=false;
        String promoPieceType=null;

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
            startCol=ch0-'a';
            // check if it's a pawn promo
            if (c.length()>3) {
                String temp = c.substring(c.length()-4);
                if ( temp.equals("8(Q)") || temp.equals("8(R)") || temp.equals("8(N)") || temp.equals("8(B)") ) {
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
        else {throw new IllegalArgumentException("Invalid piece type at command string index 0");}
        // as long as the command isn't (pawn move + length=2)
        if (!( (pieceType.equals("pawn")) && (c.length()==2) )) {
            c = c.substring(1);
        }
        // extract end position co-ordinates from the string
        int[] temp;
        try {
            temp=targetCoords(c);
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't extract target co-ordinates. Error message given: " + e.getMessage());
        }
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
            try {
                temp = targetCoords(c);
            } catch (Exception e) {
                throw new IllegalArgumentException("Couldn't extract disambiguation start square co-ordinates. Error message given: " + e.getMessage());
            }
            startCol=temp[0];
            startRow=temp[1];
        }
        else if (c.length()==1) {
            ch0 = c.charAt(0);
            if (ch0>='a' && ch0<='h'){
                startCol = c.charAt(0)-'a';
            }
            else if (ch0>='1' && ch0<='8') {
                startRow=Character.getNumericValue(ch0)-1;
            }
            else {
                throw new IllegalArgumentException("The one disambiguation character given was invalid.");
            }
        }
        else if (c.length()!=0){
            throw new IllegalArgumentException("Input invalid");
        }

        // return the move :)
        if (pieceType.equals("pawn")) {
            return new Move(endCol, endRow, capture, check, checkmate, promoPieceType);
        }
        return new Move(pieceType, startCol, startRow, endCol, endRow, capture, check, checkmate);
    }
}
