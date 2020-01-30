import draughts.*;

import java.awt.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TestHelper {

    public static void assertSetEquals(String message, Set set1, Set set2) {
        assertEquals(message + " Size was not equal.", set1.size(), set2.size());

        for (Object object : set1) {
            boolean containsObject = false;
            for (Object o : set2) {
                if (object.equals(o)) {
                    containsObject = true;
                    break;
                }
            }
            if (!containsObject) fail(message + "Set did not contain object: " + object);
        }
    }

    public static void assertSetNotEquals(String message, Set set1, Set set2) {
        boolean equal = (set1.size() == set2.size());

        for (Object object : set1) {
            boolean containsObject = false;
            for (Object o : set2) {
                if (object.equals(o)) {
                    containsObject = true;
                    break;
                }
            }
            if (!containsObject) equal = false;
        }
        if (equal) fail(message + "Sets were equal.");
    }

    //Helper class implementing Player
    public class TestPlayer implements Player {

        @Override
        public Move notify(Set<Move> validMoves) {
            if (validMoves.iterator().hasNext()) return validMoves.iterator().next();
            return null;
        }

    }

    //Helper class implementing Player
    public class NoMovePlayer implements Player {

        public Set<Move> validMoves;

        @Override
        public Move notify(Set<Move> validMoves) {
            this.validMoves = validMoves;
            return null;
        }

    }

    public TestPlayer makeTestPlayer() {
        return new TestPlayer();
    }

    public NoMovePlayer makeMoveWithPieces(Set<Piece> pieces) {
        NoMovePlayer player = new NoMovePlayer();
        TestModel model = new TestModel("Game", player, Colour.White, pieces);
        model.turnInModel();
        return player;
    }

    //Helper class extending DraughtsModel
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

    }

    public TestModel makeTestModel(String gameName, Player player, Colour currentPlayer, Set<Piece> pieces) {
        return new TestModel(gameName, player, currentPlayer, pieces);
    }

    public TestModel makeTestModel(String gameName, Player player) {
        return new TestModel(gameName, player);
    }

}
