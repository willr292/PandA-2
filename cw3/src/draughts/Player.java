package draughts;

import java.util.Set;
/**
 * An interface to get the Move from a Player.
 */

public interface Player {
    
    /**
     * Returns the Move selected by the Player.
     *
     * @param validMoves the Set of valid Moves a player can take.
     * @return the Move selected by the Player.
     */
    public Move notify(Set<Move> validMoves);
    
}