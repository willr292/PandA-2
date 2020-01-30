package draughts;

import java.util.*;
import java.awt.*;
    
/**
 * A class to act as a DPDA to work out when there is enough information for a move.
 */

public class InputPDA {
  
    private DraughtsModel model;
    private Stack<Point> stack;
    private int currentState = 1;
    
    /**
     * Constructs a new InputPDA object.
     *
     * @param model the game model associated with this game.
     */
    public InputPDA(DraughtsModel model) {
        this.model = model;
        stack = new Stack<Point>();
    }
    
    // Returns the top of the stack.
    // @return the top of the stack.
    private Point popStack() {
        Point point = stack.pop();
        return point;
    }
    
    // Pushes a Point onto the top of the stack.
    // @param point the Point to be pushed to the top of the stack.
    private void pushStack(Point point) {
        stack.push(point);
    }
    
    /**
     * Transitions the DPDA according to the input and what 
     * the stack contains.
     * 
     * @param player the Colour of the current player.
     * @param x the x coordinate of the click.
     * @param y the y coordinate of the click.
     */
    public void transition(Colour player, int x, int y) {
        Piece piece = model.getPiece(x, y);
        switch(currentState) {
            case 1:
                if (piece != null && piece.getColour().equals(player)) {
                    pushStack(new Point(x, y));
                    currentState = 2;
                }
                break;
            case 2:
                if (piece != null && piece.getColour().equals(player)) {
                    popStack();
                    pushStack(new Point(x, y));
                    currentState = 2;
                } else if (piece == null) {
                    pushStack(new Point(x, y));
                    currentState = 3;
                }
                break;
            default:
                stack.clear();
                if (piece != null && piece.getColour().equals(player)) {
                    pushStack(new Point(x, y));
                    currentState = 2;
                } else {
                    currentState = 1;
                }
                break;
        }
    }
    
    /**
     * Returns true if the DPDA is in an accept state.
     * 
     * @return true if the DPDA is in an accept state.
     */
    public boolean isAccepted() {
        if (currentState == 3) return true;
        return false;
    }
    
    /**
     * Resets the DPDA to its initial state and empties the stack.
     */
    public void reset() {
        stack.clear();
        currentState = 1;
    }
    
    /**
     * Returns a Move from the contents of the stack.
     *
     * @param colour the colour of the player whose Move is being made.
     * @return the Move for the specified player.
     */
    public Move createMove(Colour colour) {
        Point destination = popStack();
        Point position = popStack();
        Piece piece = model.getPiece((int) position.getX(), (int) position.getY());
        return new Move(piece, (int) destination.getX(), (int) destination.getY());
    }
    
}