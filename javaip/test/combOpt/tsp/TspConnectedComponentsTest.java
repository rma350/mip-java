package combOpt.tsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class TspConnectedComponentsTest {

	@Test
	public void testConnectedComponents() {
		UndirectedGraph graph = new UndirectedGraph();
		graph.addNodes(6);
		Map<Edge, Double> varVals = Maps.newHashMap();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < i; j++) {
				Edge edge = graph.addEdge(graph.vertexSet().get(i), graph
						.vertexSet().get(j), 10.0);
				if (i < 3 == j < 3) {
					varVals.put(edge, 1.0);
				} else {
					varVals.put(edge, 0.0);
				}
			}
		}
		Set<Node> firstComponent = Sets.newHashSet(graph.vertexSet().subList(0,
				3));
		Set<Node> secondComponent = Sets.newHashSet(graph.vertexSet().subList(
				3, 6));
		for (int i = 0; i < 3; i++) {
			assertEquals(firstComponent, Tsp.connectedComponent(graph, varVals,
					graph.vertexSet().get(i)));
		}

		for (int i = 3; i < 6; i++) {
			assertEquals(secondComponent, Tsp.connectedComponent(graph,
					varVals, graph.vertexSet().get(i)));
		}

		List<Set<Node>> actualConnectedComponents = Tsp.connectedComponents(
				graph, varVals);
		assertEquals(2, actualConnectedComponents.size());
		assertTrue(actualConnectedComponents.contains(firstComponent));
		assertTrue(actualConnectedComponents.contains(secondComponent));

	}

	@Test
	public void testConnectedComponentsLoop() {
		UndirectedGraph graph = new UndirectedGraph();
		graph.addNodes(6);
		Map<Edge, Double> varVals = Maps.newHashMap();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < i; j++) {
				Edge edge = graph.addEdge(graph.vertexSet().get(i), graph
						.vertexSet().get(j), 10.0);
				if (i == j + 1 || (i == 5 && j == 0)) {
					varVals.put(edge, 1.0);
				} else {
					varVals.put(edge, 0.0);
				}
			}
		}
		Set<Node> onlyComponent = Sets.newHashSet(graph.vertexSet());
		for (int i = 0; i < 6; i++) {
			assertEquals(onlyComponent, Tsp.connectedComponent(graph, varVals,
					graph.vertexSet().get(i)));
		}
		List<Set<Node>> actualConnectedComponents = Tsp.connectedComponents(
				graph, varVals);
		List<Set<Node>> expected = Lists.newArrayList();
		expected.add(onlyComponent);
		assertEquals(expected, actualConnectedComponents);
	}

}
