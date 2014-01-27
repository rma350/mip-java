package combOpt.graph.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import combOpt.graph.Node;
import combOpt.graph.UndirectedGraphTable;
import combOpt.graph.algorithms.ConnectedComponents;

public class ConnectedComponentsTest {

	@Test
	public void testConnectedComponents() {
		UndirectedGraphTable graph = new UndirectedGraphTable();
		graph.addNodes(6);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < i; j++) {
				if (i < 3 == j < 3) {
					graph.addEdge(graph.vertexSet().get(i), graph.vertexSet()
							.get(j));
				}
			}
		}
		Set<Node> firstComponent = Sets.newHashSet(graph.vertexSet().subList(0,
				3));
		Set<Node> secondComponent = Sets.newHashSet(graph.vertexSet().subList(
				3, 6));
		for (int i = 0; i < 3; i++) {
			assertEquals(firstComponent,
					ConnectedComponents.connectedComponent(graph, graph
							.vertexSet().get(i)));
		}
		for (int i = 3; i < 6; i++) {
			assertEquals(secondComponent,
					ConnectedComponents.connectedComponent(graph, graph
							.vertexSet().get(i)));
		}
		List<Set<Node>> actualConnectedComponents = ConnectedComponents
				.connectedComponents(graph);
		assertEquals(2, actualConnectedComponents.size());
		assertTrue(actualConnectedComponents.contains(firstComponent));
		assertTrue(actualConnectedComponents.contains(secondComponent));

	}

	@Test
	public void testConnectedComponentsLoop() {
		UndirectedGraphTable graph = new UndirectedGraphTable();
		graph.addNodes(6);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < i; j++) {
				if (i == j + 1 || (i == 5 && j == 0)) {
					graph.addEdge(graph.vertexSet().get(i), graph.vertexSet()
							.get(j));
				}
			}
		}
		Set<Node> onlyComponent = Sets.newHashSet(graph.vertexSet());
		for (int i = 0; i < 6; i++) {
			assertEquals(onlyComponent, ConnectedComponents.connectedComponent(
					graph, graph.vertexSet().get(i)));
		}
		List<Set<Node>> actualConnectedComponents = ConnectedComponents
				.connectedComponents(graph);
		List<Set<Node>> expected = Lists.newArrayList();
		expected.add(onlyComponent);
		assertEquals(expected, actualConnectedComponents);
	}

}
