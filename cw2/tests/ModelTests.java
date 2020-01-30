import draughts.*;

import java.awt.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ModelTests {

    private class TestPlayer implements Player {

        @Override
        public Move notify(Set<Move> moves) {
            if (moves.iterator().hasNext()) return moves.iterator().next();
            return null;
        }

    }

    public class TestModel extends DraughtsModel {

      public TestModel(String gameName, Player player, Colour currentPlayer, Set<Piece> pieces) {
          super(gameName, player, currentPlayer, pieces);
      }

      public TestModel(String gameName, Player player) {
          super(gameName, player);
      }

      public boolean removePieceInModel(Point position, Point destination) {
          return removePiece(position, destination);
      }

      public void turnInModel() {
          turn();
      }

      public void playInModel(Move move) {
          play(move);
      }
    /*  public void initialisePiecesInModel() {
        initialisePieces();
      } */
    }

    @Test
    public void testGameNameIsCorrect() throws Exception {
        DraughtsModel model = new DraughtsModel("Test", null);

        assertEquals("The game name should be the same as the one passed in", "Test", model.getGameName());
    }

    @Test
    public void testCurrentPlayerIsRedAtStartOfGame() throws Exception {
        DraughtsModel model = new DraughtsModel("Game", null);

        assertEquals("The red player should be the current player at the beginning of the game.", Colour.Red, model.getCurrentPlayer());
    }

    @Test
    public void testCurrentPlayerUpdatesCorrectly() throws Exception {
        TestModel model = new TestModel("Test", new TestPlayer());

        assertEquals("The current player should be red initially", Colour.Red, model.getCurrentPlayer());

        model.turnInModel();
        assertEquals("The current player should be White after one turn", Colour.White, model.getCurrentPlayer());

        model.turnInModel();
        assertEquals("The current player should be red after two turns", Colour.Red, model.getCurrentPlayer());
    }

   @Test
    public void testCorrectPieceisRemoved() {
      Set<Piece> pieces = new HashSet<Piece>();
      Piece piece = new Piece(Colour.Red, 1, 0);
      pieces.add(piece);
      piece = new Piece(Colour.White, 2, 1);
      pieces.add(piece);
      TestModel model = new TestModel("Test", new TestPlayer(), Colour.Red, pieces);
      Boolean test = model.removePieceInModel(new Point(1,0), new Point(3,2));
      assertEquals("The correct piece should be removed", true, test);
    }

    @Test
    public void testCorrectPieceisMoved() {
      Set<Piece> pieces = new HashSet<Piece>();
      Piece piece = new Piece(Colour.Red, 1,0);
      pieces.add(piece);
      TestModel model = new TestModel("Test", new TestPlayer(), Colour.Red, pieces);
      Move testMove = new Move(piece, 2, 1);
      model.playInModel(testMove);
      assertEquals("The piece is moved incorrectly", piece, model.getPiece(2,1));

    }

    @Test
    public void testPieceisRemovedWhenJumped() {
      Set<Piece> pieces = new HashSet<Piece>();
      Piece piece = new Piece(Colour.Red, 1,0);
      pieces.add(piece);
      piece = new Piece(Colour.White, 2,1);
      pieces.add(piece);
      TestModel model = new TestModel("Test", new TestPlayer(), Colour.Red, pieces);
      Move testMove = new Move(piece, 3,2);
      model.playInModel(testMove);
      assertEquals("The piece has been removed when jumped", model.getPiece(2,1), null);

    }

    @Test
    public void testPiecesAreInitialised() {
      Set<Piece> pieces = new HashSet<Piece>();
      TestModel model = new TestModel("Test", new TestPlayer(), Colour.Red, pieces);
      // model.initialisePiecesInModel();

      for(int i=0;i<=2;i++){
        for(int j=(i+1)%2;j<=6+(i+1)%2;j=j+2){
          // System.out.println("("+i+","+j+")");
          // assertEquals("A piece has not been initialised", model.getPiece(j,i), new Piece(Colour.White, j, i));
        }
      }
        for(int x=5;x<=7;x++){
          for(int y=(x+1)%2;y<=6+(x+1)%2;y=y+2){
            System.out.println("("+x+","+y+")");
            // assertEquals("A piece has not been initialised", model.getPiece(y,x), new Piece(Colour.Red, y, x));
          }
        }
      }


    @Test
    public void testCorrectPieceIsReturned() {
      Set<Piece> pieces = new HashSet<Piece>();
      Piece piece = new Piece(Colour.Red, 3, 5);
      pieces.add(piece);
      DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);

      assertEquals("The correct piece should be returned", piece, model.getPiece(3,5));
    }
}
