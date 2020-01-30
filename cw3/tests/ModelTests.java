import draughts.*;

import java.awt.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ModelTests {

    private TestHelper helper;

    public ModelTests() {
      helper = new TestHelper();
    }

    @Test
    public void testGameNameIsCorrect() throws Exception {
        DraughtsModel model = new DraughtsModel("Test", null);

        assertEquals("The game name should be the same as the one passed in", "Test", model.getGameName());
    }

    @Test
    public void testCurrentPlayerUpdatesCorrectly() throws Exception {
        TestHelper.TestModel model = helper.makeTestModel("Test", helper.makeTestPlayer());

        assertEquals("The current player should be red initially", Colour.Red, model.getCurrentPlayer());

        model.turnInModel();
        assertEquals("The current player should be white after one turn", Colour.White, model.getCurrentPlayer());

        model.turnInModel();
        assertEquals("The current player should be red after two turns", Colour.Red, model.getCurrentPlayer());
    }

    @Test
    public void testCorrectPieceIsReturned() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece test1 = new Piece(Colour.Red, 3, 5);
        Piece test2 = new Piece(Colour.Red, 7, 2);
        Piece test3 = new Piece(Colour.White, 1, 0);
        Piece test4 = new Piece(Colour.White, 4, 4);
        pieces.add(test1);
        pieces.add(test2);
        pieces.add(test3);
        pieces.add(test4);

        DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);

        assertEquals("The correct piece should be returned", test1, model.getPiece(3, 5));

        assertEquals("The correct piece should be returned", test2, model.getPiece(7, 2));

        assertEquals("The correct piece should be returned", test3, model.getPiece(1, 0));

        assertEquals("The correct piece should be returned", test4, model.getPiece(4, 4));
    }

    @Test
    public void testRemovePieceReturnsTrueForJumpMove() {
        TestHelper.TestModel model = helper.makeTestModel("Test", null);
        assertTrue("The removePiece method should return true when a forwards jump move occurs", model.removePieceInModel(new Point(0,0), new Point(2,2)));
        assertTrue("The removePiece method should return true when a backwards jump move occurs", model.removePieceInModel(new Point(2,2), new Point(0,0)));
    }

    @Test
    public void testRemovePieceReturnsFalseForNonJumpMove() {
        TestHelper.TestModel model = helper.makeTestModel("Test", null);
        assertFalse("The removePiece method should return false when a non jump more occurs", model.removePieceInModel(new Point(0,0), new Point(1,1)));
    }

    @Test
    public void testMoveIsPlayedCorrectly() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.White, 0, 0);
        pieces.add(piece);
        TestHelper.TestModel model = helper.makeTestModel("Test", helper.makeTestPlayer(), Colour.White, pieces);
        model.turnInModel();

        Piece testPiece = new Piece(Colour.White, 1, 1);

        assertEquals("The model should have the correct number of pieces after making a non-jump move.", 1, model.getPieces().size());
        assertEquals("The piece should have been moved to the correct location.", testPiece, model.getPieces().iterator().next());
    }

    @Test
    public void testJumpMoveIsPlayedCorrectly() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece test1 = new Piece(Colour.White, 0, 0);
        Piece test2 = new Piece(Colour.Red, 1, 1);
        pieces.add(test1);
        pieces.add(test2);
        TestHelper.TestModel model = helper.makeTestModel("Test", helper.makeTestPlayer(), Colour.White, pieces);
        model.turnInModel();

        Piece testPiece = new Piece(Colour.White, 2, 2);

        assertEquals("The model should have the correct number of pieces after making a non-jump move.", 1, model.getPieces().size());
        assertEquals("The piece should have been moved to the correct location.", testPiece, model.getPieces().iterator().next());
    }

    @Test
    public void testTurnProgressesAfterPlayerMove() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece test1 = new Piece(Colour.White, 0, 0);
        Piece test2 = new Piece(Colour.Red, 2, 2);
        pieces.add(test1);
        pieces.add(test2);
        TestHelper.TestPlayer testPlayer = helper.makeTestPlayer();
        TestHelper.TestModel model = helper.makeTestModel("Test", testPlayer, Colour.White, pieces);
        model.turnInModel();

        assertFalse("The turn should advance once a player has made a non-jump move.", model.isGameOver());
    }

}
