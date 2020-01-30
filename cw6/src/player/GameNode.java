package player;

import scotlandyard.*;
import graph.*;
import swing.algorithms.*;
import java.util.*;
import java.io.*;

public class GameNode{
  private GameBoardSim state;
  private List<GameNode> children;
  private List<Move> movesMade;
  private int score;

  public GameNode(GameBoardSim state,List<Move> movesMade){
    this.state = state;
    children = new ArrayList<GameNode>();
    this.movesMade = movesMade;
    this.score = state.computeScore();
  }

  public GameNode addChild(List<Move> newMoves){
    GameBoardSim nState = new GameBoardSim(state.getPlayerLocs(),state.getPlayerTickets(),newMoves,state.getDijkstraGraph(),state.getGraph(),state.getXLoc());
    GameNode childNode = new GameNode(nState,newMoves);
    children.add(childNode);
    return childNode;
  }

  public List<GameNode> getChildren(){
    return children;
  }

  public List<List<Move>> getPossXMoves(){
    return state.getPossXMoves();
  }

  public List<List<Move>> getPossDetMoves(){
    return state.getPossDetMoves();
  }

  public int getScore(){
    return score;
  }

  public void setScore(int num){
    score = num;
  }

  public boolean xWin(){
    return state.xWin();
  }

  public boolean detWin(){
    return state.detWin();
  }

  public Move getMoveMade(){
    return movesMade.get(0);
  }

}
