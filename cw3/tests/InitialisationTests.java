import draughts.*;

import java.util.*;
import java.util.concurrent.*;

import org.junit.*;
import static org.junit.Assert.*;

public class InitialisationTests {

    @Test
    public void testPiecesAreCorrectlyPlacedAtStartOfGame() {
        DraughtsModel model = new DraughtsModel("Game", null);

        Set<Piece> pieces = new CopyOnWriteArraySet<Piece>();
        pieces.add(new Piece(Colour.White, 1, 0));
        pieces.add(new Piece(Colour.White, 3, 0));
        pieces.add(new Piece(Colour.White, 5, 0));
        pieces.add(new Piece(Colour.White, 7, 0));
        pieces.add(new Piece(Colour.White, 0, 1));
        pieces.add(new Piece(Colour.White, 2, 1));
        pieces.add(new Piece(Colour.White, 4, 1));
        pieces.add(new Piece(Colour.White, 6, 1));
        pieces.add(new Piece(Colour.White, 1, 2));
        pieces.add(new Piece(Colour.White, 3, 2));
        pieces.add(new Piece(Colour.White, 5, 2));
        pieces.add(new Piece(Colour.White, 7, 2));

        pieces.add(new Piece(Colour.Red, 0, 5));
        pieces.add(new Piece(Colour.Red, 2, 5));
        pieces.add(new Piece(Colour.Red, 4, 5));
        pieces.add(new Piece(Colour.Red, 6, 5));
        pieces.add(new Piece(Colour.Red, 1, 6));
        pieces.add(new Piece(Colour.Red, 3, 6));
        pieces.add(new Piece(Colour.Red, 5, 6));
        pieces.add(new Piece(Colour.Red, 7, 6));
        pieces.add(new Piece(Colour.Red, 0, 7));
        pieces.add(new Piece(Colour.Red, 2, 7));
        pieces.add(new Piece(Colour.Red, 4, 7));
        pieces.add(new Piece(Colour.Red, 6, 7));

        assertEquals("The pieces should be added in the correct positions for the correct colour.", pieces, model.getPieces());
    }

    @Test
    public void testCurrentPlayerIsRedAtStartOfGame() {
        DraughtsModel model = new DraughtsModel("Game", null);

        assertEquals("The red player should be the current player at the beginning of the game.", Colour.Red, model.getCurrentPlayer());
    }

}
