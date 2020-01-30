package graph;

import java.util.*;

/**
 * A class which represents an undirected graph. It uses a directed graph and adds each edge going both ways.
 */

public class UndirectedGraph<X, Y> extends DirectedGraph<X, Y>{

    /**
     * Constructs a new UndirectedGraph.
     */
    public UndirectedGraph() {
        super();
    }

    /**
     * Adds an edge to the graph. It adds the edge going both ways.
     *
     * @param edge the edge to add to the graph.
     */
    @Override
    public void add(Edge<X, Y> edge){
        super.add(edge);
        super.add(edge.swap());
    }

    /**
     * Removes an edge from the graph, if it exists.
     *
     * @param edge the edge to be removed from the graph.
     */
    @Override
    public void remove(Edge<X, Y> edge) {
        super.remove(edge);
        Node source = edge.getSource();
        Node target = edge.getTarget();

        List<Edge<X,Y>> possibleSwaps = getEdgesTo(source);

        for (Edge<X,Y> swappedEdge : possibleSwaps) {
            if (swappedEdge.getSource().getIndex().equals(target)) {
                super.remove(swappedEdge);
                break;
            }
        }
    }

}
