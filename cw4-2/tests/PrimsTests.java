import graph.*;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class PrimsTests {

    @Test
    public void testPrimsOnSmallGraph() throws Exception {
        TestGraphReader graphReader = new TestGraphReader();
        Graph<Integer, Integer> graph = graphReader.readGraph("small-graph.txt");

        Graph<Integer, Integer> expectedGraph = graphReader.readGraph("mst-small-graph.txt");

        Prim prim = new Prim(graph);

        Graph<Integer, Integer> minimumSpanningTree = prim.getMinimumSpanningTree();
        List<Edge<Integer, Integer>> edges = minimumSpanningTree.getEdges();
        List<Edge<Integer, Integer>> expectedEdges = expectedGraph.getEdges();
        assertTrue("The number of edges in the minimum spanning tree should be " + expectedEdges.size() + ".",
                    edges.size() == expectedEdges.size());

        containsAllNodes(expectedGraph.getNodes(), minimumSpanningTree.getNodes());
        containsAllEdges(expectedEdges, edges);
    }

    @Test
    public void testPrimsOnMediumGraph() throws Exception {
        TestGraphReader graphReader = new TestGraphReader();
        Graph<Integer, Integer> graph = graphReader.readGraph("medium-graph.txt");

        Graph<Integer, Integer> expectedGraph = graphReader.readGraph("mst-medium-graph.txt");

        Prim prim = new Prim(graph);

        Graph<Integer, Integer> minimumSpanningTree = prim.getMinimumSpanningTree();
        List<Edge<Integer, Integer>> edges = minimumSpanningTree.getEdges();
        List<Edge<Integer, Integer>> expectedEdges = expectedGraph.getEdges();
        assertTrue("The number of edges in the minimum spanning tree should be " + expectedEdges.size() + ".",
                    edges.size() == expectedEdges.size());

        containsAllNodes(expectedGraph.getNodes(), minimumSpanningTree.getNodes());
        containsAllEdges(expectedEdges, edges);
    }

    private void containsAllNodes(List<Node<Integer>> expected, List<Node<Integer>> actual) {
        for (Node<Integer> node : expected) {
            assertTrue("The minimum spanning tree should contain this node: " + node,
                        containsNode(actual, node));
        }
    }

    private void containsAllEdges(List<Edge<Integer, Integer>> expected, List<Edge<Integer, Integer>> actual) {
        for (Edge<Integer, Integer> edge : expected) {
            assertTrue("The minimum spanning tree should contain this edge: " + edge,
                        containsEdge(actual, edge));
        }
    }

    private boolean containsNode(List<Node<Integer>> nodes, Node<Integer> testNode) {
        for (Node<Integer> node : nodes) {
            if (node.getIndex().equals(testNode.getIndex())) return true;
        }
        return false;
    }

    private boolean containsEdge(List<Edge<Integer, Integer>> edges, Edge<Integer, Integer> testEdge) {
        for (Edge<Integer, Integer> edge : edges) {
            if (edge.getSource().getIndex().equals(testEdge.getSource().getIndex())
                && edge.getTarget().getIndex().equals(testEdge.getTarget().getIndex())
                && edge.getData().equals(testEdge.getData())) return true;
        }
        return false;
    }

}
