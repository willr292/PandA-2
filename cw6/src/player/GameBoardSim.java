package player;

import scotlandyard.*;
import graph.*;
import swing.algorithms.*;
import java.util.*;
import java.io.*;

public class GameBoardSim{
    private Map<Colour, Integer> playerLocs;
    private List<Integer> playerValLocs;
    private Map<Colour, Map<Ticket,Integer>> playerTickets;
    private ScotlandYardGraph graph;
    private Dijkstra dijkstraGraph;
    private int xLoc;
    private int score;

    public GameBoardSim(Map<Colour,Integer> playerLocs, Map<Colour,Map<Ticket,Integer>> playerTickets, List<Move> moves,Dijkstra dijkstraGraph,ScotlandYardGraph graph,int xLoc){
        this.playerLocs = playerLocs;
        this.playerTickets = playerTickets;
        this.xLoc = xLoc;
        this.graph = graph;
        this.dijkstraGraph = dijkstraGraph;

        for(Move m:moves){
          if(m instanceof MoveTicket){
              updateTickets((MoveTicket) m);
          }
          else if(m instanceof MoveDouble){
              updateTickets((MoveDouble) m);
          }
        }
        playerValLocs = new ArrayList<Integer>();
        for(Map.Entry<Colour,Integer> e: playerLocs.entrySet()){
          playerValLocs.add(e.getValue());
        }
    }

    public boolean xWin(){
      boolean check = true;
      for(Map.Entry<Colour,Map<Ticket,Integer>> e : playerTickets.entrySet()){
        if(!e.getKey().equals(Colour.Black)){
          for(Map.Entry<Ticket,Integer> f : e.getValue().entrySet()){
            if(f.getValue()!=0){
              check = false;
            }
          }
        }
      }
      return check;
    }

    public boolean detWin(){
      boolean check = false;
      for(Map.Entry<Colour,Integer> e: playerLocs.entrySet()){
        if(e.getValue()==xLoc){
          check = true;
        }
      }
      return check;
    }

    public Map<Colour,Map<Ticket,Integer>> getPlayerTickets(){
      return playerTickets;
    }

    public void updateTickets(MoveTicket move){
      if(!move.colour.equals(Colour.Black)){
          playerLocs.put(move.colour,move.target);
      }
      else{
        xLoc = move.target;
      }
      int currTick = playerTickets.get(move.colour).get(move.ticket)-1;
      playerTickets.get(move.colour).put(move.ticket,currTick);
  }

    public void updateTickets(MoveDouble move){
        xLoc = move.move2.target;
        int currTick = playerTickets.get(move.colour).get(Ticket.Double);
        playerTickets.get(move.colour).replace(Ticket.Double,currTick-1);
        currTick = playerTickets.get(move.colour).get(move.move1.ticket);
        playerTickets.get(move.colour).replace(move.move1.ticket,currTick-1);
        currTick = playerTickets.get(move.colour).get(move.move2.ticket);
        playerTickets.get(move.colour).replace(move.move2.ticket,currTick-1);
    }

    public int getXLoc(){
      return xLoc;
    }

    public Dijkstra getDijkstraGraph(){
      return dijkstraGraph;
    }

    public ScotlandYardGraph getGraph(){
      return graph;
    }
    public List<List<Move>> getPossXMoves(){
      List<List<Move>> allMoveLists = new ArrayList<List<Move>>();
      for(Move m : generateMoves(Colour.Black,xLoc,playerTickets.get(Colour.Black))){
        List<Move> moveList = new ArrayList<Move>();
        moveList.add(m);
        allMoveLists.add(moveList);

      }
      return allMoveLists;
    }

    private Map<Transport,Integer> getTransport(Colour col){
      Map<Transport,Integer> result = new HashMap<Transport,Integer>();
      for(Ticket t:Ticket.values()){
        if(t.equals(Ticket.Secret)){
          result.put(Transport.valueOf("Boat"),playerTickets.get(col).get(t));
        }
        else if(!t.equals(Ticket.Double)){
          result.put(Transport.valueOf(t.toString()),playerTickets.get(col).get(t));
        }
      }
      return result;
    }
    public List<List<Move>> getPossDetMoves(){
      List<Integer> detLocs = new ArrayList<Integer>();
      Move bestMove = MovePass.instance(Colour.Blue);
      List<Move> moveList = new ArrayList<Move>();
      for(Map.Entry<Colour,Integer> e : playerLocs.entrySet()){
        int highDist = 3000;
        int start = 0;
        for(Move m:generateMoves(e.getKey(),e.getValue(),playerTickets.get(e.getKey()))){
          if(m instanceof MovePass){
            start = e.getValue();
          }
          else{
            start = getTarget((MoveTicket) m);
          }
          int dist = dijkstraGraph.getRoute(xLoc,start,getTransport(m.colour)).size();
          if(highDist>dist && !detLocs.contains(start)){
            highDist = dist;
            bestMove = m;
            detLocs.add(start);
          }
        }
        moveList.add(bestMove);
      }
      List<List<Move>> listMoveList = new ArrayList<List<Move>>();
      listMoveList.add(moveList);
      return listMoveList;
    }

    private int getTarget(MoveTicket move){
      return move.target;
    }

    public Map<Colour,Integer> getPlayerLocs(){
        return playerLocs;
    }

    public int computeScore() {
        int start = xLoc;
        int score = 0;
        int dist = 0;

        //Shortest distance to all detectives is calculated using Djikstras and this becomes the base score.
        for (Map.Entry<Colour, Integer> e : playerLocs.entrySet()) {
            dist = dijkstraGraph.getRoute(start, e.getValue(), getTransport(e.getKey())).size();
            //Moving within one node of a detective is inadvisable so should have a low weight.
            if (start == e.getValue()) {
                score = -500;
            } else if(dist == 2) {
                score = -300;
            } else {
                score = dijkstraGraph.getRoute(start, e.getValue(), getTransport(e.getKey())).size();

            }
        }
        //Outgoing edges from the target node are scored based on transport.
        for (Edge<Integer, Transport> x : graph.getEdgesFrom(graph.getNode(start))) {
            if (x.getData() == Transport.Taxi) {
                score = score + 10;
            }
            if (x.getData() == Transport.Underground) {
                score = score + 3;
            }
            if (x.getData() == Transport.Bus) {
                score = score + 7;
            }
            if (x.getData() == Transport.Boat) {
                score = score + 12;
            }
        }

        //Nodes in Regent's park are a bottleneck and it is difficult for MrX to escape.
        List<Integer> regentsPark = new ArrayList<Integer>(Arrays.asList(2, 20, 10, 21, 33));
        if (regentsPark.contains(start)) {
            score = score - 5;
        }
        return score;
    }

    public List<Move> generateMoves(Colour player, Integer location, Map<Ticket,Integer> tickets) {

        List<Move> validMoves = new ArrayList<Move>();
        List<Edge<Integer,Transport>> nearEdges = graph.getEdgesFrom(graph.getNode(location));
        Move move;

        for(Edge<Integer, Transport> edge : nearEdges){
            //Individual moves for Mr X and all detectives.
            if(!playerValLocs.contains(edge.getTarget().getIndex())) {
                move = MoveTicket.instance(player, Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex());
                validMoves.add(move);
            }

            if(player.equals(Colour.Black)) {
                //Mr X individual secret moves.
                if(!playerValLocs.contains(edge.getTarget().getIndex())) {
                    move = MoveTicket.instance(player, Ticket.Secret, edge.getTarget().getIndex());
                    validMoves.add(move);
                }
                //Mr X Double Moves with variations using secret tickets.
                for(Edge<Integer, Transport> edge2 : graph.getEdgesFrom(edge.getTarget())) {

                    if(!playerValLocs.contains(edge.getTarget().getIndex()) && !playerValLocs.contains(edge2.getTarget().getIndex())) {
                        move = MoveDouble.instance(player, Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex(), Ticket.fromTransport(edge2.getData()), edge2.getTarget().getIndex());
                        validMoves.add(move);
                        move = MoveDouble.instance(player, Ticket.Secret, edge.getTarget().getIndex(), Ticket.fromTransport(edge2.getData()), edge2.getTarget().getIndex());
                        validMoves.add(move);
                        move = MoveDouble.instance(player, Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex(), Ticket.Secret, edge2.getTarget().getIndex());
                        validMoves.add(move);
                        move = MoveDouble.instance(player, Ticket.Secret, edge.getTarget().getIndex(), Ticket.Secret, edge2.getTarget().getIndex());
                        validMoves.add(move);
                    }
                }
            }
        }
        //Remove the move from validMoves if the player does not have enough tickets.
        List<Move> invalidMoves = new ArrayList<Move>();
        for(Move m : validMoves) {

            if(m instanceof MoveTicket) {
                for(Map.Entry<Ticket, Integer> x : findTicket((MoveTicket) m).entrySet()) {
                    if(x.getValue() > tickets.get(x.getKey())){
                        invalidMoves.add(m);

                    }
                }
            } else if(m instanceof MoveDouble) {
                for(Map.Entry<Ticket, Integer> x : findTicket((MoveDouble) m).entrySet()) {
                    if(x.getValue() > tickets.get(x.getKey()) || !player.equals(Colour.Black)){
                        invalidMoves.add(m);
                    }
                }
            }
        }
        validMoves.removeAll(invalidMoves);

        //Add a move pass if the detectives have no valid moves.
        if(validMoves.size() == 0 && !player.equals(Colour.Black)) {

            Move movePass = MovePass.instance(player);
            validMoves.add(movePass);
        }

        return validMoves;
    }

    //Functions for finding what tickets are required for playing a certain move.
    private Map<Ticket, Integer> findTicket(MoveTicket m){
        Map<Ticket, Integer> reqTickets = new HashMap<Ticket, Integer>();
        reqTickets.put(m.ticket, 1);
        return reqTickets;
    }

    private Map<Ticket, Integer> findTicket(MoveDouble m) {
        Map<Ticket, Integer> reqTickets = new HashMap<Ticket, Integer>();
        for(Ticket t : Ticket.values()) {
            reqTickets.put(t, 0);
        }
        reqTickets.put(m.move1.ticket, reqTickets.get(m.move2.ticket) + 1);
        reqTickets.put(m.move2.ticket, reqTickets.get(m.move2.ticket) + 1);
        reqTickets.put(Ticket.Double , 1);
        return reqTickets;
    }


}
