package draughts;

import views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.imageio.*;

/**
 * A class to start a new game of English Draughts.
 * Rules for the game: <a href="http://en.wikipedia.org/wiki/English_draughts">http://en.wikipedia.org/wiki/English_draughts</a>
 */

public class Draughts implements Runnable, Player, ActionListener, WindowListener {

    private JFrame window;
    private SetUpView setUp;
    private BoardView board;
    private DraughtsModel model;
    private InputPDA pda;
    private BlockingQueue<Integer> queue;
    private BlockingQueue<String> initQueue;

    /**
     * Called to start the game.
     *
     * @param args the arguments provided by the user.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Draughts draughts = new Draughts();
            SwingUtilities.invokeAndWait(draughts);
            draughts.initialiseGame();
        } catch (Exception e) {
            System.err.println("Error starting game." + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates the window and draws the board.
     */
    public void run() {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.addWindowListener(this);
        board = new BoardView();
        board.setActionListener(this);
        window.add(board);
        setUp = new SetUpView(this);
        window.setGlassPane(setUp);
        setUp.setOpaque(false);
        setUp.setVisible(true);
        window.setTitle("Draughts");
        window.setResizable(false);
        window.pack();
        window.setIconImages(getIcons());
        window.setLocationByPlatform(true);
        window.setVisible(true);
    }

    // Returns a List of icon images.
    // @return a List of icon images.
    private List<Image> getIcons() {
        List<Image> icons = new ArrayList<Image>();
        try {
            icons.add(ImageIO.read(this.getClass().getResource("/icon16.png")));
            icons.add(ImageIO.read(this.getClass().getResource("/icon32.png")));
            icons.add(ImageIO.read(this.getClass().getResource("/icon48.png")));
            icons.add(ImageIO.read(this.getClass().getResource("/icon64.png")));
        } catch (Exception e) {
            System.err.println("Error reading icon files.");
            System.exit(1);
        }
        return icons;
    }

    /**
     * Called when the user clicks on a square on the board.
     *
     * @param e the ActionEvent containing the coordinates
     * of the square that was clicked.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("click")) {
            Point point = (Point) e.getSource();
            if (queue != null) {
                put(queue, Integer.valueOf((int) point.getX()));
                put(queue, Integer.valueOf((int) point.getY()));
            }
        } else if (e.getActionCommand().equals("new_game")) {
            // Start a new game - use a queue to wait for this.
            // Load a dialog to get the name of the game.
            String gameName = getInput("New Game", "Enter the name for the game:", null);
            if (gameName != null) {
                put(initQueue, "new_game");
                put(initQueue, gameName);
            }
        } else if (e.getActionCommand().equals("load_game")) {
            // Load a new game - use a queue to wait for this.
            // Load a dialog to choose game.
            String gameName = getInput("Load Game", "Choose which game to load:", SaveGame.savedGames());
            if (gameName != null) {
                put(initQueue, "load_game");
                put(initQueue, gameName);
            }
        }
    }

    // Returns the String entered by the user (or selected if options != null).
    // @param title the title of the dialog.
    // @param message the message to be displayed to the user.
    // @param options the Array of options for the user to select from.
    // @return the String entered by the user (or selected if options != null).
    private String getInput(String title, String message, String[] options) {
        String input = (String) JOptionPane.showInputDialog(
                                                    window,
                                                    message,
                                                    title,
                                                    JOptionPane.PLAIN_MESSAGE,
                                                    null,
                                                    options,
                                                    null);
        return input;
    }

    // Puts an object on the specified queue.
    // @param queue the Queue to recieve the object.
    // @parma object the Object to be put on the Queue.
    private void put(BlockingQueue queue, Object object) {
        try {
            queue.put(object);
        } catch (InterruptedException e) {
            System.err.println("Interrupted from putting something on the queue.");
        }
    }

    /**
     * Waits until the user has selected whether to
     * load an existing or start a new game.
     */
    public void initialiseGame() {
        try {
            setUp.setVisible(true);
            initQueue = new ArrayBlockingQueue<String>(2);
            String message_id = initQueue.take();
            String game_name = initQueue.take();
            setUp.setVisible(false);
            if (message_id.equals("new_game")) {
                startGame(game_name);
            } else if (message_id.equals("load_game")) {
                loadGame(game_name);
            }
        } catch (InterruptedException e) {
            System.err.println("Interrupted from taking an item from the queue.");
        }
    }

    // Starts a new game of English Draughts.
    private void startGame(String gameName) {
        model = new DraughtsModel(gameName, this);
        playGame();
    }

    // Loads an existing game of English Draughts.
    // @param gameName the name of the game to load.
    private void loadGame(String gameName) {
        SaveGame game = SaveGame.loadGame(gameName);
        model = new DraughtsModel(gameName, this, game.getCurrentPlayer(), game.getPieces());
        playGame();
    }

    // Plays a game of English Draughts.
    private void playGame() {
        queue = new ArrayBlockingQueue<Integer>(1024);
        pda = new InputPDA(model);
        board.update(model.getPieces());
        board.setText(model.getCurrentPlayer().toString() + " Players turn.");
        model.start();
        board.update(model.getPieces());
        board.setText(model.getWinningMessage());
        SaveGame.saveGame(model.getGameName(), model.getCurrentPlayer(), model.getPieces());
        initialiseGame();
    }

    /**
     * Returns the Move selected by the user.
     *
     * @param validMoves the Set of valid Moves the user could take.
     * @return the Move selected by the user.
     */
    public Move notify(Set<Move> validMoves) {
        Colour currentPlayer = model.getCurrentPlayer();
        board.update(model.getPieces());
        board.setText(currentPlayer.toString() + " Players turn.");
        Move move = null;
        while(true) {
            try {
                Integer x = queue.take();
                Integer y = queue.take();
                pda.transition(currentPlayer, x, y);
                board.select(x, y);
                if (pda.isAccepted()) {
                    move = pda.createMove(currentPlayer);
                    if (contains(validMoves, move)) break;
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted from taking something from the queue.");
            }
        }
        pda.reset();
        animatePiece(move);
        board.select(-1, -1);
        return move;
    }

    // Animates a specified Move.
    // @param move the Move to be animated.
    private void animatePiece(Move move) {
        board.setupAnimation(move);
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println("Interrupted from sleeping whilst animating a Piece.");
            }
            board.updateAnimation((double) i / 50.0);
        }
        board.resetAnimation();
    }

    // Returns true if the Set contains the Move.
    // @param moves the Set containing the Moves.
    // @param move the Move to be checked.
    // @return true if the Set contains the Move.
    private boolean contains(Set<Move> moves, Move move) {
        for (Move m : moves) {
            if (m.toString().equals(move.toString())) return true;
        }
        return false;
    }

    /**
     * Called when the window is closing.
     * Saves the current game.
     *
     * @param e the WindowEvent containing information
     * about which window is closing.
     */
    public void windowClosing(WindowEvent e) {
        if (model != null) {
            SaveGame.saveGame(model.getGameName(), model.getCurrentPlayer(), model.getPieces());
        }
    }

    /**
     * Unused method from the WindowListener interface.
     *
     * @param e the WindowEvent containing information about the window.
     */
    public void windowActivated(WindowEvent e) {}

    /**
     * Unused method from the WindowListener interface.
     *
     * @param e the WindowEvent containing information about the window.
     */
    public void windowClosed(WindowEvent e) {}

    /**
     * Unused method from the WindowListener interface.
     *
     * @param e the WindowEvent containing information about the window.
     */
    public void windowDeactivated(WindowEvent e) {}

    /**
     * Unused method from the WindowListener interface.
     *
     * @param e the WindowEvent containing information about the window.
     */
    public void windowDeiconified(WindowEvent e) {}

    /**
     * Unused method from the WindowListener interface.
     *
     * @param e the WindowEvent containing information about the window.
     */
    public void windowIconified(WindowEvent e) {}

    /**
     * Unused method from the WindowListener interface.
     *
     * @param e the WindowEvent containing information about the window.
     */
    public void windowOpened(WindowEvent e) {}

}
