package graph;

import java.util.*;

/**
 * A class that represents a directed graph.
 */

public class DirectedGraph<X, Y> implements Graph<X, Y>{

    private Map<X, Node<X>> nodeMap;
    private Map<Node<X>, List<Edge<X, Y>>> sourceEdges;
    private Map<Node<X>, List<Edge<X, Y>>> targetEdges;
    private List<Node<X>> allNodes;
    private List<Edge<X, Y>> allEdges;

    /**
     * Constructs a new empty directed graph.
     */
    public DirectedGraph() {
        nodeMap = new HashMap<X, Node<X>>();
        sourceEdges = new HashMap<Node<X>, List<Edge<X, Y>>>();
        targetEdges = new HashMap<Node<X>, List<Edge<X, Y>>>();
        allNodes = new ArrayList<Node<X>>();
        allEdges = new ArrayList<Edge<X, Y>>();
    }

    /**
     * Adds a node to the graph. Duplicate nodes are not allowed.
     *
     * @param node the node to be added to the graph.
     */
    @Override
    public void add(Node<X> node) {
        nodeMap.put(node.getIndex(), node);
        allNodes.add(node);
        sourceEdges.put(node, new ArrayList<Edge<X, Y>>());
        targetEdges.put(node, new ArrayList<Edge<X, Y>>());
    }

    /**
     * Adds an edge to the graph. Multiple edges with the same source and
     * target nodes are allowed.
     *
     * @param edge the edge to add to the graph.
     */
    @Override
    public void add(Edge<X, Y> edge) {
        Node<X> source = getNode(edge.getSource().getIndex());
        Node<X> target = getNode(edge.getTarget().getIndex());

        sourceEdges.get(source).add(edge);
        targetEdges.get(target).add(edge);

        allEdges.add(edge);
    }

    /**
     * Removes a node from the graph, if it exists.
     *
     * @param node the node to be removed from the graph.
     */
    @Override
    public void remove(Node<X> node) {
        nodeMap.remove(node.getIndex());
        allNodes.remove(node);

        List<Edge<X,Y>> nodeSourceEdges = sourceEdges.get(node);
        List<Edge<X,Y>> nodeTargetEdges = targetEdges.get(node);
        if (nodeSourceEdges == null || nodeTargetEdges == null) return;
        sourceEdges.remove(node);
        targetEdges.remove(node);
        allEdges.removeAll(nodeSourceEdges);
        allEdges.removeAll(nodeTargetEdges);
    }

    /**
     * Removes an edge from the graph, if it exists.
     *
     * @param edge the edge to be removed from the graph.
     */
    @Override
    public void remove(Edge<X, Y> edge) {
        Node<X> source = getNode(edge.getSource().getIndex());
        Node<X> target = getNode(edge.getTarget().getIndex());
        if (source == null || target == null) return;
        sourceEdges.get(source).remove(edge);
        targetEdges.get(target).remove(edge);

        allEdges.remove(edge);
    }

    /**
     * Returns all edges in the graph.
     *
     * @return all edges in the graph.
     */
    @Override
    public List<Edge<X, Y>> getEdges() {
        return new ArrayList<Edge<X, Y>>(allEdges);
    }

    /**
     * Returns all nodes in the graph.
     *
     * @return all nodes in the graph.
     */
    @Override
    public List<Node<X>> getNodes() {
        return new ArrayList<Node<X>>(allNodes);
    }

    /**
     * Returns the node in the graph with the specified index. Returns null
     * if there isn't a node with that index in the graph.
     *
     * @return the node in the graph with the specified index.
     */
    @Override
    public Node<X> getNode(X index) {
        return nodeMap.get(index);
    }

    /**
     * Returns all edges in the graph where the target of the edge is
     * the specified node. Returns null if there aren't any edges.
     *
     * @param node the target node of the edges to be returned.
     * @return all edges in the graph where the target of the edge is
     * the specified node.
     */
    @Override
    public List<Edge<X, Y>> getEdgesTo(Node<X> node) {
        return new ArrayList<Edge<X, Y>>(targetEdges.get(node));
    }

    /**
     * Returns all edges in the graph where the source of the edge is
     * the specified node. Returns null if there aren't any edges.
     *
     * @param node the source node of the edges to be returned.
     * @return all edges in the graph where the source of the edge is
     * the specified node.
     */
    @Override
    public List<Edge<X, Y>> getEdgesFrom(Node<X> node) {
        return new ArrayList<Edge<X, Y>>(sourceEdges.get(node));
    }

    /**
     * Returns a representation of the graph as a string.
     *
     * @return a representation of the graph as a string.
     */
    @Override
    public String toString() {
        String output = "";
        for (Node<X> node : allNodes) {
            output += node.toString() + "\n";
        }

        for (Edge<X, Y> edge : allEdges) {
            output += edge.toString() + "\n";
        }

        return output;
    }

}
