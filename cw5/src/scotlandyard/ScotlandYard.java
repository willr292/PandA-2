package scotlandyard;

import graph.Graph;

import java.io.IOException;
import java.util.*;

/**
 * A class to perform all of the game logic.
 */

public class ScotlandYard implements ScotlandYardView, Receiver {

    protected MapQueue<Integer, Token> queue;
    protected Integer gameId;
    protected Random random;
    protected List<Boolean> rounds;
    protected Integer numberOfDetectives;
    protected List<PlayerData> playerList;
    protected int xLoc = 0;
    protected int xRound=0;
    protected int playerCount = 0;
    protected ScotlandYardGraph graph;
    protected List<Spectator> spectatorList;


    /**
     * Constructs a new ScotlandYard object. This is used to perform all of the game logic.
     *
     * @param numberOfDetectives the number of detectives in the game.
     * @param rounds the List of booleans determining at which rounds Mr X is visible.
     * @param graph the graph used to represent the board.
     * @param queue the Queue used to put pending moves onto.
     * @param gameId the id of this game.
     */
    public ScotlandYard(Integer numberOfDetectives, List<Boolean> rounds, ScotlandYardGraph graph, MapQueue<Integer, Token> queue, Integer gameId) {
        this.queue = queue;
        this.gameId = gameId;
        this.random = new Random();
        //TODO:
        this.rounds = rounds;
        this.numberOfDetectives = numberOfDetectives;
        playerList = new ArrayList<PlayerData>();
        xLoc = 0;
        playerCount = 0;
        this.graph = graph;
        spectatorList = new ArrayList<Spectator>();

    }

    /**
     * Starts playing the game.
     */
    public void startRound() {
        if (isReady() && !isGameOver()) {
            turn();
        }
    }

    /**
     * Notifies a player when it is their turn to play.
     */
    public void turn() {

        Integer token = getSecretToken();
        queue.put(gameId, new Token(token, getCurrentPlayer(), System.currentTimeMillis()));
        notifyPlayer(getCurrentPlayer(), token);
    }

    /**
     * Plays a move sent from a player.
     *
     * @param move the move chosen by the player.
     * @param token the secret token which makes sure the correct player is making the move.
     */
    public void playMove(Move move, Integer token) {
        Token secretToken = queue.get(gameId);
        if (secretToken != null && token == secretToken.getToken()) {
            queue.remove(gameId);
            play(move);

            nextPlayer();
            startRound();
        }
    }

    /**
     * Returns a random integer. This is used to make sure the correct player
     * plays the move.
     * @return a random integer.
     */
    private Integer getSecretToken() {
        return random.nextInt();
    }

    /**
     * Notifies a player with the correct list of valid moves.
     *
     * @param colour the colour of the player to be notified.
     * @param token the secret token for the move.
     */
    private void notifyPlayer(Colour colour, Integer token) {
        //TODO:
        for(PlayerData player : playerList){
            if(player.getColour().equals(colour)){
                player.getPlayer().notify(player.getLocation(), validMoves(colour), token, this);
            }
        }
    }


    /**
     * Passes priority onto the next player whose turn it is to play.
     */
    protected void nextPlayer() {
        //TODO:
        if(playerCount==numberOfDetectives){
            playerCount=0;
        }
        else playerCount++;
    }

    /**
     * Allows the game to play a given move.
     *
     * @param move the move that is to be played.
     */
    protected void play(Move move) {
        if (move instanceof MoveTicket){
            play((MoveTicket) move);
            if(move.colour.equals(Colour.Black))xRound++;
        }
        else if (move instanceof MoveDouble){
            play((MoveDouble) move);
        }
        else if (move instanceof MovePass){
            play((MovePass) move);
            if(move.colour.equals(Colour.Black))xRound++;
        }
        // System.out.println(move.toString());
    }

    /**
     * Plays a MoveTicket.
     *
     * @param move the MoveTicket to play.
     */
    protected void play(MoveTicket move) {
        //TODO:
        findPlayer(getCurrentPlayer()).setLocation(move.target);
        findPlayer(getCurrentPlayer()).removeTicket(move.ticket);
        MoveTicket sMove = move;
        if (move.colour.equals(Colour.Black)){
            sMove = MoveTicket.instance(Colour.Black, move.ticket, getPlayerLocation(Colour.Black));
        }
        else{
            findPlayer(Colour.Black).addTicket(move.ticket);
        }

        for(Spectator s : spectatorList) {
            s.notify(sMove);
        }
    }

    /**
     * Plays a MoveDouble.
     *
     * @param move the MoveDouble to play.
     */
    protected void play(MoveDouble move) {
        //TODO:
        for(Spectator s : spectatorList){
            s.notify(move);
        }
        findPlayer(getCurrentPlayer()).setLocation(move.move1.target);
        findPlayer(getCurrentPlayer()).removeTicket(move.move1.ticket);
        xRound++;
        Move sMove = MoveTicket.instance(Colour.Black,move.move1.ticket,getPlayerLocation(Colour.Black));
        for(Spectator s : spectatorList){
            s.notify(sMove);
        }

        findPlayer(getCurrentPlayer()).setLocation(move.move2.target);
        findPlayer(getCurrentPlayer()).removeTicket(move.move2.ticket);
        findPlayer(getCurrentPlayer()).removeTicket(Ticket.Double);
        xRound++;
        sMove = MoveTicket.instance(Colour.Black,move.move2.ticket,getPlayerLocation(Colour.Black));
        for(Spectator s : spectatorList) {
            s.notify(sMove);
        }


    }

    /**
     * Plays a MovePass.
     *
     * @param move the MovePass to play.
     */
    protected void play(MovePass move) {
        //TODO:
        Move sMove = MovePass.instance(getCurrentPlayer());
        for(Spectator s : spectatorList) {
            s.notify(sMove);
        }

    }

    /**
     * Returns the list of valid moves for a given player.
     *
     * @param player the player whose moves we want to see.
     * @return the list of valid moves for a given player.
     */
    public List<Move> validMoves(Colour player) {
        //TODO:
        List<Integer> playerLocs = new ArrayList<Integer>();
        for(Colour p : getPlayers()){
            if (!p.equals(Colour.Black)){
                playerLocs.add(findPlayer(p).getLocation());
            }
        }
        List<Move> validMoves = graph.generateMoves(player,findPlayer(player).getLocation(),findPlayer(player).getTickets(),playerLocs);
        /*
        if(validMoves.size() == 0 && !player.equals(Colour.Black)) {
            List<Move> noMoves = new ArrayList<Move>();
            Move movePass = MovePass.instance(player);
            noMoves.add(movePass);
            return noMoves;
        } else */
        return validMoves;
    }

    public PlayerData findPlayer(Colour colour){
        for(PlayerData player : playerList){
            if(player.getColour().equals(colour)){
                return player;
            }
        }
        return null;
    }

    /**
     * Allows spectators to join the game. They can only observe as if they
     * were a detective: only MrX's revealed locations can be seen.
     *
     * @param spectator the spectator that wants to be notified when a move is made.
     */
    public void spectate(Spectator spectator) {
        //TODO:
        spectatorList.add(spectator);

    }

    /**
     * Allows players to join the game with a given starting state. When the
     * last player has joined, the game must ensure that the first player to play is Mr X.
     *
     * @param player the player that wants to be notified when he must make moves.
     * @param colour the colour of the player.
     * @param location the starting location of the player.
     * @param tickets the starting tickets for that player.
     * @return true if the player has joined successfully.
     */
    public boolean join(Player player, Colour colour, int location, Map<Ticket, Integer> tickets) {
        //TODO:

        if(!getPlayers().contains(colour)){
            if(!colour.equals(Colour.Black)){

            }
            playerList.add(new PlayerData(player,colour,location,tickets));
            return true;
        }
        else return false;
    }

    /**
     * A list of the colours of players who are playing the game in the initial order of play.
     * The length of this list should be the number of players that are playing,
     * the first element should be Colour.Black, since Mr X always starts.
     *
     * @return The list of players.
     */
    public List<Colour> getPlayers() {
        //TODO:
        List<Colour> colourList = new ArrayList<Colour>();
        for(PlayerData player : playerList){
            colourList.add(player.getColour());
        }

        return colourList;
    }

    /**
     * Returns the colours of the winning players. If Mr X it should contain a single
     * colour, else it should send the list of detective colours
     *
     * @return A set containing the colours of the winning players
     */
    public Set<Colour> getWinningPlayers() {
        //TODO:
        Set<Colour> winSet = new HashSet<Colour>();
        boolean hasTickets = false;
        boolean playWin = false;
        boolean hasValMoves = false;


        if(isGameOver()) {

            for (Colour p : getPlayers()) {
                if(!p.equals(Colour.Black)) {
                    if (findPlayer(p).getLocation() == findPlayer(Colour.Black).getLocation()) playWin = true;
                }
            }

            if (validMoves(Colour.Black).size() == 0 ) playWin = true;

            if(playWin){
                for(Colour p : getPlayers()){
                    if(!p.equals(Colour.Black))winSet.add(p);
                }
                return winSet;
            }

            for(Colour p : getPlayers()){
                if(!p.equals(Colour.Black)) {
                    for (Map.Entry<Ticket, Integer> m : findPlayer(p).getTickets().entrySet()) {
                        if (m.getValue() > 0) hasTickets = true;
                    }
                    if(!(validMoves(p).size()==1 && validMoves(p).get(0) instanceof MovePass))hasValMoves = true;}
            }

            if (!hasTickets || !hasValMoves) {
                winSet.add(Colour.Black);
                return winSet;

            };

        }
        return winSet;
    }

    /**
     * The location of a player with a given colour in its last known location.
     *
     * @param colour The colour of the player whose location is requested.
     * @return The location of the player whose location is requested.
     * If Black, then this returns 0 if MrX has never been revealed,
     * otherwise returns the location of MrX in his last known location.
     * MrX is revealed in round n when {@code rounds.get(n)} is true.
     */
    public int getPlayerLocation(Colour colour) {
        //TODO:
        for(PlayerData player : playerList){
            if(player.getColour().equals(colour)){
                if(player.getColour().equals(Colour.Black)) {
                    if(rounds.get(getRound())){
                        xLoc = player.getLocation();
                    }
                    return xLoc;
                }
                return player.getLocation();
            }
        }
        return 0;
    }

    /**
     *
     * The number of a particular ticket that a player with a specified colour has.
     *
     * @param colour The colour of the player whose tickets are requested.
     * @param ticket The type of tickets that is being requested.
     * @return The number of tickets of the given player.
     */
    public int getPlayerTickets(Colour colour, Ticket ticket) {
        int num = 0;
        for(PlayerData player : playerList){
            if(player.getColour().equals(colour)){
                return player.getTickets().get(ticket);
            }
        }
        return -1;
    }

    /**
     * The game is over when MrX has been found or the agents are out of
     * tickets. See the rules for other conditions.
     *
     * @return true when the game is over, false otherwise.
     */
    public boolean isGameOver() {
        boolean hasValMoves = false;
        if(!isReady()) return false;
        boolean haveTickets = false;
        for(Colour p : getPlayers()){

            if(!p.equals(Colour.Black)) {
                for (Map.Entry<Ticket, Integer> m : findPlayer(p).getTickets().entrySet()) {
                    if (m.getValue() > 0) haveTickets = true;
                }
                if(!(validMoves(p).size()==1 && validMoves(p).get(0) instanceof MovePass))hasValMoves = true;
                if(findPlayer(p).getLocation() == findPlayer(Colour.Black).getLocation()) return true;
            }
        }
        if(xRound>=rounds.size()-1 && getCurrentPlayer().equals(Colour.Black)) return true;
        if(getPlayers().size()==1 && getPlayers().get(0).equals(Colour.Black)) return true;
        if(!haveTickets || !hasValMoves)return true;
        if(validMoves(Colour.Black).size() == 0) return true;

        return false;
    }

    /**
     * A game is ready when all the required players have joined.
     *
     * @return true when the game is ready to be played, false otherwise.
     */
    public boolean isReady() {
        if(getPlayers().size() == numberOfDetectives+1) {
            return true;
        }
        return false;
    }

    /**
     * The player whose turn it is.
     *
     * @return The colour of the current player.
     */
    public Colour getCurrentPlayer() {
        return getPlayers().get(playerCount);
    }

    /**
     * The round number is determined by the number of moves MrX has played.
     * Initially this value is 0, and is incremented for each move MrX makes.
     * A double move counts as two moves.
     *
     * @return the number of moves MrX has played.
     */
    public int getRound() {
        //TODO:
        return xRound;
    }

    /**
     * A list whose length-1 is the maximum number of moves that MrX can play in a game.
     * The getRounds().get(n) is true when MrX reveals the target location of move n,
     * and is false otherwise.
     * Thus, if getRounds().get(0) is true, then the starting location of MrX is revealed.
     *
     * @return a list of booleans that indicate the turns where MrX reveals himself.
     */
    public List<Boolean> getRounds() {
        //TODO:
        return rounds;
    }

}
