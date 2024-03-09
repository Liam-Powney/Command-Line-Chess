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
        super();
        this.boardStateStack = new Stack<BoardState>();
        this.undoStack = new Stack<BoardState>();
        boardStateStack.push(new BoardState());
    }
    public ChessGame(String in) {
        super();
        this.boardStateStack = new Stack<BoardState>();
        this.undoStack = new Stack<BoardState>();
        if (in.charAt(0)=='1') {
            boardStateStack.push(new BoardState());
            try {
                applyBulkPGNMoves(in);
            } catch (Exception e) {
                setPlayerMessage(e.getMessage());
            }
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
            if (result!=null) {throw new IllegalArgumentException("your string was illegal - game terminated at move " + (i/3)+1 + " with result: " + getResult());}
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

        BoardState boardStateAfterMove;

        if (m.getCastleShort()==null) {
            HashMap<Move, BoardState> possibleMoves = possibleMovesForDecodedPGN(cbs, m);
            if (possibleMoves.size()>1) {throw new IllegalArgumentException("More than one piece can move to that square! Please add disambiguation info");}
            else if (possibleMoves.size()==0) {throw new IllegalArgumentException("Your piece can't reach that square!");}
            m = possibleMoves.keySet().iterator().next();
            boardStateAfterMove = possibleMoves.get(m);
        }
        else {
            try {
                boardStateAfterMove = isMoveLegal(cbs, m);
            } catch (Exception e) {
                setPlayerMessage("Castling move failed. Reason given: " + e.getMessage());
                return;
            }
        }

        boardStateStack.push(boardStateAfterMove);
        cbs = boardStateStack.peek();

        // state checkers (check, checkmate, stalemate, halfmove count)
        if (checkmateChecker(cbs)) {
            if (!cbs.getWhitesTurn()) {result="white";}
            else {result="black";}
        }
        else if (checkChecker(cbs.getBoard(), cbs.getWhitesTurn())) {setPlayerMessage("Check!");}
        else if (cbs.getHalfMove()==100 || stalemateChecker(cbs)) {
            result="draw";
        }

        int repCounter=0;
        for (BoardState bs : boardStateStack) {
            if (isPositionEqual(bs)) {
                repCounter++;
                if (repCounter>=3) {result="draw"; break;}
            }
        }
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
    public HashMap<Move, BoardState> possibleMovesForDecodedPGN(BoardState bs, Move pgn) {

        HashMap<Move, BoardState> out = new HashMap<Move,  BoardState>();

        Piece[][] board = bs.getBoard();
        // for all of the correct coloured pieces
        for (int[] pieceCoords : getPieceCoords(bs.getBoard(), bs.getWhitesTurn())) {
            // if disamgbig data was given, does it match the piece coords?
            if (pgn.getStartCol()!=null && pieceCoords[0]!=pgn.getStartCol()) {continue;}
            if (pgn.getStartRow()!=null && pieceCoords[1]!=pgn.getStartRow()) {continue;}
            Piece p = board[pieceCoords[1]][pieceCoords[0]];
            // is the piece is of the correct type?
            if (pgn.getPieceType().equals("rook") && !(p instanceof Rook)) {continue;}
            else if (pgn.getPieceType().equals("knight") && !(p instanceof Knight)) {continue;}
            else if (pgn.getPieceType().equals("bishop") && !(p instanceof Bishop)) {continue;}
            else if (pgn.getPieceType().equals("queen") && !(p instanceof Queen)) {continue;}
            else if (pgn.getPieceType().equals("king") && !(p instanceof King)) {continue;}
            else if (pgn.getPieceType().equals("pawn") && !(p instanceof Pawn)) {continue;}
            // is the move legal for this piece?
            BoardState bsAfterMove;
            Move testMove = new Move(pgn, pieceCoords[0], pieceCoords[1]);
            try {
                bsAfterMove=isMoveLegal(bs, testMove);
            } catch (Exception e) {
                continue;
            }
            // ensure capture info is correctly indicated - if the target square is empty and the target square isn't the en passant square being moved to by a pawn 
            if (pgn.getCapture() && board[pgn.getEndRow()][pgn.getEndCol()]==null && !(p instanceof Pawn && Arrays.equals(bs.getEnPassantSquare(), new int[] {pgn.getEndCol(), pgn.getEndRow()}))) {continue;}
            // ensure check and checkmate info is correctly indicated
            boolean check=checkChecker(bsAfterMove.getBoard(), bsAfterMove.getWhitesTurn());
            boolean checkmate=checkmateChecker(bsAfterMove);
            if (!check && pgn.getCheck()) {continue;}
            if (!checkmate && pgn.getCheckmate()) {continue;}
            if (check && !checkmate && !pgn.getCheck()) {continue;}
            if (checkmate && !pgn.getCheckmate()) {continue;}

            out.put(testMove, bsAfterMove);
        }
        return out;
    }

    // Is a move legal for a given board state
    public BoardState isMoveLegal(BoardState bs, Move m) throws IllegalArgumentException {

        BoardState out;
        // normal piece moves
        if (m.getCastleShort()==null) {
            // check a piece of the correct type and colour is on the correct square
            Piece p = bs.getBoard()[m.getStartRow()][m.getStartCol()];
            if (p==null) {throw new IllegalArgumentException("isMoveLegal (specific piece square is null)");}
            if (p.getWhite()!=bs.getWhitesTurn()) {throw new IllegalArgumentException("isMoveLegal (piece specified is wrong colour)");}

            // can piece move to the specified square?
            if (!isSquareInPieceMoveRange(bs, m.getStartCol(), m.getStartRow(), m.getEndCol(), m.getEndRow())) {throw new IllegalArgumentException("isMoveLegal (piece can't reach that square)");}

            // player must promote a pawn
            if (bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Pawn) {
                if ( m.getPromoPiece()==null && (bs.getWhitesTurn() && m.getEndRow()==7) || (!bs.getWhitesTurn() && m.getEndRow()==0)) {
                    throw new IllegalArgumentException("Player must promote a pawn!");
                }
            }
        }
        // special case - castling
        else {
            if (bs.getWhitesTurn()) {
                if (m.getCastleShort() && !bs.getWCastleS()) {throw new IllegalArgumentException ("isMoveLegal (white has no short castling rights)");}
                if (!m.getCastleShort() && !bs.getWCastleL()) {throw new IllegalArgumentException ("isMoveLegal (white has no long castling rights)");}
            }
            else {
                if (m.getCastleShort() && !bs.getBCastleS()) {throw new IllegalArgumentException ("isMoveLegal (black has no short castling rights)");}
                if (!m.getCastleShort() && !bs.getBCastleL()) {throw new IllegalArgumentException ("isMoveLegal (black has no long castling rights)");}
            }

            // logic
            Piece[][] board = bs.getBoard();
            if (checkChecker(getBoard(), bs.getWhitesTurn())) {throw new IllegalArgumentException ("isMoveLegal (can't castle out of check)");}
            int kingCol=4, row=0, f=1;
            if (!bs.getWhitesTurn()) {row=7;}
            if (!m.getCastleShort()) {f=-1;}
            for (int c=kingCol+f; c>1&&c<7; c+=f) {
                if (board[row][c]!=null) {throw new IllegalArgumentException ("isMoveLegal (can't castle when pieces are in the way)");}
                if (isSquareUnderThreat(board, bs.getWhitesTurn(), c, row)) {throw new IllegalArgumentException ("isMoveLegal (can't castle through check)");}
            }
        }
        out=performMove(bs, m);
        if (m.getCastleShort()==null) {
            if (checkChecker(out.getBoard(), !out.getWhitesTurn())) {throw new IllegalArgumentException ("isMoveLegal (that move would leave the player in check)");}
        }

        return out;
    }

    // used to check whether a piece can actually move to a given square for a given boardstate. Used for checkmate/stalemate checking and
    // game logic
    // overloaded to take a boardstate
    public boolean isSquareInPieceMoveRange(BoardState bs, int pieceCol, int pieceRow, int squareCol, int squareRow) throws IllegalArgumentException {

        Piece p = bs.getBoard()[pieceRow][pieceCol];
        Piece endSquare = bs.getBoard()[squareRow][squareCol];
        // validate move info from the given boardstate
        if (p==null) {throw new IllegalArgumentException("isSquareInPieceMovingRange - start square is empty");}
        //if (p.getWhite()!=bs.getWhitesTurn()) {throw new IllegalArgumentException("isSquareInPieceMovingRange - piece specified is the wrong colour");}
        if (endSquare!=null && endSquare.getWhite()==p.getWhite()) {return false;} // target square has same colour piece on

        // if it's not a pawn move
        if (!(p instanceof Pawn)) { return isSquareInPieceThreatRange(bs.getBoard(), pieceCol, pieceRow, squareCol, squareRow);}
        // if it's a pawn move and it's a capture or en passant move
        if (p instanceof Pawn && (squareCol!=pieceCol && endSquare!=null && endSquare.getWhite()!=p.getWhite()) || (endSquare==null && bs.getEnPassantSquare()!=null && Arrays.equals(bs.getEnPassantSquare(), new int[] {squareCol, squareRow})) ) {return isSquareInPieceThreatRange(bs.getBoard(), pieceCol, pieceRow, squareCol, squareRow);}
        // pawn push
        if (p instanceof Pawn && pieceCol==squareCol) {
            // single push
            if (Math.abs(pieceRow-squareRow)==1) {
                if (endSquare==null) {return true;}
            }
            // double push
            else if (Math.abs(pieceRow-squareRow)==2) {
                if (p.getWhite() && pieceRow==1 && endSquare==null && bs.getBoard()[2][pieceCol]==null) {return true;}
                else if (!p.getWhite() && pieceRow==6 && endSquare==null && bs.getBoard()[5][pieceCol]==null) {return true;}
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

    public BoardState performMove(BoardState bs, Move m) {

        Piece[][] newBoard = cloneBoard(bs.getBoard());
        boolean wCastleS = bs.getWCastleS();
        boolean wCastleL = bs.getWCastleL();
        boolean bCastleS = bs.getBCastleS();
        boolean bCastleL = bs.getBCastleL();
        int[] enPassantSquare = null;
        int halfMove = bs.getHalfMove();
        int fullMove = bs.getFullMove();

        // normal move
        if (m.getCastleShort()==null) {
            Piece p = newBoard[m.getStartRow()][m.getStartCol()];
        
            // make the move
            newBoard[m.getEndRow()][m.getEndCol()] = p;
            newBoard[m.getStartRow()][m.getStartCol()] = null;
            // if it's an enpassant move
            if (p instanceof Pawn && m.getStartCol()!=m.getEndCol() && bs.getBoard()[m.getEndRow()][m.getEndCol()]==null && bs.getBoard()[m.getStartRow()][m.getEndCol()]!=null && bs.getBoard()[m.getStartRow()][m.getEndCol()] instanceof Pawn && p.getWhite()!=bs.getBoard()[m.getStartRow()][m.getEndCol()].getWhite()) {
                newBoard[m.getStartRow()][m.getEndCol()] = null;
            }
            // promote pawns
            if (m.getPromoPiece()!=null) {newBoard[m.getEndRow()][m.getEndCol()]=m.getPromoPiece();}
            // set enpassant square
            else if (bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Pawn && Math.abs(m.getEndRow()-m.getStartRow())==2) {
                if (bs.getWhitesTurn()) {enPassantSquare = new int[] {m.getEndCol(), m.getEndRow()-1};}
                else {enPassantSquare = new int[] {m.getEndCol(), m.getEndRow()+1};}
            }
            // set castling flags
            if (bs.getWhitesTurn() && m.getStartRow()==0 && bs.getBoard()[m.getStartRow()][m.getStartCol()].getWhite()==bs.getWhitesTurn()) {
                if (m.getStartCol()==0 && bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Rook) {wCastleS=false;}
                else if (m.getStartCol()==7 && bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Rook) {wCastleL=false;}
                else if(m.getStartCol()==4 && bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof King) {wCastleS=false; wCastleL=false;}
            }
            else if (!bs.getWhitesTurn() && m.getStartRow()==7 && bs.getBoard()[m.getStartRow()][m.getStartCol()].getWhite()==bs.getWhitesTurn()) {
                if (m.getStartCol()==0 && bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Rook) {bCastleS=false;}
                else if (m.getStartCol()==7 && bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Rook) {bCastleL=false;}
                else if(m.getStartCol()==4 && bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof King) {bCastleS=false; bCastleL=false;}
            }
            // halfmove logic
            if (m.getCapture() || bs.getBoard()[m.getStartRow()][m.getStartCol()] instanceof Pawn) {halfMove=0;}
            else {halfMove++;}
        }
        // castling move
        else {
            // logic for determining rook and king coords based off
            int kingCol=4, kingRow=0, rookCol=0, f=-1;
            if (!bs.getWhitesTurn()) { kingRow=7; }
            if (m.getCastleShort()) { rookCol=7; f=1; }
            // move pieces
            newBoard[kingRow][kingCol+2*f]=newBoard[kingRow][kingCol];
            newBoard[kingRow][kingCol]=null;
            newBoard[kingRow][kingCol+f]=newBoard[kingRow][rookCol];
            newBoard[kingRow][rookCol]=null;
            // set castling flags
            if (bs.getWhitesTurn()) {wCastleS=false; wCastleL=false;}
            else {bCastleS=false; bCastleL=false;}
            // halfmove logic
            halfMove++;
        }

        // fullmove logic 
        if (!bs.getWhitesTurn()) {fullMove++;}

        return new BoardState(newBoard, !bs.getWhitesTurn(), wCastleS, wCastleL, bCastleS, bCastleL, halfMove, fullMove, enPassantSquare);
    }

    //
    //  STATE CHECKERS (CHECK, CHECKMATE, STALEMATE)
    //

    // says whether the white or black king (depending on whiteCheck bool input) is in check for a given board
    public boolean checkChecker(Piece[][] board, boolean whitesKing) {
        int[] kingCoords = getKingsCoords(board, whitesKing);
        return isSquareUnderThreat(board, whitesKing, kingCoords[0], kingCoords[1]);
    }

    public boolean checkmateChecker(BoardState bs) {
        return (stalemateChecker(bs) && checkChecker(bs.getBoard(), bs.getWhitesTurn()));
    }

    public boolean stalemateChecker(BoardState bs) {
        for (int[] pieceCoords : getPieceCoords(bs.getBoard(), bs.getWhitesTurn())) {
            if (canPieceMove(bs, pieceCoords[0], pieceCoords[1])) {return false;}
        }
        return true;
    }

    public boolean canPieceMove(BoardState bs, int col, int row) {

        Piece p = bs.getBoard()[row][col];
        if (p==null) {throw new IllegalArgumentException("canPieceMove (no piece on given square)");}
        //if (p.getWhite()!=bs.getWhitesTurn()) {throw new IllegalArgumentException("canPieceMove (wrong piece colour on specified square)");}

        int[][][] moves = p.getMoves();
        for (int direction=0; direction<moves.length; direction++) {
            for (int increment=0; increment<moves[direction].length; increment++) {
                int endCol = col+moves[direction][increment][0];
                int endRow = row+moves[direction][increment][1];
                if ( endCol <0 || endCol>7 || endRow<0 || endRow>7 ) {break;}
                
                try {
                    BoardState testBS = isMoveLegal(bs, new Move(col, row, endCol, endRow));
                    return true;
                } catch (Exception e) {
                    if (bs.getBoard()[row+moves[direction][increment][1]][col+moves[direction][increment][0]]==null) {
                        continue;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public boolean isPositionEqual(BoardState bs) {
        Piece[][] board = bs.getBoard();
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                Piece s1 = board[r][c];
                Piece s2 = getBoard()[r][c];
                if (s1==null ^ s2==null) {return false;}
                if (!(s1==s2 || s1.equals(s2))) {return false;}
            }
        }
        return true;
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
        Piece promoPiece=null;

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
                    if (temp.charAt(2) == 'Q') {promoPiece = new Queen(getCBS().getWhitesTurn());}
                    else if (temp.charAt(2) == 'R') {promoPiece = new Rook(getCBS().getWhitesTurn());}
                    else if (temp.charAt(2) == 'N') {promoPiece = new Knight(getCBS().getWhitesTurn());}
                    else if (temp.charAt(2) == 'B') {promoPiece = new Bishop(getCBS().getWhitesTurn());}
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
            return new Move(endCol, endRow, startCol, capture, check, checkmate, promoPiece);
        }
        return new Move(pieceType, startCol, startRow, endCol, endRow, capture, check, checkmate);
    }
}
