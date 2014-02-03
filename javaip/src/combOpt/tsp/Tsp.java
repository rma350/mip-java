package combOpt.tsp;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lpSolveBase.ObjectiveSense;
import mipSolveBase.CutCallback;
import mipSolveBase.CutCallbackMipView;
import mipSolveBase.MipSolver;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import combOpt.graph.DirectedView;
import combOpt.graph.Edge;
import combOpt.graph.Node;
import combOpt.graph.ReadableDirectedWeightedGraph;
import combOpt.graph.Subgraphs;
import combOpt.graph.Subgraphs.ReadableUndirectedSubgraphView;
import combOpt.graph.UndirectedWeightedGraph;
import combOpt.graph.algorithms.ConnectedComponents;
import combOpt.maxFlowMinCut.MaxFlowMinCut;

import easy.EasyMip;
import easy.EasyMip.SolverType;

public class Tsp {

	private UndirectedWeightedGraph graph;
	int numCities;
	private Map<Edge, Integer> edgeVars;
	private Map<Node, Integer> degreeConstraints;

	private Set<Edge> edgesInSolution;
	private double solutionCost;

	private EnumSet<TspOption> tspOptions;

	public static enum TspOption {
		userCuts;
	}

	public Tsp(UndirectedWeightedGraph graph, SolverType solverType) {
		this(graph, solverType, EnumSet.allOf(TspOption.class));
	}

	public Tsp(UndirectedWeightedGraph graph, SolverType solverType,
			EnumSet<TspOption> tspOptions) {
		this.graph = graph;
		this.tspOptions = tspOptions;
		this.numCities = graph.vertexSet().size();
		if (numCities < 3) {
			throw new RuntimeException("Need at least three cities for TSP");
		}
		MipSolver solver = EasyMip.create(solverType);
		solver.createObj(ObjectiveSense.MIN);
		edgeVars = Maps.newHashMap();
		for (Edge e : graph.edgeSet()) {
			int edgeVar = solver.createIntVar();
			solver.setVarLB(edgeVar, 0.0);
			solver.setVarUB(edgeVar, 1.0);
			solver.setObjCoef(edgeVar, graph.getWeight(e));
			edgeVars.put(e, edgeVar);
		}
		degreeConstraints = Maps.newHashMap();
		for (Node node : graph.vertexSet()) {
			int degreeConstr = solver.createConstr();
			solver.setConstrLB(degreeConstr, 2.0);
			solver.setConstrUB(degreeConstr, 2.0);
			for (Edge edge : graph.getAdjacentEdges(node).values()) {
				solver.setConstrCoef(degreeConstr, this.edgeVars.get(edge), 1.0);
			}
		}
		ConnectedCallback lazy = new ConnectedCallback(false, "cutset");
		solver.addLazyConstraintCallback(lazy);
		if (this.tspOptions.contains(TspOption.userCuts)) {
			ConnectedCallback quickUser = new ConnectedCallback(true, "quick");
			solver.addUserCutCallback(quickUser);
			MinCutCallback fullUser = new MinCutCallback(true, "full");
			solver.addUserCutCallback(fullUser);
		}
		solver.solve();

		solutionCost = solver.getObjValue();
		edgesInSolution = Sets.newHashSet();
		for (Edge edge : graph.edgeSet()) {
			if (Math.abs(1 - solver.getVarValue(edgeVars.get(edge))) < tolerance) {
				edgesInSolution.add(edge);
			}
		}
		solver.destroy();
	}

	public Set<Edge> getEdgesInSolution() {
		return this.edgesInSolution;
	}

	public double getSolutionCost() {
		return this.solutionCost;
	}

	private class MinCutCallback implements CutCallback {

		private boolean rootOnly;
		private String name;

		public MinCutCallback(boolean rootOnly, String name) {
			this.rootOnly = rootOnly;
			this.name = name;

		}

		public String toString() {
			return name;
		}

		@Override
		public boolean onCallback(CutCallbackMipView mipView) {
			Map<Edge, Double> edgeValues = Maps.newHashMap();
			for (Edge edge : graph.edgeSet()) {
				edgeValues.put(edge, mipView.getLPVarValue(edgeVars.get(edge)));
			}
			ReadableUndirectedSubgraphView nonZeroSubgraph = Subgraphs
					.onlyEdges(graph.getGraph(), new NonZeroEdgeWeights(
							edgeValues));
			DirectedView directedView = new DirectedView(nonZeroSubgraph);
			ReadableDirectedWeightedGraph flowGraph = directedView
					.createDirectedWeightedView(edgeValues);
			Node source = graph.vertexSet().get(0);
			Set<Set<Node>> sourceSideCuts = Sets.newHashSet();
			for (Node sink : graph.vertexSet()) {
				if (sink != source) {
					MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(flowGraph,
							source, sink);
					if (maxFlowMinCut.getOptCutValue() < 1.95) {
						sourceSideCuts.add(maxFlowMinCut.getSourceSide());
					}
				}
			}
			if (sourceSideCuts.size() == 0) {
				return true;
			}
			for (Set<Node> cut : sourceSideCuts) {
				addCutsetConstraint(cut, mipView);
			}
			return false;
		}

		@Override
		public boolean skipCallback(CutCallbackMipView mipView) {
			return rootOnly && mipView.nodesCreated() > 1;
		}
	}

	private class ConnectedCallback implements CutCallback {

		private boolean rootOnly;
		private String name;

		public ConnectedCallback(boolean rootOnly, String name) {
			this.rootOnly = rootOnly;
			this.name = name;
		}

		public String toString() {
			return name;
		}

		@Override
		public boolean onCallback(CutCallbackMipView mipView) {
			long startTime = System.currentTimeMillis();
			System.out.println("starting callback");
			final Map<Edge, Double> edgeValues = Maps.newHashMap();
			for (Edge edge : graph.edgeSet()) {
				edgeValues.put(edge, mipView.getLPVarValue(edgeVars.get(edge)));
			}
			System.out.println("read edge values. "
					+ (System.currentTimeMillis() - startTime));
			List<Set<Node>> connectedComponents = ConnectedComponents
					.connectedComponents(Subgraphs.onlyEdges(graph.getGraph(),
							new NonZeroEdgeWeights(edgeValues)));
			System.out.println("found connected component. "
					+ (System.currentTimeMillis() - startTime));
			if (connectedComponents.size() <= 1) {
				System.out.println("done. "
						+ (System.currentTimeMillis() - startTime));
				return true;
			}
			System.out.println("adding constraints: "
					+ connectedComponents.size() + ". "
					+ (System.currentTimeMillis() - startTime));
			for (int i = 0; i < connectedComponents.size() - 1; i++) {
				addCutsetConstraint(connectedComponents.get(i), mipView);
			}
			System.out.println("done. "
					+ (System.currentTimeMillis() - startTime));
			return false;
		}

		@Override
		public boolean skipCallback(CutCallbackMipView mipView) {
			return rootOnly && mipView.nodesCreated() > 1;
		}
	}

	private void addCutsetConstraint(Set<Node> cutNodes,
			CutCallbackMipView mipView) {
		int cutsetConstraint = mipView.createConstr();
		mipView.setConstrLB(cutsetConstraint, 2.0);
		mipView.setConstrUB(cutsetConstraint, numCities);
		for (Node node : cutNodes) {
			for (Map.Entry<Node, Edge> adjacent : graph.getAdjacentEdges(node)
					.entrySet()) {
				if (!cutNodes.contains(adjacent.getKey())) {
					mipView.setConstrCoef(cutsetConstraint,
							edgeVars.get(adjacent.getValue()), 1.0);
				}
			}
		}
	}

	private static class NonZeroEdgeWeights implements Predicate<Edge> {

		private Map<Edge, Double> edgeWeights;

		public NonZeroEdgeWeights(Map<Edge, Double> edgeWeights) {
			this.edgeWeights = edgeWeights;
		}

		public boolean apply(Edge edge) {
			return this.edgeWeights.get(edge) > tolerance;
		}
	}

	private static final double tolerance = .0001;

}
