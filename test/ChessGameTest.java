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
    @Test
    public void testTargetCoords() {
        ChessGame cg = new ChessGame();
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
        ChessGame cg1 = new ChessGame();
        ChessGame cg2 = new ChessGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        BoardState bs1 = cg1.getCBS();
        BoardState bs2 = cg2.getCBS();
        assertEquals(bs1.getWhitesTurn(), bs2.getWhitesTurn());
        assertEquals(bs1.getWCastleS(), bs2.getWCastleS());
        assertEquals(bs1.getWCastleL(), bs2.getWCastleL());
        assertEquals(bs1.getBCastleS(), bs2.getBCastleS());
        assertEquals(bs1.getBCastleL(), bs2.getBCastleL());
        assertEquals(bs1.getHalfMove(), bs2.getHalfMove());
        assertEquals(bs1.getFullMove(), bs2.getFullMove());
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                assertEquals(bs1.getBoard()[row][col], bs2.getBoard()[row][col]);
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
        ChessGame cg = new ChessGame();
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
        //assertEquals(promoPieceType, m.getPromoPiece());
        assertEquals(castleShort, m.getCastleShort());
    }
    @Test
    public void testPGNCommandParserForErrors() {
        ChessGame cg = new ChessGame();
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
        ChessGame cg = new ChessGame();
        Piece[][] test = cg.cloneBoard(cg.getBoard());
        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                assertEquals(cg.getBoard()[row][col], test[row][col]);
            }
        }
    }
    @Test
    public void testGetKingCoords() {
        ChessGame cg = new ChessGame();
        assertTrue(Arrays.equals(cg.getKingsCoords(cg.getBoard(), cg.getCBS().getWhitesTurn()), new int[] {4, 0}));
        assertTrue(Arrays.equals(cg.getKingsCoords(cg.getBoard(), !cg.getCBS().getWhitesTurn()), new int[] {4, 7}));
        Piece[][] test = cg.cloneBoard(cg.getBoard());
        test[0][4]=null;
        assertThrows(IllegalArgumentException.class, () -> cg.getKingsCoords(test, cg.getCBS().getWhitesTurn()));
    }
    @ParameterizedTest
    @CsvSource({
        "3, 1, 3, 3, rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq - 0 1",
        "4, 0, 2, 4, rnbqkbnr/pppppppp/8/2K5/8/8/PPPPPPPP/RNBQ1BNR w kq - 0 1",
        "4, 1, 3, 2, rnbqkbnr/pppppppp/8/8/8/3P4/PPPP1PPP/RNBQKBNR w kq - 0 1"
    })
    public void moveMaker(int startCol, int startRow, int endCol, int endRow, String endPos) {
        ChessGame cg1 = new ChessGame();
        Piece[][] test = cg1.performMove(cg1.getBoard(), startCol, startRow, endCol, endRow);
        ChessGame cg2 = new ChessGame(endPos);
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                assertEquals(test[r][c], cg2.getBoard()[r][c]);
            }
        }
    }
    @Test
    public void moveMakerCastle() {
        ChessGame cg = new ChessGame("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/R3KBNR w KQkq - 0 1");
        Piece[][] board1 = cg.performMove(cg.getBoard(), true, false);
        ChessGame cg1 = new ChessGame("rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/2KR1BNR b kq - 1 1");
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                assertEquals(board1[r][c], cg1.getBoard()[r][c]);
            }
        }
        Piece[][] board2 = cg1.performMove(board1, false, true);
        ChessGame cg2 = new ChessGame("rnbq1rk1/pppppppp/8/8/8/8/PPPPPPPP/2KR1BNR w - - 2 2");
        for (int r=0; r<8; r++) {
            for (int c=0; c<8; c++) {
                assertEquals(board2[r][c], cg2.getBoard()[r][c]);
            }
        }
        // TODO test this
    }
    @Test
    public void testIsSquareInPieceThreatRange() {
        // TODO test this
        ChessGame cg = new ChessGame("rnbq1rk1/ppppbppp/5n2/4p3/3P4/2NQB3/PPP1PPPP/2KR1BNR b - - 7 5");
        assertTrue(cg.isSquareInPieceThreatRange(cg.getBoard(), 5, 5, 4, 3));
        assertFalse(cg.isSquareInPieceThreatRange(cg.getBoard(), 5, 5, 6, 7));
        assertTrue(cg.isSquareInPieceThreatRange(cg.getBoard(), 3, 3, 4, 4));
        assertTrue(cg.isSquareInPieceThreatRange(cg.getBoard(), 3, 3, 2, 4));
        assertFalse(cg.isSquareInPieceThreatRange(cg.getBoard(), 3, 3, 3, 4));
        assertFalse(cg.isSquareInPieceThreatRange(cg.getBoard(), 3, 3, 7, 7));
    }
    @Test
    public void testIsSquareUnderThreat() {
        ChessGame cg = new ChessGame("rnbq1rk1/ppppbppp/5n2/4p3/3P4/2NQB3/PPP1PPPP/2KR1BNR b - - 7 5");

    }
    @Test
    public void testIsSquareInPieceMoveRange() {
        // TODO test this
    }
    @Test
    public void testIsMoveLegal() {
        // TODO test this
    }
    @Test
    public void testIsMoveLegalCastle() {
        // TODO test this
    }
    @Test
    public void testPossibleMovesForDecodedPGN() {
        // TODO test this
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
        
    }
    @Test
    public void testCheckmateChecker() {
        //ChessGame cg = new ChessGame("r1b1k1nr/ppp1npbp/4P1p1/8/8/4B3/PqP1PPPP/1K1R1BNR w kq - 0 10");
        //assertTrue(cg.checkmateChecker(cg.getBoard(), false, null));
    }
    @ParameterizedTest
    @CsvSource({
        "r1b2rk1/ppp1pp1p/3q1bp1/8/1np1P3/5Q2/PBPPNPPP/2KR3R w - - 0 11, 1. e4 d5 2. Qf3 Nf6 3. b4 g6 4. Bc4 Bg7 5. Bb2 O-O 6. Ne2 Qd6 7. Nbc3 Nc6 8. O-O-O dxc4 9. Nd5 Nxb4 10. Nxf6+ Bxf6",
        "r1b1k1nr/ppp1npbp/4P1p1/1q6/8/4B3/PPP1PPPP/1K1R1BNR b kq - 5 9, 1. Nc3 Nc6 2. d4 g6 3. d5 e5 4. dxe6 Nce7 5. Qxd7+ Qxd7 6. Nd5 Qxd5 7. Be3 Qb5 8. O-O-O Bg7 9. Kb1",
        "r1b1k1nr/ppp1npbp/4P1p1/8/8/4B3/PqP1PPPP/1K1R1BNR w kq - 0 10, 1. Nc3 Nc6 2. d4 g6 3. d5 e5 4. dxe6 Nce7 5. Qxd7+ Qxd7 6. Nd5 Qxd5 7. Be3 Qb5 8. O-O-O Bg7 9. Kb1 Qxb2#"
    })
    public void testBulkPGNMoveMaker(String fen, String pgn) {
        
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
        
    }

}
