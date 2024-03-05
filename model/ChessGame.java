package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class ChessGame extends GameState{
    
    private Stack<BoardState> boardStateStack;

    // constructor - set up the board and pieces
    public ChessGame() {
        this.boardStateStack = new Stack<BoardState>();
        boardStateStack.push(new BoardState());
    }
    public ChessGame(String in) {
        this.boardStateStack = new Stack<BoardState>();
        if (in.charAt(0)=='1') {
            boardStateStack.push(new BoardState());
            applyBulkPGNMoves(in);
        }
        else {
            createFENBoard(in);
        }
    }

    private void applyBulkPGNMoves(String pgn) throws IllegalArgumentException{

        // process input
        pgn.replaceAll("\\s+", " ");
        String[] pgnA = pgn.split(" ");

        for (int i=0; i<pgnA.length; i++) {
            if (i%3==0 && !(pgnA[i].equals(Integer.toString((i/3)+1) + "."))) {
                throw new IllegalArgumentException("your pgn was formatted incorrectly");
            }
            try {
                if (i%3!=0) {
                    attemptMove(pgnA[i]);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("your string contained an illegal move");
            }
        }
    }

    private void createFENBoard(String fenString) throws IllegalArgumentException{

        // what does the board look like?
        Piece[][] board = new Piece[8][8];
        String[] fenStringA=fenString.split(" ");
        if (fenStringA.length!=6) {throw new IllegalArgumentException();}

        String[] boardString = fenStringA[0].split("/");
        if (boardString.length!=8) {throw new IllegalArgumentException();}
        int row=7;
        for (String rank : boardString) {
            int col=0;
            for (int i=0; i<rank.length(); i++) {

                char c = boardString[-row+7].charAt(i);

                switch (c) {
                    case 'r':
                    board[row][col]=new Rook(false);
                    col++;
                        break;
                    case 'n':
                    board[row][col]=new Knight(false);
                    col++;
                        break;
                    case 'b':
                    board[row][col]=new Bishop(false);
                    col++;
                        break;
                    case 'k':
                    board[row][col]=new King(false);
                    col++;
                        break;
                    case 'q':
                    board[row][col]=new Queen(false);
                    col++;
                        break;
                    case 'p':
                    board[row][col]=new Pawn(false);
                    col++;
                        break;
                    case 'R':
                    board[row][col]=new Rook(true);
                    col++;
                        break;
                    case 'N':
                    board[row][col]=new Knight(true);
                    col++;
                        break;
                    case 'B':
                    board[row][col]=new Bishop(true);
                    col++;
                        break;
                    case 'K':
                    board[row][col]=new King(true);
                    col++;
                        break;
                    case 'Q':
                    board[row][col]=new Queen(true);
                    col++;
                        break;
                    case 'P':
                    board[row][col]=new Pawn(true);
                    col++;
                        break;
                    default:
                    int blank = c-'0';
                    if (blank>0 && blank<9) {
                        while (blank>0) {
                            col++;
                            blank--;
                        }
                    }
                    else {
                        throw new IllegalArgumentException();
                    }
                        break;
                }
            }
            row--;
        }

        // who's turn is it?
        boolean whitesTurn;
        if (fenStringA[1].equals("w")) {whitesTurn=true;}
        else if (fenStringA[1].equals("b")) {whitesTurn=false;}
        else {throw new IllegalArgumentException();}

        // who can castle where?
        if (fenStringA[2].length()>4||fenStringA[2].length()==0) {throw new IllegalArgumentException();}

        boolean wCastleS=false;
        boolean wCastleL=false;
        boolean bCastleS=false;
        boolean bCastleL=false;

        for (int i=0; i<fenStringA[2].length(); i++) {
            switch (fenStringA[2].charAt(i)) {
                case 'K':
                    if (wCastleS || wCastleL || bCastleS || bCastleL) {throw new IllegalArgumentException();}
                    wCastleS=true;
                    break;
                case 'Q':
                    if (wCastleL || bCastleS || bCastleL) {throw new IllegalArgumentException();}
                    wCastleL=true;
                    break;
                case 'k':
                    if (bCastleS || bCastleL) {throw new IllegalArgumentException();}
                    bCastleS=true;
                    break;
                case 'q':
                    if (bCastleL) {throw new IllegalArgumentException();}
                    bCastleL=true;
                    break;
            
                default:
                    throw new IllegalArgumentException();
            }
        }

        // any en passant flags?
        int[] enPassantSquare=null;
        if (fenStringA[3].length()>2) {throw new IllegalArgumentException();}
        else if (fenStringA[3].length()==1 && !(fenStringA[3].equals("-"))) {throw new IllegalArgumentException();} 
        else if ((fenStringA[3].length()==2)) {
            try {
                enPassantSquare = targetCoords(fenStringA[3]);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        //half move and full move clocks
        int halfMove;
        int fullMove;
        try {
            halfMove = Integer.valueOf(fenStringA[4]);
            fullMove = Integer.valueOf(fenStringA[5]);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        // push the new board state :)
        boardStateStack.push(new BoardState(board, whitesTurn, wCastleS, wCastleL, bCastleS, bCastleL, halfMove, fullMove, enPassantSquare));
        System.out.println(boardStateStack.peek().getWhitesTurn());
    }
    //
    // GETTERS AND SETTERS
    //
    public BoardState getCBS() {return boardStateStack.peek();}
    public Piece[][] getBoard() {return boardStateStack.peek().getBoard();}

    //
    // MOVE MAKER - this is the function the controller will call
    //
    
    public void attemptMove(String input) throws IllegalArgumentException {

        // get current board state to reference to later
        BoardState cbs = boardStateStack.peek();

        // parse the instruction
        Move m;
        try {
            m = moveParser(input);
        } catch (Exception e) {
            throw new IllegalArgumentException("That input is invalid. Reason give: " + e.getMessage());
        }

        // create variables for new board state
        Piece[][] newBoard = cloneBoard(getBoard());
        boolean wCastleS = cbs.getWCastleS();
        boolean wCastleL = cbs.getWCastleL();
        boolean bCastleS = cbs.getBCastleS();
        boolean bCastleL = cbs.getBCastleL();
        int[] enPassantSquare = null;
        int halfMove = cbs.getHalfMove();
        int fullMove = cbs.getFullMove();

        // castling special case
        if (m.getCastleShort()!=null) {

            if ( cbs.getWhitesTurn() && (m.getCastleShort()==cbs.getWCastleS() || m.getCastleShort()!=cbs.getWCastleL())) {throw new IllegalArgumentException("White has no casting priviledges!");}
            if (!cbs.getWhitesTurn() && (m.getCastleShort()==cbs.getBCastleS() || m.getCastleShort()!=cbs.getBCastleL())) {throw new IllegalArgumentException("Black has no casting priviledges!");}

            // logic for determining rook and king coords based off
            int kingCol=3, kingRow=0, rookCol=0, f=-1;
            if (!cbs.getWhitesTurn()) { kingRow=7; }
            if (!m.getCastleShort()) { rookCol=7; f=1; }

            // ensure the king is not in check and will not be castling through check, and the squares between the king and rook and clear
            for (int i=kingCol; i>0||i<7; i+=(1*f)) {
                if (isSquareThreatened(cbs.getBoard(), i, kingRow, cbs.getWhitesTurn())) {
                    throw new IllegalArgumentException("Can't castle out of/through check!");
                }
                if (i==kingCol) {continue;}
                //if the square isn't empty
                if (cbs.getBoard()[kingRow][i]!=null) {
                    throw new IllegalArgumentException("Can't castle, there are pieces in the way!");
                }
            }

            // move pieces
            newBoard[kingRow][kingCol+2*f]=newBoard[kingRow][kingCol];
            newBoard[kingRow][kingCol]=null;
            newBoard[kingRow][kingCol+f]=newBoard[kingRow][rookCol];
            newBoard[kingRow][rookCol]=null;

            // set castling rights flags on new boardstate
            if (cbs.getWhitesTurn())    {wCastleS=false; wCastleL=false;}
            else                        {bCastleS=false; bCastleL=false;}
        }
        // Non-castling moves - checking against game logic:
        else {
            HashMap<Move, Piece[][]> moveList = possibleMovesForDecodedPGN(m, cbs);
            if (moveList.size()>1) {
                throw new IllegalArgumentException("More than one piece can make that move!");
            }
            else if (moveList.size()==0) {
                throw new IllegalArgumentException("No piece can make that move!");
            }
            Move currentMove = moveList.keySet().iterator().next();
            newBoard = moveList.get(currentMove);
            // TODO: if the move was a pawn double move, set the en passant square
            // if it's a pawn push
            if (cbs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Pawn && !m.getCapture()) {
                // if the pawn is white and went from rank 2 to 4, set enPassant square
                if (cbs.getBoard()[m.getStartRow()][m.getStartCol()].getWhite() && m.getEndRow()==3 && m.getStartRow()==1) {
                    enPassantSquare= new int[] {m.getEndRow()-1, m.getEndCol()};
                }
                // if the pawn is black and pushed from rank 7 to 5, set enPassant square
                else if (!(cbs.getBoard()[m.getStartRow()][m.getStartCol()].getWhite()) && m.getEndRow()==4 && m.getStartRow()==6) {
                    enPassantSquare= new int[] {m.getEndRow()+1, m.getEndCol()};
                }
            }
        }

        // Castling flags logic - no need to check for piece colour as this has been done during game logic above
        if (cbs.getWhitesTurn() && m.getStartRow()==0) {
            if (m.getStartCol()==0 && m.getPieceType().equals("rook")) {wCastleS=false;}
            else if (m.getStartCol()==7 && m.getPieceType().equals("rook")) {wCastleL=false;}
            else if(m.getStartCol()==4 && m.getPieceType().equals("king")) {wCastleS=false; wCastleL=false;}
        }
        if (!cbs.getWhitesTurn() && m.getStartRow()==7) {
            if (m.getStartCol()==0 && m.getPieceType().equals("rook")) {bCastleS=false;}
            else if (m.getStartCol()==7 && m.getPieceType().equals("rook")) {bCastleL=false;}
            else if(m.getStartCol()==4 && m.getPieceType().equals("king")) {bCastleS=false; bCastleL=false;}
        }

        if (m.getCapture() || m.getPieceType().equals("pawn")) {halfMove=0;}
        else {halfMove++;}
        if (halfMove==100) {}//TODO: It's a draw :)
        // fullmove logic 
        if (!cbs.getWhitesTurn()) {fullMove++;}
        
        if (m.getCheckmate()) {
            System.out.println("That's checkmate :)");
            // TODO End the game
        }
        else if(m.getCheck()) {
            System.out.println("That's check :/");
        }

        boardStateStack.push(new BoardState(newBoard, !cbs.getWhitesTurn(), wCastleS, wCastleL, bCastleS, bCastleL, halfMove, fullMove, enPassantSquare));
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
    public HashMap<Move, Piece[][]> possibleMovesForDecodedPGN(Move pgn, BoardState bs) {

        HashMap<Move, Piece[][]> out = new HashMap<Move, Piece[][]>();

        // for every square of the board
        for (int squareRow=0; squareRow<8; squareRow++) {
            for (int squareCol=0; squareCol<8; squareCol++) {
                // search columns and rows only that we need to given any disambig data present in the move
                if ( (pgn.getStartCol()==null || squareCol==pgn.getStartCol()) && (pgn.getStartRow()==null || squareRow==pgn.getStartRow()) ) {
                    Piece square = bs.getBoard()[squareRow][squareCol];
                    // does the square have a piece on that is the correct colour and type?
                    if (square!=null && square.getType().equals(pgn.getPieceType()) && square.getWhite()==bs.getWhitesTurn()) {
                        // test the piece to see if it is in range of the target square
                        Move testMove = new Move(pgn, squareCol, squareRow);
                        if (isSquareInPieceMovingRange(bs.getBoard(), squareCol, squareRow, testMove.getEndCol(), testMove.getEndRow())) {
                            // create the board after the move
                            Piece[][] boardAfterMove = performMove(testMove);
                            // check the move doesn't leave the player in check
                            if (checkChecker(boardAfterMove, bs.getWhitesTurn())) {
                                continue;
                            }
                            // check the move correctly leaves the enemy in check/not in check as indicated in their PGN instruction
                            if ( checkmateChecker(boardAfterMove, !bs.getWhitesTurn()) == pgn.getCheckmate() ) {
                                continue;
                            }
                            // check the move correctly leaves the enemy in check/not in check as indicated in their PGN instruction
                            else if ( checkChecker(boardAfterMove, !bs.getWhitesTurn()) != pgn.getCheck() ) {
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

        // if piece is a pawn and it is moving forwards then check the forward square(s)
        if (p instanceof Pawn && squareCol==pieceCol) {
            int[][] moves = p.getMoves()[0];

            // determine whether the pawn can do a double move -  done by setting the upper limit of index through the moves array for the forwards direction 
            int limit=1;
            if ( (p.getWhite() && pieceRow==1) || (!p.getWhite() && pieceRow==6)) {
                limit=2;
            }

            for (int i=0; i<limit; i++) {
                Piece square = board[pieceRow+moves[i][1]][pieceCol+moves[i][0]];
                if (square!=null) {return false;}
                if (square==board[squareRow][squareCol]) {
                    return true;
                }
            }
            return false;
        }
        // any other piece
        return isSquareInPieceCaptureRange(board, pieceCol, pieceRow, squareCol, squareRow);
    }

    // checks if a given square is in the CAPTURE range of a piece for a given board
    public boolean isSquareInPieceCaptureRange(Piece[][] board, int pieceCol, int pieceRow, int squareCol, int squareRow) {

        Piece p = board[pieceRow][pieceCol];
        if (p==null) {throw new IllegalArgumentException("There is no valid piece on those square co-ordinates :(");}

        int[] moveVector = new int[] {squareCol-pieceCol, squareRow-pieceRow};

        int a=0;
        if (p instanceof Pawn) {a=1;}

        // search for a move vector match between the input move and the piece's move set
        int[][][] moves = p.getMoves();
        for (int i=a; i<moves.length; i++) {
            for (int j=0; j<moves[i].length; j++) {

                // we've found a match!
                if (moves[i][j][0]==moveVector[0] && moves[i][j][1]==moveVector[1]) {

                    // for every increment in the direction for which there was a vector match
                    for (int x=0; x<j+1; x++) {
                        // square we are checking
                        Piece square = board[pieceRow+moves[i][x][1]][pieceCol+moves[i][x][0]];
                        // if it's the target square
                        if (square==board[squareRow][squareCol] && (square==null || square.getWhite()!=p.getWhite())) {
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
    public boolean isSquareThreatened(Piece[][] board, int c, int r, boolean white) {
        // for every enemy piece
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                Piece p = board[row][col];
                if (p!=null && p.getWhite()!=white) {
                    if (isSquareInPieceCaptureRange(board, col, row, c, r)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // performs move m on current board and returns a new Piece[][] of the board of the resulting state
    public Piece[][] performMove(Move m) {

        // clone board for the output 
        Piece[][] out = cloneBoard(getBoard());
        
        // move the pieces
        /// if it's an enpassant move
        if (m.getCapture() && m.getPieceType()=="pawn" && out[m.getEndRow()][m.getEndCol()]==null && Arrays.equals(boardStateStack.peek().getEnPassantSquare(), new int[] {m.getEndCol(), m.getStartRow()})) {
            out[m.getEndRow()][m.getEndCol()] = out[m.getStartRow()][m.getStartCol()];
            out[m.getStartRow()][m.getStartCol()] = null;
            out[m.getStartRow()][m.getEndCol()] = null;
        }
        /// otherwise
        else {
            out[m.getEndRow()][m.getEndCol()] = out[m.getStartRow()][m.getStartCol()];
            out[m.getStartRow()][m.getStartCol()] = null;
        }

        return out;
    }

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    public boolean checkChecker(Piece[][] board, boolean whitesKing) {
        int[] kingCoords = getKingsCoords(board, whitesKing);
        return isSquareThreatened(board, kingCoords[0], kingCoords[1], whitesKing);
    }

    public boolean checkmateChecker(Piece[][] board, boolean whitesKing) {

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

    public int[] getKingsCoords(Piece[][] board, boolean white) {
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
    public Piece[][] cloneBoard(Piece[][] board) {
        Piece[][] out = new Piece[8][8];
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                Piece square = board[i][j];
                if      (square instanceof Pawn)    {out[i][j]= new Pawn((Pawn)square);}
                else if (square instanceof Rook)    {out[i][j]= new Rook((Rook)square);}
                else if (square instanceof Knight)  {out[i][j]= new Knight((Knight)square);}
                else if (square instanceof Bishop)  {out[i][j]= new Bishop((Bishop)square);}
                else if (square instanceof King)    {out[i][j]= new King((King)square);}
                else if (square instanceof Queen)   {out[i][j]= new Queen((Queen)square);}
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

    // converts a command into a Move if possible
    public Move moveParser(String c) throws IllegalArgumentException {
        
        String pieceType=null;
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
            startCol=(Integer)(ch0-'a');
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
        else if (c.length()!=0) {
            throw new IllegalArgumentException("Input invalid");
        }

        // return the move :)
        if (pieceType.equals("pawn")) {
            return new Move(endCol, endRow, startCol, capture, check, checkmate, promoPieceType);
        }
        return new Move(pieceType, startCol, startRow, endCol, endRow, capture, check, checkmate);
    }
}
