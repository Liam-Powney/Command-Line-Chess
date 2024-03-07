package test;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import model.BoardState;
import model.ChessGame;
import model.Move;
import model.Piece;


public class ChessGameTest {
    
    ChessGame cg = new ChessGame();
    BoardState cgCBS = cg.getCBS();

    @Test
    public void testTargetCoords() {
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords(""), "Empty string doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("a9"), "\"a9\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("i2"), "\"i2\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("ab"), "\"ab\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("34"), "\"34\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("xe"), "E\"xe\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("5x"),  "\"5x\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("e35x"), "\"e35x\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("'sa/"), "\"'sa/\" doesn't throw an exception.");
        assertThrows(IllegalArgumentException.class, () -> cg.targetCoords("a4##"), "\"a4##\" doesn't throw an exception.");
        assertTrue(Arrays.equals(cg.targetCoords("a1"), new int[] {0, 0}));
        assertTrue(Arrays.equals(cg.targetCoords("f2"), new int[] {5, 1}));
        assertTrue(Arrays.equals(cg.targetCoords("aksjckienca389rg43ch4"), new int[] {7, 3}));
    }



    @Test
    public void testFENParser() {
        ChessGame fenGame = new ChessGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        BoardState cbs = fenGame.getCBS();
        assertEquals(cbs.getWhitesTurn(), cgCBS.getWhitesTurn());
        assertEquals(cbs.getWCastleS(), cgCBS.getWCastleS());
        assertEquals(cbs.getWCastleL(), cgCBS.getWCastleL());
        assertEquals(cbs.getBCastleS(), cgCBS.getBCastleS());
        assertEquals(cbs.getBCastleL(), cgCBS.getBCastleL());
        assertEquals(cbs.getHalfMove(), cgCBS.getHalfMove());
        assertEquals(cbs.getFullMove(), cgCBS.getFullMove());
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                assertEquals(fenGame.getBoard()[row][col], cg.getBoard()[row][col]);
            }
        }


    }

    @ParameterizedTest
    @CsvSource({
        "e4, pawn, 4, , 4, 3, false, false, false, , ",
        "bxc4, pawn, 1, , 2, 3, true, false, false, , ",
        "fxg8(Q), pawn, 5, , 6, 7, true, false, false, queen, ",
        "bxc4+, pawn, 1, , 2, 3, true, true, false, , ",
        "Ra1, rook, , , 0, 0, false, false, false, , ",
        "Qxh8, queen, , , 7, 7, true, false, false, , ",
        "Qfxa1, queen, 5, , 0, 0, true, false, false, , ",
        "Rb3xc3, rook, 1, 2, 2, 2, true, false, false, , ",
        "Ra1#, rook, , , 0, 0, false, false, true, , ",
        "Qxh8+, queen, , , 7, 7, true, true, false, , ",
        "O-O, , , , , , false, false, false, ,true ",
        "O-O-O, , , , , , false, false, false, ,false ",
        "O-O+, , , , , , false, true, false, ,true ",
        "O-O-O#, , , , , , false, false, true, ,false ",
    })
    public void testPGNCommandParser(String input, String pieceType, Integer startCol, Integer startRow, Integer endCol, Integer endRow, boolean capture, boolean check, boolean checkmate, String promoPieceType, Boolean castleShort) {
        Move m = cg.moveParser(input);
        assertNotNull(m);
        assertEquals(pieceType, m.getPieceType());
        assertEquals(startCol, m.getStartCol());
        assertEquals(startRow, m.getStartRow());
        assertEquals(m.getEndCol(), endCol);
        assertEquals(endRow, m.getEndRow());
        assertEquals(capture, m.getCapture());
        assertEquals(check, m.getCheck());
        assertEquals(checkmate, m.getCheckmate());
        assertEquals(promoPieceType, m.getPromoPieceType());
        assertEquals(castleShort, m.getCastleShort());
    }
    @Test
    public void testPGNCommandParserForErrors() {
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("askbkjbkjb"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Rxa9"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Qixa7"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Xxb3"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Nabab"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("xxa3+"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("(Q)"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("a3(Q)"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Rxa8(Q)"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Kf6+#"));
        assertThrows(IllegalArgumentException.class, () -> cg.moveParser("Qjf6#"));
    }
    @Test
    public void testBoardCloner() {

        Piece[][] board=cg.getBoard();
        Piece[][] test = cg.cloneBoard(board);
        
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                assertEquals(board[row][col], test[row][col]);
            }
        }
    }
    @Test
    public void testGetKingCoords() {
        assertTrue(Arrays.equals(cg.getKingsCoords(cg.getBoard(), cgCBS.getWhitesTurn()), new int[] {4, 0}));
        assertTrue(Arrays.equals(cg.getKingsCoords(cg.getBoard(), !cgCBS.getWhitesTurn()), new int[] {4, 7}));
        Piece[][] test = cg.cloneBoard(cg.getBoard());
        test[0][4]=null;
        assertThrows(IllegalArgumentException.class, () -> cg.getKingsCoords(test, cgCBS.getWhitesTurn()));
    }
    @Test
    public void testIsSquareInRange() {
        cg = new ChessGame();
        assertTrue(cg.isSquareInPieceRange(new Move("pawn", 0, 1, 0, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("pawn", 0, 1, 1, 2, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("pawn", 0, 1, 1, 3, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("knight", 1, 0, 1, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("knight", 1, 0, 2, 2, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("knight", 1, 0, 2, 2, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("bishop", 2, 0, 2, 4, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("bishop", 2, 0, 7, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("queen", 3, 0, 4, 0, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("queen", 3, 0, 4, 0, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("king", 4, 0, 4, 1, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("king", 4, 0, 4, 1, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("king", 4, 0, 4, 0, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("king", 4, 0, 4, 0, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertThrows(IllegalArgumentException.class, () -> cg.isSquareInPieceRange(new Move("king", 4, 7, 4, 6, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertThrows(IllegalArgumentException.class, () -> cg.isSquareInPieceRange(new Move("pawn", 4, 6, 4, 5, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("pawn", 4, 6, 4, 5, false), cg.getBoard(), !cg.getCBS().getWhitesTurn()));
        cg = new ChessGame("2kr1bnr/pb1nqp1p/1p4p1/2ppN3/3P4/2NQ4/PPPBBPPP/4RRK1 b - - 1 11");
        assertTrue(cg.isSquareInPieceRange(new Move("king", 2, 7, 2, 6, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("pawn", 2, 4, 3, 3, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("knight", 3, 6, 4, 4, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("queen", 4, 6, 4, 4, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("queen", 4, 6, 7, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("rook", 3, 7, 4, 7, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("pawn", 6, 5, 6, 4, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertTrue(cg.isSquareInPieceRange(new Move("pawn", 7, 6, 7, 4, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("king", 2, 7, 1, 6, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("king", 2, 7, 1, 6, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("bishop", 5, 7, 4, 6, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("bishop", 1, 6, 5, 2, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("bishop", 1, 6, 5, 2, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("queen", 4, 6, 4, 0, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("queen", 4, 6, 4, 0, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("pawn", 2, 4, 3, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertFalse(cg.isSquareInPieceRange(new Move("pawn", 2, 4, 2, 3, true), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertThrows(IllegalArgumentException.class, () -> cg.isSquareInPieceRange(new Move("pawn", 0, 1, 0, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        assertThrows(IllegalArgumentException.class, () -> cg.isSquareInPieceRange(new Move("pawn", 7, 3, 0, 3, false), cg.getBoard(), cg.getCBS().getWhitesTurn()));
        
    }
    @Test
    public void testThreatSquare() {
        cg = new ChessGame();
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 0, 5));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), false, 0, 5));
        cg = new ChessGame("2kr1bnr/pb1nqp1p/1p4p1/2ppN3/3P4/2NQ4/PPPBBPPP/4RRK1 b - - 1 11");
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 4, 5));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), false, 6, 5));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 0, 7));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), false, 2, 4));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 4, 4));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 4, 5));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 2, 6));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), false, 2, 3));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), true, 2, 3));
        assertTrue(cg.isSquareThreatened(cg.getBoard(), false, 3, 6));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), true, 5, 2));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), true, 3, 0));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), true, 3, 2));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), true, 2, 2));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), false, 4, 5));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), false, 3, 7));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), false, 7, 7));
        assertFalse(cg.isSquareThreatened(cg.getBoard(), false, 7, 3));
    }
    @Test
    public void testCheckChecker() {
        ChessGame fenGame = new ChessGame("rnbqkbnr/ppppp2p/5p2/6pQ/2N5/4P3/PPPP1PPP/R1B1KBNR w KQkq - 0 1");
        assertTrue(fenGame.checkChecker(fenGame.getBoard(), false));
        assertFalse(fenGame.checkChecker(fenGame.getBoard(), true));
        fenGame = new ChessGame("r1b2rk1/ppp1ppbp/3q1Np1/8/1np1P3/5Q2/PBPPNPPP/2KR3R b - - 0 10");
        assertTrue(fenGame.checkChecker(fenGame.getBoard(), false));
        assertFalse(fenGame.checkChecker(fenGame.getBoard(), true));
    }
    @Test
    public void testStalemateChecker() {
        cg = new ChessGame();
        assertFalse(cg.stalemateChecker(cg.getCBS(), true));
        assertFalse(cg.stalemateChecker(cg.getCBS(), true));
        cg.attemptMove("e4");
        assertFalse(cg.stalemateChecker(cg.getCBS(), false));

    }
    @Test
    public void testCheckmateChecker() {
        ChessGame fenGame = new ChessGame("rnbqkbnr/ppppp2p/5p2/6pQ/2N5/4P3/PPPP1PPP/R1B1KBNR w KQkq - 0 1");
        assertTrue(fenGame.checkmateChecker(fenGame.getCBS(), false));
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), true));
        fenGame = new ChessGame("rnbqkbnr/8/8/8/8/P7/8/RNBQKBNR w KQkq - 0 1");
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), true));
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), false));
        fenGame = new ChessGame("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), true));
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), false));
        fenGame = new ChessGame("r1b2rk1/ppp1ppbp/3q1Np1/8/1np1P3/5Q2/PBPPNPPP/2KR3R b - - 0 10");
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), false));
        assertFalse(fenGame.checkmateChecker(fenGame.getCBS(), true));
        fenGame = new ChessGame("r1b1k1nr/ppp1npbp/4P1p1/8/8/4B3/PqP1PPPP/1K1R1BNR w kq - 0 10");
        assertTrue(fenGame.checkmateChecker(fenGame.getCBS(), true));
    }
    @ParameterizedTest
    @CsvSource({
        "r1b2rk1/ppp1pp1p/3q1bp1/8/1np1P3/5Q2/PBPPNPPP/2KR3R w - - 0 11, 1. e4 d5 2. Qf3 Nf6 3. b4 g6 4. Bc4 Bg7 5. Bb2 O-O 6. Ne2 Qd6 7. Nbc3 Nc6 8. O-O-O dxc4 9. Nd5 Nxb4 10. Nxf6+ Bxf6",
        "r1b1k1nr/ppp1npbp/4P1p1/1q6/8/4B3/PPP1PPPP/1K1R1BNR b kq - 5 9, 1. Nc3 Nc6 2. d4 g6 3. d5 e5 4. dxe6 Nce7 5. Qxd7+ Qxd7 6. Nd5 Qxd5 7. Be3 Qb5 8. O-O-O Bg7 9. Kb1",
        "r1b1k1nr/ppp1npbp/4P1p1/8/8/4B3/PqP1PPPP/1K1R1BNR w kq - 0 10, 1. Nc3 Nc6 2. d4 g6 3. d5 e5 4. dxe6 Nce7 5. Qxd7+ Qxd7 6. Nd5 Qxd5 7. Be3 Qb5 8. O-O-O Bg7 9. Kb1 Qxb2#"
    })
    public void testBulkPGNMoveMaker(String fen, String pgn) {
        /*
        ChessGame fenCG = new ChessGame(fen);
        BoardState fbs = fenCG.getCBS();
        ChessGame pgnCG = new ChessGame(pgn);
        BoardState pbs = pgnCG.getCBS();
        assertEquals(fbs.getWhitesTurn(), pbs.getWhitesTurn());
        assertEquals(fbs.getWCastleS(), pbs.getWCastleS());
        assertEquals(fbs.getWCastleL(), pbs.getWCastleL());
        assertEquals(fbs.getBCastleS(), pbs.getBCastleS());
        assertEquals(fbs.getBCastleL(), pbs.getBCastleL());
        assertEquals(fbs.getHalfMove(), pbs.getHalfMove());
        assertEquals(fbs.getFullMove(), pbs.getFullMove());
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                assertEquals(fbs.getBoard()[row][col], pbs.getBoard()[row][col]);
            }
        }
        */
    }

}
