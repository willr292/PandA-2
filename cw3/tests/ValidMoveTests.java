import draughts.*;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ValidMoveTests {

    private TestHelper helper;

    public ValidMoveTests() {
      helper = new TestHelper();
    }

    @Test
    public void testValidMovesFromCenterOfBoard() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.White, 4, 4);
        pieces.add(piece);
        TestHelper.NoMovePlayer player = helper.makeMoveWithPieces(pieces);

        Set<Move> validMoves = new HashSet<Move>();
        addWhiteMove(validMoves, 4, 4, 5, 5);
        addWhiteMove(validMoves, 4, 4, 3, 5);

        TestHelper.assertSetEquals("The valid moves should be correct for moving from the the center of the boardc.", validMoves, player.validMoves);
    }

    @Test
    public void testValidMovesFromCornerOfBoard() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.White, 0, 0);
        pieces.add(piece);
        TestHelper.NoMovePlayer player = helper.makeMoveWithPieces(pieces);

        Set<Move> validMoves = new HashSet<Move>();
        addWhiteMove(validMoves, 0, 0, 1, 1);

        TestHelper.assertSetEquals("The valid moves should be correct for moving from the top left corner.", validMoves, player.validMoves);
    }

    @Test
    public void testValidMovesWithCapture() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece1 = new Piece(Colour.White, 2, 0);
        pieces.add(piece1);
        Piece piece2 = new Piece(Colour.White, 3, 1);
        pieces.add(piece2);
        Piece piece3 = new Piece(Colour.Red, 1, 1);
        pieces.add(piece3);
        TestHelper.NoMovePlayer player = helper.makeMoveWithPieces(pieces);

        Set<Move> validMoves = new HashSet<Move>();
        addWhiteMove(validMoves, 2, 0, 0, 2);
        addWhiteMove(validMoves, 3, 1, 2, 2);
        addWhiteMove(validMoves, 3, 1, 4, 2);

        for (Move testMove: player.validMoves) {
            System.out.println(testMove.toString());
        }
        TestHelper.assertSetEquals("The valid moves should be correct for moving from the top left corner.", validMoves, player.validMoves);
    }

    @Test
    public void testValidMovesForKing() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.White, 1, 1);
        piece.setKing(true);
        pieces.add(piece);
        TestHelper.NoMovePlayer player = helper.makeMoveWithPieces(pieces);

        Set<Move> validMoves = new HashSet<Move>();
        addWhiteMove(validMoves, 1, 1, 0, 0);
        addWhiteMove(validMoves, 1, 1, 0, 2);
        addWhiteMove(validMoves, 1, 1, 2, 2);
        addWhiteMove(validMoves, 1, 1, 2, 0);

        TestHelper.assertSetEquals("The valid moves should be correct for moving from the top left corner.", validMoves, player.validMoves);
    }

    public void addWhiteMove(Set<Move> moves, int startX, int startY, int x, int y) {
        Piece piece = new Piece(Colour.White, startX, startY);
        moves.add(new Move(piece, x, y));
    }

}
