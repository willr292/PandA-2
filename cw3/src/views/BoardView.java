package views;

import draughts.*;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 * A view to display the board and the pieces.
 */

public class BoardView extends JPanel implements MouseListener {

    private static final long serialVersionUID = -4369812185188908867L;

    private static final int maxY = 7;
    private static final int minY = 0;
    private static final int maxX = 7;
    private static final int minX = 0;
    private static final int wBorder = 35;
    private static final int hBorder = 35;
    private static final int squareSize = 50;
    private static int boardSize = 8 * squareSize;
    private BufferedImage king;
    private JLabel label;
    private Set<Piece> pieces;
    private ActionListener listener;
    private Point selected = new Point(-1, -1);
    private Piece animatedPiece;
    private int x, y, startX, startY;
    private Point start, destination;

    /**
     * Constructs a new BoardView object.
     */
    public BoardView() {
        try {
            setPreferredSize(new Dimension(boardSize + (2 * wBorder), boardSize + (2 * hBorder)));
            pieces = new HashSet<Piece>();
            king = ImageIO.read(this.getClass().getResource("/king.png"));
            label = new JLabel("");
            label.setForeground(new Color(49, 49, 51));
            label.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
            add(label);
            addMouseListener(this);
        } catch (Exception e) {
            System.err.println("Error reading king file.");
            System.exit(1);
        }
    }

    /**
     * Draws the board and pieces.
     *
     * @param g0 the Graphics object to draw to.
     */
    public void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillRect(0, 0, boardSize + (2 * wBorder), boardSize + (2 * hBorder));
        g.setColor(Color.green.darker().darker());
        int wInner = wBorder - 2;
        int hInner = hBorder - 2;
        g.fillRect(wInner, hInner, (boardSize + 4), (boardSize + 4));
        boolean white = true;
        for (int i = 0, j = wBorder; i <= maxX; i++, j += squareSize) {
            for (int k = 0, l = hBorder; k <= maxY; k++, l += squareSize) {
                if (white) g.setColor(Color.white);
                else g.setColor(Color.green.darker().darker());
                g.fillRect(j, l, squareSize, squareSize);
                white = !white;
                drawPiece(getPiece(i, k), j, l, g);
            }
            white = !white;
        }
        drawPiece(animatedPiece, x, y, g);
    }

    // Draws a Piece.
    // @param piece the Piece to be drawn.
    // @param dX the x coordinate of the upper left corner of
    // the square that will contain the Piece.
    // @param dY the y coordinate of the upper left corner of
    // the square that will contain the Piece.
    // @param g the Graphics2D object to be drawn to.
    private void drawPiece(Piece piece, int dX, int dY, Graphics2D g) {
        if (piece != null) {
            g.setColor(new Color(49, 49, 51));
            g.fillOval(dX + 6, dY + 6, squareSize - 9, squareSize - 9);
            g.setColor(getColor(piece.getColour(), piece.getX(), piece.getY()));
            g.fillOval(dX + 5, dY + 5, squareSize - 10, squareSize - 10);
            if (piece.isKing()) g.drawImage(king, dX, dY, squareSize, squareSize, null);
        }
    }

    // Returns the correct Color for the Piece.
    // @param player the Colour of the current player.
    // @param x the x coordinate of the Piece.
    // @param y the y coordinate of the Piece.
    // @return the correct Color for the Piece.
    private Color getColor(Colour player, int x, int y) {
        int alpha = 255;
        if (x == (int) selected.getX() && y == (int) selected.getY()) alpha = 200;
        if (player.equals(Colour.Red)) return new Color(204, 0, 0, alpha);
        else return new Color(255, 250, 250, alpha);
    }

    // Returns the Piece with the specified coordinates.
    // @param x the x coordinate of the Piece.
    // @param y the y coordinate of the Piece.
    // @return the Piece with the specified coordinates.
    private Piece getPiece(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) return piece;
        }
        return null;
    }

    /**
     * Sets the alpha of the selected Piece.
     *
     * @param x the x coordinate of the selected Piece.
     * @param y the y coordinate of the selected Piece.
     */
    public void select(int x, int y) {
        selected = new Point(x, y);
        repaint();
    }

    /**
     * Updates the Set of Pieces.
     *
     * @param pieces the new Set of Pieces.
     */
    public void update(Set<Piece> pieces) {
        this.pieces = pieces;
        repaint();
    }

    /**
     * Sets up the information required to animate a Piece.
     *
     * @param move the Move to be animated.
     */
    public void setupAnimation(Move move) {
        animatedPiece = move.piece;
        startX = wBorder + (move.piece.getX() * squareSize);
        startY = hBorder + (move.piece.getY() * squareSize);
        x = startX;
        y = startY;
        pieces.remove(move.piece);
        start = new Point(move.piece.getX(), move.piece.getY());
        destination = move.destination;
    }

    /**
     * Updates the information required to animate a Piece.
     *
     * @param t the fraction of where the animation is in
     * relation to the start of the animation.
     */
    public void updateAnimation(double t) {
        Point point = calcBezier(t);
        x = startX + (int) point.getX();
        y = startY + (int) point.getY();
        repaint();
    }

    // Returns the Point containing the offset to the start coordinates.
    // @param t the fraction of where the Point is in relation
    // to the start and end points.
    // @return the Point containing the offset to the start coordinates.
    private Point calcBezier(double t) {
        int xDiff = (int) destination.getX() - (int) start.getX();
        int yDiff = (int) destination.getY() - (int) start.getY();
        if (xDiff % 2 == 0) {
            if (xDiff < 0 && yDiff < 0) {
                return cubicBezier(new Point(0, 0), new Point(-25, -50), new Point(-50, -75), new Point(-100, -100), t);
            } else if (xDiff > 0 && yDiff < 0) {
                return cubicBezier(new Point(0, 0), new Point(25, -50), new Point(50, -75), new Point(100, -100), t);
            } else if (xDiff > 0 && yDiff > 0) {
                return cubicBezier(new Point(0, 0), new Point(50, 25), new Point(75, 50), new Point(100, 100), t);
            } else if (xDiff < 0 && yDiff > 0) {
                return cubicBezier(new Point(0, 0), new Point(-50, 25), new Point(-75, 50), new Point(-100, 100), t);
            }
        } else {
            if (xDiff < 0 && yDiff < 0) {
                return cubicBezier(new Point(0, 0), new Point(0, 0), new Point(-50, -50), new Point(-50, -50), t);
            } else if (xDiff > 0 && yDiff < 0) {
                return cubicBezier(new Point(0, 0), new Point(0, 0), new Point(50, -50), new Point(50, -50), t);
            } else if (xDiff > 0 && yDiff > 0) {
                return cubicBezier(new Point(0, 0), new Point(0, 0), new Point(50, 50), new Point(50, 50), t);
            } else if (xDiff < 0 && yDiff > 0) {
                return cubicBezier(new Point(0, 0), new Point(0, 0), new Point(-50, 50), new Point(-50, 50), t);
            }
        }
        return null;
    }

    // Returns a Point containing the coordinates of the line
    // for a certain Bezier curve.
    // @param p0 the P0 Point defining the curve.
    // @param p1 the P1 Point defining the curve.
    // @param p2 the P2 Point defining the curve.
    // @param p3 the P3 Point defining the curve.
    // @param t the fraction of where the Point is in relation
    // to the start and end points.
    // @return a Point containing the coordinates of the line
    // for a certain Bezier curve.
    private Point cubicBezier(Point p0, Point p1, Point p2, Point p3, double t) {
        double ti = (1 - t);
        double x = (ti * ti * ti * p0.getX()) + (3 * ti * ti * t * p1.getX())
                    + (3 * ti * t * t * p2.getX()) + (t * t * t * p3.getX());
        double y = (ti * ti * ti * p0.getY()) + (3 * ti * ti * t * p1.getY())
                    + (3 * ti * t * t * p2.getY()) + (t * t * t * p3.getY());
        return new Point((int) x, (int) y);
    }

    /**
     * Resets the information required to animate a Piece.
     */
    public void resetAnimation() {
        pieces.add(animatedPiece);
        animatedPiece = null;
    }

    /**
     * Sets the text of the JLabel in the view.
     *
     * @param message the message to be displayed in the JLabel.
     */
    public void setText(String message) {
        label.setText(message);
    }

    /**
     * Adds the specified ActionListener to receive when the user clicks on a square.
     * If listener listener is null, no action is performed.
     *
     * @param listener the listener to be added to the view.
     */
    public void setActionListener(ActionListener listener) {
        this.listener = listener;
    }

    /**
     * Sends an ActionEvent to the specified ActionListener when the user clicks on a square.
     *
     * @param e the MouseEvent containing the location of the click.
     */
    public void mouseReleased(MouseEvent e) {
        if (35 <= e.getX() && e.getX() < 435 && 35 <= e.getY() && e.getY() < 435) {
            int x = (int) Math.ceil(((e.getX() - 35) * 2) / 100);
            int y = (int) Math.ceil(((e.getY() - 35) * 2) / 100);
            if (listener != null) {
                listener.actionPerformed(new ActionEvent(new Point(x, y), 0, "click"));
            }
        }
    }

    /**
     * Unused method from the MouseListener interface.
     *
     * @param e the MouseEvent sent when the mouse button is released.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Unused method from the MouseListener interface.
     *
     * @param e the MouseEvent sent when the mouse button is pressed.
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Unused method from the MouseListener interface.
     *
     * @param e the MouseEvent sent when the mouse cursor enters the view.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Unused method from the MouseListener interface.
     *
     * @param e the MouseEvent sent when the mouse cursor leaves the view.
     */
    public void mouseExited(MouseEvent e) {}

}
