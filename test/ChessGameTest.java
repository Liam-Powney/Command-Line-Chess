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
        "0-0, , , , , , false, false, false, ,true ",
        "0-0-0, , , , , , false, false, false, ,false ",
        "0-0+, , , , , , false, true, false, ,true ",
        "0-0-0#, , , , , , false, false, true, ,false ",
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
    public void testIsSquareInPieceCaptureRange() {
        ChessGame fenGame = new ChessGame("rnbqkbnr/8/8/8/8/8/8/RNBQKBNR w KQkq - 0 1");
        assertTrue(fenGame.isSquareInPieceCaptureRange(fenGame.getBoard(), 0, 0, 0, 3));
        fenGame = new ChessGame("rnbqkbnr/p1pppppp/8/8/8/1p6/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertTrue(fenGame.isSquareInPieceCaptureRange(fenGame.getBoard(), 0, 1, 1, 2));
        fenGame = new ChessGame();
        assertTrue(fenGame.isSquareInPieceCaptureRange(fenGame.getBoard(), 0, 1, 1, 2));
        assertFalse(fenGame.isSquareInPieceCaptureRange(fenGame.getBoard(),0, 1, 0, 2));
    }
    @Test
    public void testIsSquareInPieceMovingRange() {
        ChessGame fenGame = new ChessGame();
        assertTrue(fenGame.isSquareInPieceMovingRange(fenGame.getBoard(), 0, 1, 0, 2));
        assertTrue(fenGame.isSquareInPieceCaptureRange(fenGame.getBoard(), 0, 1, 1, 2));
        assertTrue(fenGame.isSquareInPieceMovingRange(fenGame.getBoard(), 0, 1, 0, 3));
        fenGame = new ChessGame("rnbqkbnr/8/8/8/8/P7/8/RNBQKBNR w KQkq - 0 1");
        assertFalse(fenGame.isSquareInPieceMovingRange(fenGame.getBoard(), 0, 2, 0, 4));
        fenGame = new ChessGame("rnbqkbnr/8/8/8/8/8/8/RNBQKBNR w KQkq - 0 1");
        assertTrue(fenGame.isSquareInPieceMovingRange(fenGame.getBoard(), 0, 0, 0, 7));
        assertFalse(fenGame.isSquareInPieceMovingRange(fenGame.getBoard(), 3, 0, 4, 0));
    }
    @Test
    public void testIsSquareThreatened() {
        ChessGame fenGame = new ChessGame();
        assertTrue(fenGame.isSquareThreatened(fenGame.getBoard(), 3, 2, false));
        assertFalse(fenGame.isSquareThreatened(fenGame.getBoard(), 3, 2, true));
        assertTrue(fenGame.isSquareThreatened(fenGame.getBoard(), 3, 5, true));
        assertFalse(fenGame.isSquareThreatened(fenGame.getBoard(), 3, 5, false));
        fenGame= new ChessGame("rnbqkbnr/8/8/8/8/8/8/RNBQKBNR w KQkq - 0 1");
        assertTrue(fenGame.isSquareThreatened(fenGame.getBoard(), 0, 7, false));
    }
    @Test
    public void testCheckChecker() {
        ChessGame fenGame = new ChessGame("rnbqkbnr/ppppp2p/5p2/6pQ/2N5/4P3/PPPP1PPP/R1B1KBNR w KQkq - 0 1");
        assertTrue(fenGame.checkChecker(fenGame.getBoard(), false));
        assertFalse(fenGame.checkChecker(fenGame.getBoard(), true));
    }
    @Test
    public void testCheckmateChecker() {
        ChessGame fenGame = new ChessGame("rnbqkbnr/ppppp2p/5p2/6pQ/2N5/4P3/PPPP1PPP/R1B1KBNR w KQkq - 0 1");
        assertTrue(fenGame.checkmateChecker(fenGame.getBoard(), false));
        assertFalse(fenGame.checkmateChecker(fenGame.getBoard(), true));
        fenGame = new ChessGame("rnbqkbnr/8/8/8/8/P7/8/RNBQKBNR w KQkq - 0 1");
        assertFalse(fenGame.checkmateChecker(fenGame.getBoard(), true));
        assertFalse(fenGame.checkmateChecker(fenGame.getBoard(), false));
        fenGame = new ChessGame("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        assertFalse(fenGame.checkmateChecker(fenGame.getBoard(), true));
        assertFalse(fenGame.checkmateChecker(fenGame.getBoard(), false));

    }
    @Test
    public void testPossibleMovesDecoder() {
        
    }

}
