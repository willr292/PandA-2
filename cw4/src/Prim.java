import graph.*;

import java.util.*;

public class Prim {

  private List<Node> visitedNodes;
  private List<Edge<Integer, Integer>> utilEdges;
  private int distance;
  private List<Edge<Integer, Integer>> nearEdges;
  private Edge<Integer, Integer> shortEdge;

    public Prim(Graph<Integer, Integer> graph) {
        //TODO: Using the passed in graph, implement Prims algorithm in this
        // class.
        visitedNodes = new ArrayList<Node>();
        visitedNodes.add(graph.getNode(1));
        utilEdges = new ArrayList<Edge<Integer, Integer>>();
        nearEdges = new ArrayList<Edge<Integer, Integer>>();

      while(visitedNodes.size() != graph.getNodes().size()){
          nearEdges.clear();
          for(Node<Integer> node : visitedNodes){
            scanEdges(node, graph);
          }
          shortEdge = nearEdges.get(0);
          for(Edge<Integer, Integer> nextEdge : nearEdges){
            if(nextEdge.getData() < shortEdge.getData()){
              shortEdge = nextEdge;
            }
          }
          visitedNodes.add(shortEdge.getTarget());
          utilEdges.add(shortEdge);
          graph.remove(shortEdge);
        }

    }

    public void scanEdges(Node sourceNode, Graph<Integer, Integer> graph){
      List<Edge<Integer, Integer>> adjEdges = graph.getEdgesFrom(sourceNode);
      List<Edge<Integer, Integer>> keepEdges = new ArrayList<Edge<Integer, Integer>>();
      for(Edge<Integer, Integer> nextEdge : adjEdges){
        if(!visitedNodes.contains(nextEdge.getTarget())){
          keepEdges.add(nextEdge);
        }
      }
      adjEdges.retainAll(keepEdges);
      if(adjEdges.size() == 0){
        return;
      }
      shortEdge = adjEdges.get(0);
      for(Edge<Integer, Integer> nextEdge : adjEdges){
        if(nextEdge.getData() < shortEdge.getData()){
          shortEdge = nextEdge;
        }
      }
      nearEdges.add(shortEdge);
    }

    public Graph<Integer, Integer> getMinimumSpanningTree() {
        //TODO: You should return a new graph that represents the minimum
        // spanning tree of the graph.
        UndirectedGraph<Integer, Integer> minGraph = new UndirectedGraph<Integer, Integer>();
        for(Node<Integer> node : visitedNodes){
          minGraph.add(node);
        }
        for(Edge<Integer, Integer> edge : utilEdges){
          minGraph.add(edge);
        }
        return minGraph;
    }



}
