package player;

import scotlandyard.*;
import graph.*;
import swing.algorithms.*;
import java.util.*;
import java.io.*;

public class MrXAI implements Player{
    private Map<Colour, Integer> playerLocs;
    private Map<Colour, Map<Ticket,Integer>> playerTickets;
    private Dijkstra dijkstraGraph;
    ScotlandYardGraph boardGraph;
    ScotlandYardView view;


    public MrXAI(ScotlandYardView view, String graphFilename, Dijkstra dijkstraGraph) {
        this.view = view;
        this.dijkstraGraph = dijkstraGraph;

        //Creates a new reader object and reads in the graph, throwing an exception if it is unable to.
        ScotlandYardGraphReader reader = new ScotlandYardGraphReader();
        try {
            this.boardGraph = reader.readGraph(graphFilename);
        }
        catch(IOException e){
            System.out.println("Could not read graph.");
        }


        //Creating the data structures to hold the players locations, type of tickets and no. of tickets.
        playerLocs = new HashMap<Colour, Integer>();
        playerTickets = new HashMap<Colour, Map<Ticket, Integer>>();
        Map<Ticket, Integer> currTickets = new HashMap<Ticket, Integer>();
        currTickets.put(Ticket.Double,2);
        currTickets.put(Ticket.Secret,5);
        playerTickets.put(Colour.Black,currTickets);
    }

    public Move miniMax(GameNode currentNode,int depth) {
        GameNode bestNode = maxMove(currentNode,depth);
        return bestNode.getMoveMade();
    }

    public int checkWinScore(GameNode currentNode, int depth){
      if(depth == 0){
        return currentNode.getScore();
      }
      if(currentNode.detWin()){
        return -1000;
      }
      if(currentNode.xWin()){
        System.out.println("Xwin");
        return 1000;
      }
      return -1;
    }

    public GameNode maxMove(GameNode currentNode, int depth){
      GameNode bestNode = currentNode;
      int score = -10000;
      if(checkWinScore(currentNode,depth)!=-1){
        currentNode.setScore(checkWinScore(currentNode,depth));
        return currentNode;
      }
      else{
        List<List<Move>> moves = currentNode.getPossXMoves();
        for(List<Move> lm:moves){
          GameNode nextNode = currentNode.addChild(lm);
          GameNode moveNode = minMove(nextNode,depth-1);
          if(moveNode.getScore()>score){
            bestNode = moveNode;
            score = moveNode.getScore();
          }
        }
      }
      return bestNode;
    }

    public GameNode minMove(GameNode currentNode, int depth){
      GameNode bestNode = currentNode;
      int score = 10000;
      if(checkWinScore(currentNode,depth)!=-1){
        currentNode.setScore(checkWinScore(currentNode,depth));
        return currentNode;
      }
      else{
        List<List<Move>> moves = currentNode.getPossDetMoves();
        for(List<Move> lm:moves){
          GameNode nextNode = currentNode.addChild(lm);
          GameNode moveNode = maxMove(nextNode,depth-1);
          if(moveNode.getScore()<score){
            bestNode = moveNode;
            score = moveNode.getScore();
          }
        }
      }
      return bestNode;
    }

    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
      for(Colour c : view.getPlayers()){
        if(!c.equals(Colour.Black)){
          playerLocs.put(c,view.getPlayerLocation(c));
        }
        Map<Ticket,Integer> currTickets = new HashMap<Ticket,Integer>();
        for(Ticket t: Ticket.values()){
          currTickets.put(t,view.getPlayerTickets(c,t));
        }
        playerTickets.put(c,currTickets);
      }
      List<Move> firstMove = new ArrayList<Move>();
      firstMove.add(MovePass.instance(Colour.Black));
      GameBoardSim firstState = new GameBoardSim(playerLocs,playerTickets,firstMove,dijkstraGraph,boardGraph,location);
      GameNode firstNode = new GameNode(firstState,moves);
      Move bestMove = miniMax(firstNode,1);
      if(bestMove instanceof MoveTicket){
        updateMrXTickets((MoveTicket) bestMove);
      }
      else if(bestMove instanceof MoveDouble){
        updateMrXTickets((MoveDouble) bestMove);
      }
      receiver.playMove(bestMove, token);
    }

    private void updateMrXTickets(MoveTicket move){
      int currTicks = playerTickets.get(Colour.Black).get(move.ticket);
      playerTickets.get(Colour.Black).replace(move.ticket,currTicks-1);
    }
    private void updateMrXTickets(MoveDouble move){
      int currTicks = playerTickets.get(Colour.Black).get(Ticket.Double);
      playerTickets.get(Colour.Black).replace(Ticket.Double,currTicks-1);
      updateMrXTickets((MoveTicket) move.move1);
      updateMrXTickets((MoveTicket) move.move2);
    }
}
