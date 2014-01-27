package combOpt.maxFlowMinCut;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lpSolveBase.BasicLpSolver;
import lpSolveBase.ObjectiveSense;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import combOpt.graph.DirectedEdge;
import combOpt.graph.Node;
import combOpt.graph.ReadableDirectedWeightedGraph;

import easy.EasyLp;

public class MaxFlowMinCut {

	private ReadableDirectedWeightedGraph graph;
	private Node source;
	private Node sink;
	private Set<Node> sourceSide;
	private Set<Node> sinkSide;
	private double optCutValue;

	public MaxFlowMinCut(ReadableDirectedWeightedGraph graph, Node source,
			Node sink) {
		this.graph = graph;
		this.source = source;
		this.sink = sink;
		BasicLpSolver lpSolver = EasyLp.easyLpSolver();
		lpSolver.createObj(ObjectiveSense.MAX);
		Map<DirectedEdge, Integer> edgeVars = Maps.newHashMap();
		for (DirectedEdge edge : graph.edges()) {
			int edgeVar = lpSolver.createVar();
			edgeVars.put(edge, edgeVar);
			lpSolver.setVarLB(edgeVar, 0);
			if (edge.getSource() == sink || edge.getTarget() == source) {
				lpSolver.setVarUB(edgeVar, 0.0);
			} else {
				lpSolver.setVarUB(edgeVar, graph.getWeight(edge));
			}

		}
		Map<Node, Integer> nodeConstraints = Maps.newHashMap();
		for (Node node : graph.nodes()) {
			if (node == source) {
				for (Entry<Node, DirectedEdge> outgoing : graph
						.getOutgoingEdges(node).entrySet()) {
					lpSolver.setObjCoef(edgeVars.get(outgoing.getValue()), 1);
				}
			} else if (node == sink) {

			} else {
				int nodeConstraint = lpSolver.createConstr();
				nodeConstraints.put(node, nodeConstraint);
				lpSolver.setConstrLB(nodeConstraint, 0);
				lpSolver.setConstrUB(nodeConstraint, 0);
				for (Entry<Node, DirectedEdge> incoming : graph
						.getIncomingEdges(node).entrySet()) {
					lpSolver.setConstrCoef(nodeConstraint,
							edgeVars.get(incoming.getValue()), 1);
				}
				for (Entry<Node, DirectedEdge> outgoing : graph
						.getOutgoingEdges(node).entrySet()) {
					lpSolver.setConstrCoef(nodeConstraint,
							edgeVars.get(outgoing.getValue()), -1);
				}
			}
		}
		lpSolver.solve();
		optCutValue = lpSolver.getObjValue();
		this.sourceSide = Sets.newHashSet();
		sourceSide.add(source);
		this.sinkSide = Sets.newHashSet();
		sinkSide.add(sink);
		for (Node node : graph.nodes()) {
			if (node != source && node != sink) {
				double dualVal = lpSolver.getDualVarValue(nodeConstraints
						.get(node));
				if (Math.abs(1 - dualVal) < lpDualTolerance) {
					sourceSide.add(node);
				} else if (Math.abs(dualVal) < lpDualTolerance) {
					sinkSide.add(node);
				} else {
					throw new RuntimeException("Unexpected dual value of "
							+ dualVal + " on constraint for node: "
							+ node.getId());
				}
			}
		}
		lpSolver.destroy();
	}

	public Set<Node> getSourceSide() {
		return sourceSide;
	}

	public Set<Node> getSinkSide() {
		return sinkSide;
	}

	public double getOptCutValue() {
		return optCutValue;
	}

	private static final double lpDualTolerance = .001;

}
