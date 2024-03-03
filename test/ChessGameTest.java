package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import model.ChessGame;
import model.Move;

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
        
    })
    public void testPGNCommandParser(String input, String pieceType, Integer startCol, Integer startRow, Integer endCol, Integer endRow, boolean capture, boolean check, boolean checkmate, String promoPieceType, Boolean castleShort) {

        Move m = cg.moveParser(input);

        assertNotNull(m);
        assertEquals(pieceType, m.getPieceType());
        assertEquals(startCol, m.getStartCol());
        assertEquals(startRow, m.getStartRow());
        assertEquals(endCol, m.getEndCol());
        assertEquals(endRow, m.getEndRow());
        assertEquals(capture, m.getCapture());
        assertEquals(check, m.getCheck());
        assertEquals(checkmate, m.getCheckmate());
        assertEquals(promoPieceType, m.getPromoPieceType());
        assertEquals(castleShort, m.getCastleShort());
    }
    


}
