package combOpt.tsp;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lpSolveBase.ObjectiveSense;
import mipSolveBase.CutCallback;
import mipSolveBase.CutCallbackMipView;
import mipSolveBase.MipSolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import easy.EasyMip;
import easy.EasyMip.SolverType;

public class Tsp {

	private UndirectedGraph graph;
	int numCities;
	private Map<Edge, Integer> edgeVars;
	private Map<Node, Integer> degreeConstraints;

	private Set<Edge> edgesInSolution;
	private double solutionCost;

	private EnumSet<TspOption> tspOptions;

	public static enum TspOption {
		userCuts;
	}

	public Tsp(UndirectedGraph graph, SolverType solverType) {
		this(graph, solverType, EnumSet.allOf(TspOption.class));
	}

	public Tsp(UndirectedGraph graph, SolverType solverType,
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
			solver.setObjCoef(edgeVar, e.getWeight());
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
		ConnectedCallback lazy = new ConnectedCallback(false);
		solver.addLazyConstraintCallback(lazy);
		if (this.tspOptions.contains(TspOption.userCuts)) {
			ConnectedCallback user = new ConnectedCallback(true);
			solver.addUserCutCallback(user);
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

	private class ConnectedCallback implements CutCallback {

		private boolean rootOnly;

		public ConnectedCallback(boolean rootOnly) {
			this.rootOnly = rootOnly;
		}

		@Override
		public boolean onCallback(CutCallbackMipView mipView) {
			if (rootOnly && mipView.nodesCreated() > 1) {
				return true;
			}
			Map<Edge, Double> edgeValues = Maps.newHashMap();
			for (Edge edge : graph.edgeSet()) {
				edgeValues.put(edge, mipView.getLPVarValue(edgeVars.get(edge)));
			}
			List<Set<Node>> connectedComponents = connectedComponents(graph,
					edgeValues);
			for (int i = 0; i < connectedComponents.size() - 1; i++) {
				Set<Node> component = connectedComponents.get(i);
				int cutsetConstraint = mipView.createConstr();
				mipView.setConstrLB(cutsetConstraint, 2.0);
				mipView.setConstrUB(cutsetConstraint, numCities);
				for (Node node : component) {
					for (Map.Entry<Node, Edge> adjacent : graph
							.getAdjacentEdges(node).entrySet()) {
						if (!component.contains(adjacent.getKey())) {
							mipView.setConstrCoef(cutsetConstraint,
									edgeVars.get(adjacent.getValue()), 1.0);
						}
					}
				}
			}
			return true;
		}

	}

	private static double tolerance = .0001;

	public static Set<Node> connectedComponent(UndirectedGraph graph,
			Map<Edge, Double> edgeValues, Node start) {
		Set<Node> explored = Sets.newHashSet();
		Set<Node> frontier = Sets.newHashSet(start);
		while (!frontier.isEmpty()) {
			Node top = frontier.iterator().next();
			frontier.remove(top);
			explored.add(top);
			for (Map.Entry<Node, Edge> neighbor : graph.getAdjacentEdges(top)
					.entrySet()) {
				if (!explored.contains(neighbor.getKey())
						&& edgeValues.get(neighbor.getValue()).doubleValue() > tolerance) {
					frontier.add(neighbor.getKey());
				}
			}
		}
		return explored;
	}

	public static List<Set<Node>> connectedComponents(UndirectedGraph graph,
			Map<Edge, Double> edgeValues) {
		List<Set<Node>> ans = Lists.newArrayList();
		Set<Node> unused = Sets.newHashSet(graph.vertexSet());
		while (!unused.isEmpty()) {
			Node first = unused.iterator().next();
			Set<Node> component = connectedComponent(graph, edgeValues, first);
			ans.add(component);
			unused.removeAll(component);
		}
		return ans;
	}

}
