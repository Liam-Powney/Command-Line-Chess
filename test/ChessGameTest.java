package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import model.Bishop;
import model.ChessGame;
import model.King;
import model.Knight;
import model.Move;
import model.Pawn;
import model.Piece;
import model.Queen;
import model.Rook;

public class ChessGameTest {
    
    ChessGame cg = new ChessGame();

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

        Piece[][] test = cg.cloneBoard(board);

        for (int row=0; row<8; row++) {
            for (int col=0; col<8; col++) {
                assertEquals(board[row][col], test[row][col]);
            }
        }
    }
    


}
