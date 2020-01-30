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
     * @param gameName      the name of this game.
     * @param player        the Player object used to get the Moves
     *                      from the users.
     * @param currentPlayer the colour of the current player in the game.
     * @param pieces        the pieces left in the game.
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
     * @param player   the Player object used to get the Moves
     *                 from the users.
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
        //TODO:
        for (int i = 0; i <= 2; i++) {
            for (int j = (i + 1) % 2; j <= 6 + (i + 1) % 2; j = j + 2) {
                Piece piece = new Piece(Colour.White, j, i);
                pieces.add(piece);
            }
        }
        for (int x = 5; x <= 7; x++) {
            for (int y = (x + 1) % 2; y <= 6 + (x + 1) % 2; y = y + 2) {
                Piece piece = new Piece(Colour.Red, y, x);
                pieces.add(piece);
            }
        }
    }

    /**
     * Starts the game.
     */
    public void start() {
        while (!isGameOver()) {
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
        boolean jumpOntoKing = jumpOntoKing(piece);
        checkForKing(piece);
        Set<Move> validMoves = validMoves(currentPlayer, piece, 1, true);
        if (piece.isKing()) validMoves.addAll(validMoves(currentPlayer, piece, -1, true));
        if (jump && validMoves.size() > 0 && !jumpOntoKing) {
            Move m = getPlayerMove(validMoves);
            play(m);
        }
    }

    // If a Piece has been jumped over, it will be removed.
    // Returns true if a Piece has been jumped over.
    // @param position the position of the jumping Piece.
    // @param destination the destination of the jumping Piece.
    // @return true if a Piece has been jumped over.
    protected boolean removePiece(Point position, Point destination) {
        int x = (int) (destination.getX() - position.getX());
        int y = (int) (destination.getY() - position.getY());
        if (x % 2 == 0) {
            x = (int) (position.getX() + (x / 2));
            y = (int) (position.getY() + (y / 2));
            Piece piece = getPiece(x, y);
            pieces.remove(piece);
            return true;
        }
        return false;
    }

    // Returns true if the player has jumped onto their opponents
    // kings row and they are not already a king.
    // @param piece the Piece that has made the Move.
    // @return true if the player has jumped onto their opponents
    // kings row and they are not already a king.
    protected boolean jumpOntoKing(Piece piece) {
        if ((piece.getColour().equals(Colour.Red) && piece.getY() == 0 && !piece.isKing())
                || (piece.getColour().equals(Colour.White) && piece.getY() == 7 && !piece.isKing())) {
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
        //TODO;
        Set<Move> newMoveSet = new HashSet<Move>();
        for (Piece piece : pieces) {
            if (piece.getColour() == player) {
                newMoveSet.addAll(validMoves(player, piece, 1, false));
                if (piece.isKing()) {
                    newMoveSet.addAll(validMoves(player, piece, -1, false));
                }
            }
        }

        return newMoveSet;
    }

    // Returns the Set of valid Moves for a normal Piece. These will only be one move ahead.
    // For normal players, yOffset = 1, for king players yOffset = -1. This means you
    // must call this function twice for king players, once with yOffset = 1 and
    // once with yOffset = -1.
    // @param player the Colour of the player to whom the Moves relate.
    // @param piece the Piece to generate the Moves for.
    // @param yOffset the distance to move in the y direction for a Move.
    // @param jumpOnly the boolean which decides whether to calculate valid Moves
    // for only jump Moves.
    // @return the Set of valid Moves for a normal Piece.
    private Set<Move> validMoves(Colour player, Piece piece, int yOffset, boolean jumpOnly) {
        Move thisMove;
        Set<Move> validMoves = new HashSet<Move>();
        if (player.equals(Colour.Red)) yOffset = -yOffset;
        if (!jumpOnly) {
            if (isEmpty(piece.getX() - 1, piece.getY() + yOffset)) {
                thisMove = new Move(piece, piece.getX() - 1, piece.getY() + yOffset);
                validMoves.add(new Move(piece, piece.getX() - 1, piece.getY() + yOffset));
                //System.out.println(thisMove.toString());
            } else if (isEmpty(piece.getX() - 2, piece.getY() + 2 * yOffset) && isTakeable(piece.getX()-1,piece.getY()+yOffset,player)) {
                thisMove = new Move(piece, piece.getX() - 2, piece.getY() + 2 * yOffset);
                validMoves.add(new Move(piece, piece.getX() - 2, piece.getY() + 2 * yOffset));
                //System.out.println(thisMove.toString());
            }
            if (isEmpty(piece.getX() + 1, piece.getY() + yOffset)) {
                thisMove = new Move(piece, piece.getX() + 1, piece.getY() + yOffset);
                validMoves.add(new Move(piece, piece.getX() + 1, piece.getY() + yOffset));
                //System.out.println(thisMove.toString());
            } else if (isEmpty(piece.getX() + 2, piece.getY() + 2 * yOffset) && isTakeable(piece.getX()+1,piece.getY()+yOffset,player)) {
                thisMove = new Move(piece, piece.getX() + 2, piece.getY() + 2 * yOffset);
                validMoves.add(new Move(piece, piece.getX() + 2, piece.getY() + 2 * yOffset));
                //System.out.println(thisMove.toString());
            }

        }

    //TODO: We have given an implementation of how to calculate one of the valid
    // moves (single move to the left), it's your job now to calculate the rest.
    return validMoves;

    }

    // Returns true if the coordinates are empty.
    // If the coordinates are not on the board, it returns false.
    // @return true if the coordinates are empty.
    private boolean isEmpty(int x, int y) {
        if (getPiece(x, y) != null || 0 > x || x > 7 || 0 > y || y > 7) return false;
        return true;
    }

    private boolean isTakeable(int x, int y, Colour colour) {
        if (colour.equals(getPiece(x,y).getColour())) {
            return false;
        }
        return true;
    }

    // If any Pieces are on the other players king row,
    // it sets their king boolean to true.
    protected void checkForKing(Piece piece) {
        if ((piece.getColour().equals(Colour.Red) && piece.getY() == 0)
            || (piece.getColour().equals(Colour.White) && piece.getY() == 7)) piece.setKing(true);
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
        //TODO:

        int red;
        int white;
        red = 0;
        white = 0;

        for (Piece piece : pieces) {
            if (piece.getColour().equals(Colour.Red)) {
                red = red + 1;
            }
            if (piece.getColour().equals(Colour.White)) {
                white = white + 1;
            }
        }
        if (red == 0 || white == 0) {
            return true;
        }
        else if (((validMoves(Colour.Red).size() == 0) && getCurrentPlayer().equals(Colour.Red)) || ((validMoves(Colour.White).size() == 0) && getCurrentPlayer().equals(Colour.White))) {
            return true;
        }
        else {
            return false;
        }
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
