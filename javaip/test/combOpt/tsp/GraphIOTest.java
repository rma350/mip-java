package combOpt.tsp;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

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
				expectedNodes.get(1)), 3);
		expectedEdges.add(e01);
		expectedEdgeTable.put(expectedNodes.get(0), expectedNodes.get(1), e01);
		expectedEdgeTable.put(expectedNodes.get(1), expectedNodes.get(0), e01);

		Edge e12 = new Edge(ImmutableSet.of(expectedNodes.get(1),
				expectedNodes.get(2)), 5);
		expectedEdges.add(e12);
		expectedEdgeTable.put(expectedNodes.get(1), expectedNodes.get(2), e12);
		expectedEdgeTable.put(expectedNodes.get(2), expectedNodes.get(1), e12);

		Edge e02 = new Edge(ImmutableSet.of(expectedNodes.get(0),
				expectedNodes.get(2)), 4);
		expectedEdges.add(e02);
		expectedEdgeTable.put(expectedNodes.get(0), expectedNodes.get(2), e02);
		expectedEdgeTable.put(expectedNodes.get(2), expectedNodes.get(0), e02);

		assertEquals(expectedNodes, instance.getGraph().vertexSet());
		assertEquals(expectedEdges, instance.getGraph().edgeSet());
		assertEquals(expectedEdgeTable, instance.getGraph().getEdgeTable());

	}

}
