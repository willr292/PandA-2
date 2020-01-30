package draughts;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class to save and load games.
 */

public class SaveGame {

    /**
     * The directory where the saved games are stored.
     */
    public static final String SAVE_DIR = "DraughtsSavedGames";

    /**
     * The file extension for saved games.
     */
    public static final String FILE_EXT = ".draughts";

    private Colour currentPlayer;
    private Set<Piece> pieces;

    /**
     * Constructs a new SaveGame object.
     *
     * @param currentPlayer the current player in the game.
     * @param pieces the Set of pieces in the game.
     */
    public SaveGame(Colour currentPlayer, Set<Piece> pieces) {
        this.currentPlayer = currentPlayer;
        this.pieces = pieces;
    }

    /**
     * Returns the current player for this save game.
     *
     * @return the current player for this save game.
     */
    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the Set of Pieces for this save game.
     *
     * @return the Set of Pieces for this save game.
     */
    public Set<Piece> getPieces() {
        return pieces;
    }

    /**
     * Saves a game.
     *
     * @param gameName the name of the game to be saved.
     * @param currentPlayer the current player in the game.
     * @param pieces the Set of Pieces in the game.
     */
    public static void saveGame(String gameName, Colour currentPlayer, Set<Piece> pieces) {
        createDir();
        try {
            File file = new File(SaveGame.SAVE_DIR + "/" + gameName + SaveGame.FILE_EXT);
            file.createNewFile();
            String game = currentPlayer.toString() + "\n";
            for (Piece piece : pieces) {
                game += piece.toString() + "\n";
            }
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(game);
            printWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing save game.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Returns an Array of the previously saved games.
     *
     * @return an Array of the previously saved games.
     */
    public static String[] savedGames() {
        List<String> games = new ArrayList<String>();
        File savedGames = createDir();
        for (File file : savedGames.listFiles()) {
            String name = file.getName();
            if (!file.isDirectory() && name.endsWith(SaveGame.FILE_EXT)) {
                games.add(name.replace(SaveGame.FILE_EXT, ""));
            }
        }
        return games.toArray(new String[games.size()]);
    }

    /**
     * Returns the File of the saved games directory.
     * Creates the directory if it doesn't already exist.
     *
     * @return the File of the saved games directory.
     */
    public static File createDir() {
        File savedGames = new File(SaveGame.SAVE_DIR);
        try {
            if (!savedGames.exists()) savedGames.mkdir();
        } catch (Exception e) {
            System.err.println("Error creating DraughtsSavedGames folder.");
            e.printStackTrace();
            System.exit(1);
        }
        return savedGames;
    }

    /**
     * Returns a SaveGame object containing the data for a saved game.
     *
     * @param gameName the name of the game to be retrieved.
     * @return a SaveGame object containing the data for a saved game.
     */
    public static SaveGame loadGame(String gameName) {
        createDir();
        try {
            File gameFile = new File(SaveGame.SAVE_DIR + "/" + gameName + SaveGame.FILE_EXT);
            Scanner scanner = new Scanner(gameFile);
            Colour currentPlayer = null;
            Set<Piece> pieces = new HashSet<Piece>();
            if (scanner.hasNextLine()) {
                currentPlayer = Colour.valueOf(scanner.nextLine());
                while (scanner.hasNextLine()) {
                    pieces.add(Piece.valueOf(scanner.nextLine()));
                }
            }
            scanner.close();
            return new SaveGame(currentPlayer, pieces);
        } catch (Exception e) {
            System.err.println("Error loading game.");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

}
