import graph.*;

import java.util.*;

public class Dijkstra {

    public Dijkstra(Graph<Integer, Integer> graph) {
        //TODO: Using the passed in graph, implement Dijkstras algorithm in this
        // class.
        List<Node<Integer>> visitedNodes = new ArrayList<Node<Integer>>();
        Node source = graph.getNode(0);
        List<Edge<Integer, Integer>> shortestEstimates = new ArrayList<Edge<Integer, Integer>>();
        List<Integer> distances = new ArrayList<Integer>();
        visitedNodes.add(source);


        shortestEstimates = graph.getEdgesFrom(source);
        Edge<Integer, Integer> minimum = shortestEstimates.get(0);

        for (Edge<Integer, Integer> edge : shortestEstimates) {
            distances.add(edge.getData());
        }


        while (!visitedNodes.equals(graph.getNodes())) {



            for (Edge<Integer, Integer> currentEdge : shortestEstimates) {

                if (currentEdge.getData() < minimum.getData()) {
                    minimum = currentEdge;
                }


            }

            visitedNodes.add(minimum.getTarget());

        }


    }

    public List<Integer> shortestPath(Integer origin, Integer destination) {
        //TODO: You should return an ordered list of the node the indecies you
        // visit in your shortest path from origin to destination.


        return new ArrayList<Integer>();
    }

}
