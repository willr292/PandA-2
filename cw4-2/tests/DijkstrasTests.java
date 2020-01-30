import graph.*;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class DijkstrasTests {

    @Test
    public void testDijkstrasOnSmallGraph() throws Exception {
        TestGraphReader graphReader = new TestGraphReader();
        Graph<Integer, Integer> graph = graphReader.readGraph("small-graph.txt");

        List<Integer> expectedNodes = new ArrayList<Integer>();
        expectedNodes.add(1);
        expectedNodes.add(2);
        expectedNodes.add(4);
        expectedNodes.add(5);
        expectedNodes.add(7);

        Dijkstra dijkstra = new Dijkstra(graph);

        List<Integer> actualNodes = dijkstra.shortestPath(1, 7);
        assertEquals("The shortest path between 1 and 7 should contain nodes 1, 2, 4, 5 and 7",
                      expectedNodes, actualNodes);
    }

    @Test
    public void testDijkstrasOnMediumGraph() throws Exception {
        TestGraphReader graphReader = new TestGraphReader();
        Graph<Integer, Integer> graph = graphReader.readGraph("medium-graph.txt");

        List<Integer> expectedNodes = new ArrayList<Integer>();
        expectedNodes.add(1);
        expectedNodes.add(2);
        expectedNodes.add(6);
        expectedNodes.add(7);
        expectedNodes.add(8);
        expectedNodes.add(12);
        expectedNodes.add(14);
        expectedNodes.add(18);
        expectedNodes.add(21);
        expectedNodes.add(34);

        Dijkstra dijkstra = new Dijkstra(graph);

        List<Integer> actualNodes = dijkstra.shortestPath(1, 34);
        assertEquals("The shortest path between 1 and 34 should contain nodes 1, 2, 6, 7, 8, 12, 14, 18, 21 and 34",
                      expectedNodes, actualNodes);
    }

    @Test
    public void testDijkstrasOnLargeGraph() throws Exception {
        TestGraphReader graphReader = new TestGraphReader();
        Graph<Integer, Integer> graph = graphReader.readGraph("large-graph.txt");

        List<Integer> expectedNodes = new ArrayList<Integer>();
        expectedNodes.add(1);
        expectedNodes.add(9);
        expectedNodes.add(20);
        expectedNodes.add(33);
        expectedNodes.add(46);
        expectedNodes.add(61);
        expectedNodes.add(78);
        expectedNodes.add(97);
        expectedNodes.add(109);
        expectedNodes.add(124);
        expectedNodes.add(138);
        expectedNodes.add(152);
        expectedNodes.add(153);
        expectedNodes.add(167);
        expectedNodes.add(168);
        expectedNodes.add(184);
        expectedNodes.add(185);
        expectedNodes.add(199);

        Dijkstra dijkstra = new Dijkstra(graph);

        List<Integer> actualNodes = dijkstra.shortestPath(1, 199);
        assertEquals("The shortest path between 1 and 199 should contain nodes 1, 9, 20, 33, 46, 61, 78, 97, 109, 124, 138, 152, 153, 167, 168, 184, 185 and 199",
                      expectedNodes, actualNodes);
    }

}
