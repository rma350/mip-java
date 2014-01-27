package combOpt.maxFlowMinCut;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import combOpt.graph.DirectedEdge;
import combOpt.graph.DirectedGraphTable;
import combOpt.graph.Node;
import combOpt.graph.ReadableDirectedWeightedGraph;
import combOpt.graph.ReadableDirectedWeightedGraphImpl;

public class MaxFlowMinCutTest {

	@Test
	public void testSymmetric() {
		DirectedGraphTable graph = new DirectedGraphTable();
		Node nA = graph.addNode();
		Node nB = graph.addNode();
		Node nC = graph.addNode();
		Node nD = graph.addNode();
		DirectedEdge eAB = graph.addEdge(nA, nB);
		DirectedEdge eBA = graph.addEdge(nB, nA);
		DirectedEdge eAC = graph.addEdge(nA, nC);
		DirectedEdge eCA = graph.addEdge(nC, nA);
		DirectedEdge eBC = graph.addEdge(nB, nC);
		DirectedEdge eCB = graph.addEdge(nC, nB);
		DirectedEdge eBD = graph.addEdge(nB, nD);
		DirectedEdge eDB = graph.addEdge(nD, nB);
		DirectedEdge eCD = graph.addEdge(nC, nD);
		DirectedEdge eDC = graph.addEdge(nD, nC);

		Map<DirectedEdge, Double> edgeCapacities = Maps.newHashMap();
		edgeCapacities.put(eAB, 4.0);
		edgeCapacities.put(eBA, 4.0);
		edgeCapacities.put(eAC, 3.0);
		edgeCapacities.put(eCA, 3.0);
		edgeCapacities.put(eBC, 3.0);
		edgeCapacities.put(eCB, 3.0);
		edgeCapacities.put(eBD, 4.0);
		edgeCapacities.put(eDB, 4.0);
		edgeCapacities.put(eCD, 5.0);
		edgeCapacities.put(eDC, 5.0);

		ReadableDirectedWeightedGraph instance = new ReadableDirectedWeightedGraphImpl(
				graph, edgeCapacities);

		MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(instance, nA, nD);
		assertEquals(7.0, maxFlowMinCut.getOptCutValue(), tolerance);
		assertEquals(Sets.newHashSet(nA), maxFlowMinCut.getSourceSide());
		assertEquals(Sets.newHashSet(nB, nC, nD), maxFlowMinCut.getSinkSide());
	}

	/**
	 * http://en.wikipedia.org/wiki/File:Max-flow_min-cut_project-selection.svg
	 */
	@Test
	public void testLargerGraph() {
		DirectedGraphTable graph = new DirectedGraphTable();
		Node nA = graph.addNode();
		Node nB = graph.addNode();
		Node nC = graph.addNode();
		Node nD = graph.addNode();
		Node nE = graph.addNode();
		Node nF = graph.addNode();
		Node nG = graph.addNode();
		Node nH = graph.addNode();
		Map<DirectedEdge, Double> edgeCapacities = Maps.newHashMap();
		edgeCapacities.put(graph.addEdge(nA, nB), 100.0);
		edgeCapacities.put(graph.addEdge(nA, nC), 200.0);
		edgeCapacities.put(graph.addEdge(nA, nD), 150.0);
		edgeCapacities.put(graph.addEdge(nB, nE), 1000.0);
		edgeCapacities.put(graph.addEdge(nB, nF), 1000.0);
		edgeCapacities.put(graph.addEdge(nC, nF), 1000.0);
		edgeCapacities.put(graph.addEdge(nD, nG), 1000.0);
		edgeCapacities.put(graph.addEdge(nE, nH), 200.0);
		edgeCapacities.put(graph.addEdge(nF, nH), 100.0);
		edgeCapacities.put(graph.addEdge(nG, nH), 50.0);
		ReadableDirectedWeightedGraph instance = new ReadableDirectedWeightedGraphImpl(
				graph, edgeCapacities);

		MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(instance, nA, nH);
		assertEquals(250.0, maxFlowMinCut.getOptCutValue(), tolerance);
		assertEquals(Sets.newHashSet(nA, nC, nD, nF, nG),
				maxFlowMinCut.getSourceSide());
		assertEquals(Sets.newHashSet(nB, nE, nH), maxFlowMinCut.getSinkSide());
	}

	private static double tolerance = .0001;

}
