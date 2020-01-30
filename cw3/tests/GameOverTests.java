import draughts.*;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class GameOverTests {

    @Test
    public void testIsGameOverWhenRedHasNoPieces() {
        Set<Piece> pieces = new HashSet<Piece>();
        pieces.add(new Piece(Colour.White, 1, 0));

        DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);
        assertTrue("The game should be over if the red player hasn't got any pieces left", model.isGameOver());
    }

    @Test
    public void testIsGameOverWhenWhiteHasNoPieces() {
        Set<Piece> pieces = new HashSet<Piece>();
        pieces.add(new Piece(Colour.Red, 0, 7));

        DraughtsModel model = new DraughtsModel("Test", null, Colour.White, pieces);
        assertTrue("The game should be over if the white player hasn't got any pieces left", model.isGameOver());
    }

    @Test
    public void testIsGameOverWhenRedHasNoMovesAndItsRedsTurn() {
        Set<Piece> pieces = new HashSet<Piece>();
        pieces.add(new Piece(Colour.White, 1, 0));
        pieces.add(new Piece(Colour.White, 3, 0));
        pieces.add(new Piece(Colour.Red, 2, 1));

        DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);
        assertTrue("The game should be over if the red player hasn't got any valid moves and it's the red players turn", model.isGameOver());
    }

    @Test
    public void testIsGameOverWhenWhiteHasNoMovesAndItsRedsTurn() {
        Set<Piece> pieces = new HashSet<Piece>();
        pieces.add(new Piece(Colour.Red, 4, 7));
        pieces.add(new Piece(Colour.Red, 6, 7));
        pieces.add(new Piece(Colour.White, 5, 6));

        DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);
        assertFalse("The game shouldn't be over if the red player hasn't got any valid moves and it's the red players turn", model.isGameOver());
    }

    @Test
    public void testIsGameOverWhenRedHasNoMovesAndItsWhitesTurn() {
        Set<Piece> pieces = new HashSet<Piece>();
        pieces.add(new Piece(Colour.White, 1, 0));
        pieces.add(new Piece(Colour.White, 3, 0));
        pieces.add(new Piece(Colour.Red, 2, 1));

        DraughtsModel model = new DraughtsModel("Test", null, Colour.White, pieces);
        assertFalse("The game shouldn't be over if the red player hasn't got any valid moves and it's the white players turn", model.isGameOver());
    }

    @Test
    public void testIsGameOverWhenWhiteHasNoMovesAndItsWhitesTurn() {
        Set<Piece> pieces = new HashSet<Piece>();
        pieces.add(new Piece(Colour.Red, 4, 7));
        pieces.add(new Piece(Colour.Red, 6, 7));
        pieces.add(new Piece(Colour.White, 5, 6));

        DraughtsModel model = new DraughtsModel("Test", null, Colour.White, pieces);
        assertTrue("The game should be over if the white player hasn't got any valid moves and it's the white players turn", model.isGameOver());
    }

}
