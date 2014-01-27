package combOpt.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import combOpt.tsp.GeoTspTestInstance;
import combOpt.tsp.GeoTspTestReader;

public class GraphIOTest {

	@Test
	public void test() {
		GeoTspTestInstance instance = GeoTspTestReader.readTest("minimal");
		assertEquals(12, instance.getOptSolution());
		assertEquals(3, instance.getPoints().size());
		assertEquals(0, instance.getPoints().get(0).getX());
		assertEquals(0, instance.getPoints().get(0).getY());
		assertEquals(0, instance.getPoints().get(1).getX());
		assertEquals(3, instance.getPoints().get(1).getY());
		assertEquals(4, instance.getPoints().get(2).getX());
		assertEquals(0, instance.getPoints().get(2).getY());

		List<Node> expectedNodes = Lists.newArrayList(new Node(0), new Node(1),
				new Node(2));
		Table<Node, Node, Edge> expectedEdgeTable = HashBasedTable.create();
		Set<Edge> expectedEdges = Sets.newHashSet();
		Edge e01 = new Edge(ImmutableSet.of(expectedNodes.get(0),
				expectedNodes.get(1)));
		expectedEdges.add(e01);
		expectedEdgeTable.put(expectedNodes.get(0), expectedNodes.get(1), e01);
		expectedEdgeTable.put(expectedNodes.get(1), expectedNodes.get(0), e01);
		assertEquals(3.0, instance.getGraph().getWeight(e01), tolerance);

		Edge e12 = new Edge(ImmutableSet.of(expectedNodes.get(1),
				expectedNodes.get(2)));
		expectedEdges.add(e12);
		expectedEdgeTable.put(expectedNodes.get(1), expectedNodes.get(2), e12);
		expectedEdgeTable.put(expectedNodes.get(2), expectedNodes.get(1), e12);
		assertEquals(5.0, instance.getGraph().getWeight(e12), tolerance);

		Edge e02 = new Edge(ImmutableSet.of(expectedNodes.get(0),
				expectedNodes.get(2)));
		expectedEdges.add(e02);
		expectedEdgeTable.put(expectedNodes.get(0), expectedNodes.get(2), e02);
		expectedEdgeTable.put(expectedNodes.get(2), expectedNodes.get(0), e02);
		assertEquals(4.0, instance.getGraph().getWeight(e02), tolerance);

		assertEquals(expectedNodes, instance.getGraph().vertexSet());
		assertEquals(expectedEdges, instance.getGraph().edgeSet());
		assertEquals(expectedEdgeTable, instance.getGraph().getGraph()
				.getEdgeTable());

	}

	private static double tolerance = .0001;

}
