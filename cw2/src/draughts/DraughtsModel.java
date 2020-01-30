package draughts;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class that represents a game of Draughts.
 */

public class DraughtsModel {

    private String gameName;
    private Colour currentPlayer;
    private Player player;
    private Set<Piece> pieces;

    /**
     * Constructs a game of Draughts from a save game.
     *
     * @param gameName the name of this game.
     * @param player the Player object used to get the Moves
     * from the users.
     * @param currentPlayer the colour of the current player in the game.
     * @param pieces the pieces left in the game.
     */
    public DraughtsModel(String gameName, Player player, Colour currentPlayer, Set<Piece> pieces) {
        this.gameName = gameName;
        this.player = player;
        this.currentPlayer = currentPlayer;
        this.pieces = new CopyOnWriteArraySet<Piece>(pieces);
    }

    /**
     * Constructs a new game of Draughts.
     *
     * @param gameName the name of this game.
     * @param player the Player object used to get the Moves
     * from the users.
     */
    public DraughtsModel(String gameName, Player player) {
        this.gameName = gameName;
        this.player = player;
        pieces = new CopyOnWriteArraySet<Piece>();
        currentPlayer = Colour.Red;
        initialisePieces();
    }

    // Creates the initial Set of Pieces.
    // (0, 0) is the top left of the board.
    private void initialisePieces() {
        //Not implemented
    }

    /**
     * Starts the game.
     */
    public void start() {
        while(!isGameOver()) {
            turn();
        }
    }

    /**
     * Performs a turn in the game.
     */
    protected void turn() {
        Move move = getPlayerMove(validMoves(currentPlayer));
        if (move != null) play(move);
        nextPlayer();
    }

    // Plays a Move in the game.
    // @param move the Move to be played.
    protected void play(Move move) {
        Point destination = move.destination;
        Piece piece = move.piece;
        Point position = new Point(piece.getX(), piece.getY());
        piece.setX((int) destination.getX());
        piece.setY((int) destination.getY());

        boolean jump = removePiece(position, destination);
        //Not fully implemented: If jump is true we would need to deal with
        //subsequent moves
    }

    // If a Piece has been jumped over, it will be removed.
    // Returns true if a Piece has been jumped over.
    // @param position the position of the jumping Piece.
    // @param destination the destination of the jumping Piece.
    // @return true if a Piece has been jumped over.
    protected boolean removePiece(Point position, Point destination) {
        int x = (int) (destination.getX() - position.getX());
        int y = (int) (destination.getY() - position.getY());
        if (x == 2 || x == (-2)) {
            x = (int) (position.getX() + (x / 2));
            y = (int) (position.getY() + (y / 2));
            Piece piece = getPiece(x, y);
            pieces.remove(piece);
            return true;
        }
        return false;
    }

    // Returns the Move selected by the users.
    // @param validMoves the Set of valid Moves for the current player.
    // @return the Move selected by the users.
    private Move getPlayerMove(Set<Move> validMoves) {
        return player.notify(validMoves);
    }

    // Updates the current player.
    private void nextPlayer() {
        if (currentPlayer.equals(Colour.Red)) currentPlayer = Colour.White;
        else currentPlayer = Colour.Red;
    }

    // Returns a Set of valid Moves for a player. These will only be one move ahead.
    // @param player the Colour of the player for whom the Moves should be generated.
    // @return a Set of valid Moves for a player.
    private Set<Move> validMoves(Colour player) {
        //Not implemented
        return new HashSet<Move>();
    }

    /**
     * Returns the Colour of the current player.
     *
     * @return the Colour of the current player.
     */
    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the Set of Pieces.
     *
     * @return the Set of Pieces.
     */
    public Set<Piece> getPieces() {
        return pieces;
    }

    /**
     * Returns the name of the game.
     *
     * @return the name of the game.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Returns the Piece with the specified coordinates.
     *
     * @param x the x coordinate of the Piece.
     * @param y the y coordinate of the Piece.
     * @return the Piece with the specified coordinates.
     */
    public Piece getPiece(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) return piece;
        }
        return null;
    }

    /**
     * Returns true if the game is over.
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        //Not implemented
        return true;
    }

    /**
     * Returns a String containing who won the game.
     *
     * @return a String containing who won the game.
     */
    public String getWinningMessage() {
        if (validMoves(Colour.Red).size() == 0) return "White Player wins!";
        else return "Red Player wins!";
    }

}
