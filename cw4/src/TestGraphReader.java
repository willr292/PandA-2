import graph.*;

import java.io.*;

/**
 * A class which for reading in a graph representation from a file and constructing
 * a graph from it.
 */

public class TestGraphReader extends GraphReader<Integer, Integer> {

    /**
     * Constructs a graph, read in from a file.
     *
     * @param filename the name of the file containing the graph to be read.
     * @return a newly constructed graph.
     */
    @Override
    public Graph<Integer, Integer> readGraph(String filename) throws IOException {
        InputStream input = this.getClass().getResourceAsStream("/" + filename);
        if (input == null) {
            System.err.println("Could not load resource in ScotlandYardGraphReader: " + filename);
            System.exit(1);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        UndirectedGraph graph = new UndirectedGraph();

        String[] topLine = reader.readLine().split(" ");
        int numberOfNodes = Integer.parseInt(topLine[0]);
        int numberOfEdges = Integer.parseInt(topLine[1]);

        for (int i = 0; i < numberOfNodes; i++) {
            String line = reader.readLine();
            if (line.isEmpty()) continue;
            graph.add(getNode(line));
        }

        for (int i = 0; i < numberOfEdges; i++) {
            String line = reader.readLine();
            if (line.isEmpty()) continue;
            Edge<Integer, Integer> edge = getEdge(line, graph);
            graph.add(edge);
        }

        return graph;
    }

    /**
     * Constructs a new Edge from a string.
     *
     * @param line the string used to construct the edge.
     * @param graph the graph from which nodes will be taken when constructing
     * the edge
     * @return a newly constructed edge
     */
    private Edge<Integer, Integer> getEdge(String line, UndirectedGraph graph) {
        String[] parts = line.split(" ");
        Integer data = null;
        Node<Integer> n1 = null;
        Node<Integer> n2 = null;

        try {
            n1 = graph.getNode(Integer.parseInt(parts[0]));
            n2 = graph.getNode(Integer.parseInt(parts[1]));
            data = Integer.parseInt(parts[2]);
        } catch (IllegalArgumentException e) {
            System.err.println("Error reading graph. Cannot create edge for line: " + line);
            System.exit(1);
        }

        if (n1 == null || n2 == null) {
            if (n1 == null) System.err.println("Error reading graph. Cannot find node: " + parts[1]);
            if (n2 == null) System.err.println("Error reading graph. Cannot find node: " + parts[2]);
            System.exit(1);
        }

        Edge<Integer, Integer> edge = new Edge<Integer, Integer>(n1, n2, data);
        return edge;
    }

    /**
     * Constructs a new Node from a string.
     *
     * @param line the string used to construct the node.
     * @return a newly constructed node
     */
    private Node<Integer> getNode(String line) {
        Integer nodeNumber = 0;
        try {
            nodeNumber= Integer.parseInt(line);
        } catch(NumberFormatException e) {
            System.err.println("Error reading graph. Cannot create node for line: " + line);
            System.exit(1);
        }
        Node<Integer> node = new Node<Integer>(nodeNumber);
        return node;
    }

}
