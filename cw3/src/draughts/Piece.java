package draughts;

/**
 * A class to represent a Piece in the game.
 */

public class Piece {

    private final Colour colour;
    private int x;
    private int y;
    private boolean king;

    /**
     * Constructs a new Piece object.
     *
     * @param colour the Colour of the Piece.
     * @param x the x coordinate of the Piece.
     * @param y the y coordinate of the Piece.
     */
    public Piece(Colour colour, int x, int y) {
        this.colour = colour;
        this.x = x;
        this.y = y;
        this.king = false;
    }

    /**
     * Returns the Colour of the Piece.
     *
     * @return the Colour of the Piece.
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Returns the x coordinate of the Piece.
     *
     * @return the x coordinate of the Piece.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the Piece.
     *
     * @return the y coordinate of the Piece.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x coordinate of the Piece.
     *
     * @param x the new x coordinate of the Piece.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate of the Piece.
     *
     * @param y the new y coordinate of the Piece.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns true if the Piece is a king.
     *
     * @return true if the Piece is a king.
     */
    public boolean isKing() {
        return king;
    }

    /**
     * Sets whether the Piece is a king.
     *
     * @param king the boolean representing whether
     * the Piece is a king.
     */
    public void setKing(boolean king) {
        this.king = king;
    }

    /**
     * Returns a String representing a Piece.
     *
     * @return a String representing a Piece.
     */
    @Override
    public String toString() {
        return "[" + colour.toString() + ", " + x + ", " + y + ", " + king + "]";
    }

    /**
     * Returns true if this object contains the same data as the object passed in.
     *
     * @param obj the object to be checked for equality.
     * @return true if this object contains the same data as the object passed in.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Piece)) return false;
        Piece that = (Piece) obj;
        if (!this.getColour().equals(that.getColour())) return false;
        if (this.getX() != that.getX()) return false;
        if (this.getY() != that.getY()) return false;
        return true;
    }

    /**
     * Returns the Piece represented by a String.
     *
     * @param pieceString the String representing the Piece.
     * @return the Piece represented by a String.
     * @throws NullPointerException if the input is null.
     * @throws IllegalArgumentException if the input doesn't
     * represent a Piece object.
     */
    public static Piece valueOf(String pieceString) {
        if (pieceString == null) throw new NullPointerException();
        pieceString = pieceString.substring(1, pieceString.length() - 1);
        String[] items = pieceString.split(", ");
        if (items.length != 4) throw new IllegalArgumentException();
        Colour colour = Colour.valueOf(items[0]);
        int x = Integer.parseInt(items[1]);
        int y = Integer.parseInt(items[2]);
        boolean king = Boolean.parseBoolean(items[3]);
        Piece piece = new Piece(colour, x, y);
        piece.setKing(king);
        return piece;
    }

}
