package draughts;

import java.awt.*;

/**
 * A class to represent a Move in the game.
 */

public class Move {

    /**
     * The Piece to be moved.
     */
    public final Piece piece;

    /**
     * The coordinates of the destination.
     */
    public final Point destination;

    /**
     * Constructs a new Move object.
     *
     * @param piece the Piece to be moved.
     * @param x the x coordinate of the destination.
     * @param y the y coordinate of the destination.
     */
    public Move(Piece piece, int x, int y) {
        this.piece = piece;
        destination = new Point(x, y);
    }

    /**
     * Returns true if this object contains the same data as the object passed in.
     *
     * @param obj the object to be checked for equality.
     * @return true if this object contains the same data as the object passed in.
     */
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Move)) return false;
      Move that = (Move) obj;
      if (!this.piece.equals(that.piece)) return false;
      if (!this.destination.equals(that.destination)) return false;
      return true;
    }

    /**
     * Returns a String representing a Move.
     *
     * @return a String representing a Move.
     */
    @Override
    public String toString() {
        return "Move: " + piece.toString() + " (" + destination.getX() + ", " + destination.getY() + ")";
    }

}
