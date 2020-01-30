import graph.*;

import java.util.*;


public class Dijkstra {
  private List<Node<Integer>> shortestPath;
  private List<Node<Integer>> visitedNodes;
  private List<Integer> totDist;
  private List<Edge<Integer, Integer>> nearEdges;
  private List<Node<Integer>> queue;
  private List<List<Edge<Integer,Integer>>> paths;
  //private Node<Integer> vertex;
    public Dijkstra(Graph<Integer, Integer> graph) {
      System.out.println("\n\n");
        //TODO: Using the passed in graph, implement Dijkstras algorithm in this
        // class.
        visitedNodes = new ArrayList<Node<Integer>>();
        queue = graph.getNodes();
        totDist = new ArrayList<Integer>();
        nearEdges = new ArrayList<Edge<Integer, Integer>>();
        paths = new ArrayList<List<Edge<Integer,Integer>>>();


      //  vertex = new Node<Integer>(5);
        //Edge<Integer,Integer> nearestEdge = new Edge<Integer,Integer>();
        for(int i = 0;i<graph.getNodes().size();i++){
          totDist.add(i,Integer.MAX_VALUE);
          paths.add(new ArrayList<Edge<Integer,Integer>>());
        }
        totDist.set(0,0);
        while(queue.size()!=visitedNodes.size()){
          Node<Integer> vertex = queue.get(minDist(totDist, graph));
          visitedNodes.add(vertex);
          nearEdges = findNeighbours(vertex,graph);
          for(Edge<Integer, Integer> currentEdge : nearEdges){
            if(totDist.get(currentEdge.getTarget().getIndex()- 1) > totDist.get(vertex.getIndex() - 1) + currentEdge.getData()) {
              totDist.set((currentEdge.getTarget().getIndex()- 1), (totDist.get(vertex.getIndex() - 1) + currentEdge.getData()));
              paths.get(currentEdge.getTarget().getIndex()- 1).add(currentEdge.swap());
              System.out.println("Adding to path [" + (currentEdge.getTarget().getIndex()- 1) + "] node " + vertex.toString());
              //System.out.println("Setting distance["+(currentEdge.getTarget().getIndex()- 1)+"] to "+(totDist.get(vertex.getIndex() - 1) + currentEdge.getData()));
            }
          }
        }
        String visitedNodesStr = "{";
        for(List<Edge<Integer,Integer>> path : paths){
          for(Edge<Integer,Integer> edge : path) {
            visitedNodesStr = visitedNodesStr + edge.toString() + " ,";
          }
          System.out.println(visitedNodesStr+"}");
          visitedNodesStr = "{";
        }

    }

  /*  public Edge<Integer, Integer> scanNode(Node<Integer> node, Graph<Integer, Integer> graph){
      List<Integer> locDist = new ArrayList<Integer>();
      List<Edge<Integer, Integer>> adjEdges = graph.getEdgesFrom(sourceNode);
      List<Edge<Integer, Integer>> keepEdges = new ArrayList<Edge<Integer, Integer>>();
      for(Edge<Integer, Integer> nextEdge : graph.getEdgesFrom(node)){
        if(!visitedNodes.contains(nextEdge.getTarget())){
          keepEdges.add(nextEdge);
        }
      }
      adjEdges.retainAll(keepEdges);
      if(adjEdges.size() == 0){
        return NULL;
      }
      Edge<Integer, Integer> shortEdge = adjEdges.get(0);
      for(Edge<Integer, Integer> nextEdge : adjEdges){
        if(nextEdge.getData() < shortEdge.getData()){
          shortEdge = nextEdge;
        }
      }
      return shortEdge;
    }*/

    public List<Edge<Integer,Integer>> findNeighbours(Node<Integer> node, Graph<Integer,Integer> graph){
      List<Edge<Integer, Integer>> adjEdges = graph.getEdgesFrom(node);
      List<Edge<Integer, Integer>> keepEdges = new ArrayList<Edge<Integer, Integer>>();
      for(Edge<Integer, Integer> nextEdge : adjEdges){
        if(!visitedNodes.contains(nextEdge.getTarget())){
          keepEdges.add(nextEdge);
        }
      }
      adjEdges.retainAll(keepEdges);
      return adjEdges;
    }

    public Integer minDist(List<Integer> totDist, Graph<Integer, Integer> graph){
      Integer min = Integer.MAX_VALUE;
      Integer ind = 0;
      //Node<Integer> minNode = new Node<Integer>();
      for(int i = 0; i<totDist.size();i++){
        // System.out.println("hi");
         //System.out.println(!visitedNodes.contains(graph.getNode(i)));
        if(totDist.get(i)<min && !visitedNodes.contains(graph.getNode(i+1))){
          ind = i;
          min = totDist.get(i);
        }
      }
      return ind;
    }

  /*  public void updateDist(Node<Integer> node, List<Integer> totDist){
      List<Edge<Integer, Integer>> adjEdges = graph.getEdgesFrom(node);
      for(Edge<Integer, Integer> currentEdge : adjEdges){
        if(totDist.get(currentEdge.getTarget().getIndex()-1)+currentEdge.getData()<)
      }
    }
*/
    /*public Integer findMin(List<Integer> list){
      if(list.size() == 0){
        return NULL;
      }
      else if(list.size() == 1){
        return list.get(0);
      }
      else{
        Integer min = list.get(0);
      }
      for(Integer num : list){
        if(num<min){
          min = num;
        }
      }
      return min;
    } */

    public List<Integer> shortestPath(Integer origin, Integer destination) {
        //TODO: You should return an ordered list of the node the indecies you
        // visit in your shortest path from origin to destination.
        //return paths.get(destination-1);
        List<Edge<Integer,Integer>> shortPath = paths.get(destination-1);
        List<Integer> finalPath = new ArrayList<Integer>();
        while(shortPath.get(shortPath.size()-1).getTarget().getIndex() != origin){
          finalPath.add(shortPath.get(shortPath.size()-1).getSource().getIndex());
          shortPath = paths.get(shortPath.get(shortPath.size()-1).getTarget().getIndex()-1);
        }
        finalPath.add(shortPath.get(0).getSource().getIndex());
        finalPath.add(shortPath.get(0).getTarget().getIndex());
        Collections.reverse(finalPath);
        String visitedNodesStr = "{";
        for(Integer node : finalPath) {
          visitedNodesStr = visitedNodesStr + node + " ,";
        }
        System.out.println(visitedNodesStr+"}");
        return finalPath;
    }

}
