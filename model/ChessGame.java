package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class ChessGame extends GameState{
    
    private Stack<BoardState> boardStateStack;
    private Stack<BoardState> undoStack;
    private String result;

    //
    // CONSTRUCTORS
    // 
    public ChessGame() {
        this.boardStateStack = new Stack<BoardState>();
        this.undoStack = new Stack<BoardState>();
        boardStateStack.push(new BoardState());
    }
    public ChessGame(String in) {
        this.boardStateStack = new Stack<BoardState>();
        this.undoStack = new Stack<BoardState>();
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
            if (i%3==0 && !(pgnA[i].equals(Integer.toString((i/3)+1) + "."))) {throw new IllegalArgumentException("your pgn was formatted incorrectly");}
            try {if (i%3!=0) {attemptMove(pgnA[i]);}}
            catch (Exception e) {throw new IllegalArgumentException("your string contained the illegal move " + pgnA[i]);}
        }
    }

    private void createFENBoard(String fenString) throws IllegalArgumentException{

        // what does the board look like?
        Piece[][] board = new Piece[8][8];
        String[] fenStringA=fenString.split(" ");
        if (fenStringA.length!=6) {throw new IllegalArgumentException("Your fen string is formatted incorrectly");}

        String[] boardString = fenStringA[0].split("/");
        if (boardString.length!=8) {throw new IllegalArgumentException("Your fen piece layout is formatted incorrectly");}
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
                        throw new IllegalArgumentException("Board layout string contains illegal character '" + c + "'");
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
        else {throw new IllegalArgumentException("Fen string contains illegal character at position 2. Expected 'w' or 'b', recieved '" + fenStringA[1] + "'");}

        // who can castle where?
        if (fenStringA[2].length()>4||fenStringA[2].length()==0) {throw new IllegalArgumentException("Castling rights string formatted incorrectly");}

        boolean wCastleS=false;
        boolean wCastleL=false;
        boolean bCastleS=false;
        boolean bCastleL=false;

        for (int i=0; i<fenStringA[2].length(); i++) {
            switch (fenStringA[2].charAt(i)) {
                case 'K':
                    if (wCastleS || wCastleL || bCastleS || bCastleL) {throw new IllegalArgumentException("Ensure castling rights string is in correct order (KQkq)");}
                    wCastleS=true;
                    break;
                case 'Q':
                    if (wCastleL || bCastleS || bCastleL) {throw new IllegalArgumentException("Ensure castling rights string is in correct order (KQkq)");}
                    wCastleL=true;
                    break;
                case 'k':
                    if (bCastleS || bCastleL) {throw new IllegalArgumentException("Ensure castling rights string is in correct order (KQkq)");}
                    bCastleS=true;
                    break;
                case 'q':
                    if (bCastleL) {throw new IllegalArgumentException("Ensure castling rights string is in correct order (KQkq)");}
                    bCastleL=true;
                    break;
                case '-':
                    if ( i!=0 || fenStringA[2].length()>1) {throw new IllegalArgumentException("Illegal castling rights string (char '-' can only appear alone)");}
                    continue;
                default:
                    throw new IllegalArgumentException("Castling rights string contained illegal character '" + fenStringA[2].charAt(i) + "'");
            }
        }

        // any en passant flags?
        int[] enPassantSquare=null;
        if (fenStringA[3].length()>2) {throw new IllegalArgumentException("En Passant square string is formatted incorrectly");}
        else if (fenStringA[3].length()==1 && !(fenStringA[3].equals("-"))) {throw new IllegalArgumentException("En Passant square string is formatted incorrectly");} 
        else if ((fenStringA[3].length()==2)) {
            try {
                enPassantSquare = targetCoords(fenStringA[3]);
            } catch (Exception e) {
                throw new IllegalArgumentException("En Passant square string is formatted incorrectly. Target coords error: " + e.getMessage());
            }
        }

        //half move and full move clocks
        int halfMove;
        int fullMove;
        try {
            halfMove = Integer.valueOf(fenStringA[4]);
            fullMove = Integer.valueOf(fenStringA[5]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Half-move/Full-move info formatted incorrectly");
        }

        // push the new board state :)
        boardStateStack.push(new BoardState(board, whitesTurn, wCastleS, wCastleL, bCastleS, bCastleL, halfMove, fullMove, enPassantSquare));
    }


    //
    // GETTERS AND SETTERS
    //
    public BoardState getCBS() {return boardStateStack.peek();}
    public Piece[][] getBoard() {return boardStateStack.peek().getBoard();}
    public String getResult() {return result;}

    public ArrayList<int[]> getPieceCoords(Piece[][] board, boolean white) {
        ArrayList<int[]> out = new ArrayList<int[]>();
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                if (board[r][c]!=null && board[r][c].getWhite()==white) {out.add(new int[] {c, r});}
            }
        }
        return out;
    }

    public int[] getKingsCoords(Piece[][] board, boolean kingColour) {
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                Piece square = board[row][col];
                if (square!=null && square instanceof King && square.getWhite()==kingColour) {
                    return new int[] {col, row};
                }
            }
        }
        throw new IllegalArgumentException("I couldn't find a king on the board??! :S");
    }

    public void undo() {
        if (boardStateStack.size()<2) {return;}
        undoStack.push(boardStateStack.peek());
        boardStateStack.pop();
    }

    public void redo() {
        if (undoStack.size()<1) {return;}
        boardStateStack.push(undoStack.peek());
        undoStack.pop();
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
    // ATTEMPT MOVE FROM COMMAND
    //
    
    public void attemptMove(String input) throws IllegalArgumentException {

        // get current board state to reference to later
        BoardState cbs = boardStateStack.peek();

        // parse the instruction
        Move m;
        try {m = moveParser(input);} 
        catch (Exception e) {throw new IllegalArgumentException("That input is invalid. Reason give: " + e.getMessage());}

        // create variables for new board state
        Piece[][] newBoard;
        boolean wCastleS = cbs.getWCastleS();
        boolean wCastleL = cbs.getWCastleL();
        boolean bCastleS = cbs.getBCastleS();
        boolean bCastleL = cbs.getBCastleL();
        int[] enPassantSquare = null;
        int halfMove = cbs.getHalfMove();
        int fullMove = cbs.getFullMove();

        // check that the move is legal and only relates to one possible move for non-castling moves
        if (m.getCastleShort()==null) {
            HashMap<Move, Piece[][]> moveList = possibleMovesForDecodedPGN(cbs, m);
            if (moveList.size()>1) {
                throw new IllegalArgumentException("More than one piece can make that move!");
            }
            else if (moveList.size()==0) {
                throw new IllegalArgumentException("No piece can make that move!");
            }
            m = moveList.keySet().iterator().next();
            newBoard = moveList.get(m);
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
            // set enpassant square
            if (m.getPieceType().equals("pawn") && Math.abs(m.getEndRow()-m.getStartRow())==2) {
                if (cbs.getWhitesTurn()) {enPassantSquare= new int[] {m.getEndCol(), m.getEndRow()-1};}
                else {enPassantSquare= new int[] {m.getEndCol(), m.getEndRow()+1};}
            }
        }
        else {
            // check if the castling move is legal
            try {newBoard=isMoveLegal(cbs, m.getCastleShort());} 
            catch (Exception e) {throw new IllegalArgumentException("Castle attemp illegal. Reason given: " + e.getMessage());}

            // set castling rights flags and enPassant square on new boardstate
            if (cbs.getWhitesTurn())    {wCastleS=false; wCastleL=false;}
            else                        {bCastleS=false; bCastleL=false;}
            enPassantSquare=null;
        }

        // halfmove logic
        if ( (m.getCastleShort()==null && m.getCapture() ) || (m.getPieceType()!=null && m.getPieceType().equals("pawn"))) {halfMove=0;}
        else {halfMove++;}

        // fullmove logic 
        if (!cbs.getWhitesTurn()) {fullMove++;}

        boardStateStack.push(new BoardState(newBoard, !cbs.getWhitesTurn(), wCastleS, wCastleL, bCastleS, bCastleL, halfMove, fullMove, enPassantSquare));
        undoStack.clear();
        cbs = boardStateStack.peek();

        // state checkers (check, checkmate, stalemate, halfmove count)
        if (checkmateChecker(cbs.getBoard(), cbs.getWhitesTurn(), cbs.getEnPassantSquare())) {
            if (cbs.getWhitesTurn()) {result="white";}
            else {result="black";}
            System.out.println("Checkmate!");
        }
        else if (checkChecker(cbs.getBoard(), cbs.getWhitesTurn())) {System.out.println("Check!");}
        else if (halfMove==100 || stalemateChecker(cbs.getBoard(), cbs.getWhitesTurn(), cbs.getEnPassantSquare())) {
            result="draw";
            System.out.println("Draw!");}
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
    public HashMap<Move, Piece[][]> possibleMovesForDecodedPGN(BoardState bs, Move pgn) {

        HashMap<Move, Piece[][]> out = new HashMap<Move, Piece[][]>();

        Piece[][] board = bs.getBoard();
        // for all of the correct coloured pieces
        for (int[] pieceCoords : getPieceCoords(bs.getBoard(), bs.getWhitesTurn())) {
            Piece p = board[pieceCoords[1]][pieceCoords[0]];
            // if the piece is the correct type and colour
            if (p.getType()==pgn.getPieceType() && p.getWhite()==bs.getWhitesTurn()) {
                // discern disamgib data
                if (pgn.getStartCol()!=null && pieceCoords[0]!=pgn.getStartCol()) {continue;}
                if (pgn.getStartRow()!=null && pieceCoords[1]!=pgn.getStartRow()) {continue;}
                // is the move legal for this piece?
                Piece[][] boardAfterMove;
                try {
                    boardAfterMove=isMoveLegal(bs.getBoard(), bs.getWhitesTurn(), pieceCoords[0], pieceCoords[1], pgn.getEndCol(), pgn.getEndRow(), bs.getEnPassantSquare());
                } catch (Exception e) {
                    continue;
                }
                // ensure the move is corerctly labelled with capture, check, and checkmate info
                // if the pgn indicates the move is a capture but the target square is empty and the target square isn't the en passant square being moved to by a pawn 
                if (pgn.getCapture() && board[pgn.getEndRow()][pgn.getEndCol()]==null && !(p instanceof Pawn && Arrays.equals(bs.getEnPassantSquare(), new int[] {pgn.getEndCol(), pgn.getEndRow()}))) {continue;}
                
                // set enpassant square
                int[] newEPS=null;
                if (pgn.getPieceType().equals("pawn") && Math.abs(pgn.getEndRow()-pieceCoords[1])==2) {
                    if (bs.getWhitesTurn()) {newEPS= new int[] {pgn.getEndCol(), pgn.getEndRow()-1};}
                    else {newEPS= new int[] {pgn.getEndCol(), pgn.getEndRow()+1};}
                }

                boolean check=checkChecker(boardAfterMove, !bs.getWhitesTurn());
                boolean checkmate=checkmateChecker(boardAfterMove, !bs.getWhitesTurn(), newEPS);
                if (!check && pgn.getCheck()) {continue;}
                if (!checkmate && pgn.getCheckmate()) {continue;}
                if (check && !checkmate && !pgn.getCheck()) {continue;}
                if (checkmate && !pgn.getCheckmate()) {continue;}

                out.put(new Move(pgn, pieceCoords[0], pieceCoords[1]), boardAfterMove);
            }
        }
        return out;
    }

    // Intakes a boardstate and a move, throws an error if the move is illegal, returns the resulting board if the move is legal
    public Piece[][] isMoveLegal(Piece[][] board, boolean white, int pieceCol, int pieceRow, int squareCol, int squareRow, int[] enPassantSquare) throws IllegalArgumentException {

        // check a piece of the correct type and colour is on the correct square
        Piece p = board[pieceRow][pieceCol];
        if (p==null) {throw new IllegalArgumentException("isMoveLegal (specific piece square is null)");}
        if (p.getWhite()!=white) {throw new IllegalArgumentException("isMoveLegal (piece specified is wrong colour)");}

        // can piece move to the specified square?
        if (!isSquareInPieceMoveRange(board, pieceCol, pieceRow, squareCol, squareRow, enPassantSquare)) {throw new IllegalArgumentException("isMoveLegal (piece can't reach that square)");}

        // will the move leave the player in check?
        Piece[][] out=performMove(board, pieceCol, pieceRow, squareCol, squareRow);
        if (checkChecker(out, p.getWhite())) {throw new IllegalArgumentException ("isMoveLegal (that move would leave the player in check)");}

        return out;
    }

    // overloaded function for castling moves
    public Piece[][] isMoveLegal(BoardState bs, boolean castleShort) throws IllegalArgumentException {

        // sanity checker
        if (bs.getWhitesTurn()) {
            if (castleShort && !bs.getWCastleS()) {throw new IllegalArgumentException ("isMoveLegal (white has no short castling rights)");}
            if (!castleShort && !bs.getWCastleL()) {throw new IllegalArgumentException ("isMoveLegal (white has no long castling rights)");}
        }
        else {
            if (castleShort && !bs.getBCastleS()) {throw new IllegalArgumentException ("isMoveLegal (black has no short castling rights)");}
            if (!castleShort && !bs.getBCastleL()) {throw new IllegalArgumentException ("isMoveLegal (black has no long castling rights)");}
        }

        // logic
        Piece[][]board = bs.getBoard();
        if (checkChecker(getBoard(), bs.getWhitesTurn())) {throw new IllegalArgumentException ("isMoveLegal (can't castle out of check)");}
        int kingCol=4, row=0, f=1;
        if (!bs.getWhitesTurn()) {row=7;}
        if (!castleShort) {f=-1;}
        for (int c=kingCol+f; c>1&&c<7; c+=f) {
            if (board[row][c]!=null) {throw new IllegalArgumentException ("isMoveLegal (can't castle when pieces are in the way)");}
            if (isSquareUnderThreat(board, bs.getWhitesTurn(), c, row)) {throw new IllegalArgumentException ("isMoveLegal (can't castle through check)");}
        }

        return performMove(board, bs.getWhitesTurn(), castleShort);
    }

    // used to check whether a piece can actually move to a given square for a given boardstate. Used for checkmate/stalemate checking and
    // game logic
    public boolean isSquareInPieceMoveRange(Piece[][] board, int pieceCol, int pieceRow, int squareCol, int squareRow, int[] enPassantSquare) throws IllegalArgumentException {

        Piece p = board[pieceRow][pieceCol];
        Piece endSquare = board[squareRow][squareCol];
        // validate move info from the given boardstate
        if (p==null) {throw new IllegalArgumentException("isSquareInPieceMovingRange - start square is empty");}
        //if (p.getWhite()!=bs.getWhitesTurn()) {throw new IllegalArgumentException("isSquareInPieceMovingRange - piece specified is the wrong colour");}
        if (endSquare!=null && endSquare.getWhite()==p.getWhite()) {return false;} // target square has same colour piece on

        // if it's not a pawn move
        if (!(p instanceof Pawn)) { return isSquareInPieceThreatRange(board, pieceCol, pieceRow, squareCol, squareRow);}
        // if it's a pawn move and it's a capture or en passant move
        if (p instanceof Pawn && (squareCol!=pieceCol && endSquare!=null && endSquare.getWhite()!=p.getWhite()) || (endSquare==null && enPassantSquare!=null && Arrays.equals(enPassantSquare, new int[] {squareCol, squareRow})) ) {return isSquareInPieceThreatRange(board, pieceCol, pieceRow, squareCol, squareRow);}
        // pawn push
        if (p instanceof Pawn && pieceCol==squareCol) {
            // single push
            if (Math.abs(pieceRow-squareRow)==1) {
                if (endSquare==null) {return true;}
            }
            // double push
            else if (Math.abs(pieceRow-squareRow)==2) {
                if (p.getWhite() && pieceRow==1 && endSquare==null && board[2][pieceCol]==null) {return true;}
                else if (!p.getWhite() && pieceRow==6 && endSquare==null && board[5][pieceCol]==null) {return true;}
            }
            else {return false;}
        }
        return false;
    }

    // returns true only if a piece is threatening a given square. i.e. a pawn is always threatening it's diagnal directions regardless of whether a 
    // enemy piece is present. Used for checking check etc.
    public boolean isSquareInPieceThreatRange(Piece[][] board, int pieceCol, int pieceRow, int squareCol, int squareRow) throws IllegalArgumentException {

        Piece p = board[pieceRow][pieceCol];
        Piece endSquare = board[squareRow][squareCol];
        // validate move info from the given boardstate
        if (p==null) {throw new IllegalArgumentException("isSquareInPieceThreatRange - start square is empty");}
        if (endSquare!=null && endSquare.getWhite()==p.getWhite()) {return false;}

        int startLim=0;
        if (p instanceof Pawn) {startLim=1;}
        int[][][] moves=p.getMoves();
        int[] moveVec = new int[] {squareCol-pieceCol, squareRow-pieceRow};

        for (int direction=startLim; direction<moves.length; direction++) {
            for (int increment=0; increment<moves[direction].length; increment++) {

                if (Arrays.equals(moves[direction][increment], moveVec)) {

                    for (int i=0; i<increment; i++) {
                        Piece cs = board[pieceRow+moves[direction][i][1]][pieceCol+moves[direction][i][0]];
                        if (cs!=null) {return false;}
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSquareUnderThreat(Piece[][] board, boolean whitesTurn, int col, int row) {
        for ( int[] pieceCoords : getPieceCoords(board, !whitesTurn) ) {
            if (isSquareInPieceThreatRange(board, pieceCoords[0], pieceCoords[1], col, row)) {return true;}
        }
        return false;
    }

    //
    // MOVER MAKERS
    //

    // performs move on current board and returns a new Piece[][] of the board of the resulting state.
    // This does not check for move legality!
    public Piece[][] performMove(Piece[][] board, int pieceCol, int pieceRow, int squareCol, int squareRow) {

        Piece[][] out = cloneBoard(board);
        Piece p = out[pieceRow][pieceCol];
        
        // make the move
        out[squareRow][squareCol] = out[pieceRow][pieceCol];
        out[pieceRow][pieceCol] = null;
        // if it's an enpassant move
        if (p instanceof Pawn && pieceCol!=squareCol && board[squareRow][squareCol]==null && board[pieceRow][squareCol]!=null && board[pieceRow][squareCol] instanceof Pawn && p.getWhite()!=board[pieceRow][squareCol].getWhite()) {
            out[pieceRow][squareCol] = null;
        }
        return out;
    }

    // overloaded function for castling. This does not check for move legality!
    public Piece[][] performMove(Piece[][] board, boolean white, boolean castleShort) {
        // logic for determining rook and king coords based off
        int kingCol=4, kingRow=0, rookCol=0, f=-1;
        if (!white) { kingRow=7; }
        if (castleShort) { rookCol=7; f=1; }
        // move pieces
        Piece[][] out = cloneBoard(board);
        out[kingRow][kingCol+2*f]=out[kingRow][kingCol];
        out[kingRow][kingCol]=null;
        out[kingRow][kingCol+f]=out[kingRow][rookCol];
        out[kingRow][rookCol]=null;

        return out;
    }

    //
    //  STATE CHECKERS (CHECK, CHECKMATE, STALEMATE)
    //

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    public boolean checkChecker(Piece[][] board, boolean whitesKing) {
        int[] kingCoords = getKingsCoords(board, whitesKing);
        return isSquareUnderThreat(board, whitesKing, kingCoords[0], kingCoords[1]);
    }

    public boolean checkmateChecker(Piece[][] board, boolean white, int[] enPassantSquare) {
        return (stalemateChecker(board, white, enPassantSquare) && checkChecker(board, white));
    }

    public boolean stalemateChecker(Piece[][] board, boolean white, int[] enPassantSquare) {
        for (int[] pieceCoords : getPieceCoords(board, white)) {
            if (canPieceMove(board, pieceCoords[0], pieceCoords[1], enPassantSquare)) {return false;}
        }
        // TODO can player castle? Does this need to be checked for? If player can castle then king must also be able to
        // move 1 square in direction of castling, hence no stalemate
        return true;
    }

    public boolean canPieceMove(Piece[][] board, int col, int row, int[] enPassantSquare) {

        Piece p = board[row][col];
        if (p==null) {throw new IllegalArgumentException("canPieceMove (no piece on given square)");}
        //if (p.getWhite()!=bs.getWhitesTurn()) {throw new IllegalArgumentException("canPieceMove (wrong piece colour on specified square)");}

        int[][][] moves = p.getMoves();
        for (int direction=0; direction<moves.length; direction++) {
            for (int increment=0; increment<moves[direction].length; increment++) {
                int endCol = col+moves[direction][increment][0];
                int endRow = row+moves[direction][increment][1];
                if ( endCol <0 || endCol>7 || endRow<0 || endRow>7 ) {break;}
                Piece[][] testBoard;
                try {
                    testBoard=isMoveLegal(board, p.getWhite(), col, row, col+moves[direction][increment][0], row+moves[direction][increment][1], enPassantSquare);
                    return true;
                } catch (Exception e) {
                    if (board[row+moves[direction][increment][1]][col+moves[direction][increment][0]]==null) {
                        continue;
                    }
                    break;
                }
            }
        }
        return false;
    }

    //
    // PGN COMMAND PARSER
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
        if (c.equals("O-O-O")) {return new Move(false, check, checkmate);}
        else if (c.equals("O-O")) {return new Move(true, check, checkmate);}

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
