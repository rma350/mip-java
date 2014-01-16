package combOpt.tsp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lpSolveBase.ObjectiveSense;
import mipSolveJava.CutCallback;
import mipSolveJava.MipSolverImpl;
import mipSolveJava.MipSolverInternal;
import mipSolveJava.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Tsp {

	private UndirectedGraph graph;
	int numCities;
	private Map<Edge, Integer> edgeVars;
	private Map<Node, Integer> degreeConstraints;

	private Set<Edge> edgesInSolution;
	private double solutionCost;

	public Tsp(UndirectedGraph graph) {
		this.graph = graph;
		this.numCities = graph.vertexSet().size();
		if (numCities < 3) {
			throw new RuntimeException("Need at least three cities for TSP");
		}
		MipSolverInternal solver = new MipSolverImpl();
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
		ConnectedCallback lazy = new ConnectedCallback(solver);
		solver.addLazyConstraintCallback(lazy);
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

	private class ConnectedCallback extends CutCallback {

		public ConnectedCallback(MipSolverInternal mipSolver) {
			super(mipSolver);
		}

		@Override
		protected boolean onCallback(Solution solution) {
			Map<Edge, Double> edgeValues = Maps.newHashMap();
			for (Edge edge : graph.edgeSet()) {
				edgeValues.put(edge,
						solution.getVariableValues()[edgeVars.get(edge)]);
			}
			List<Set<Node>> connectedComponents = connectedComponents(graph,
					edgeValues);
			for (int i = 0; i < connectedComponents.size() - 1; i++) {
				Set<Node> component = connectedComponents.get(i);
				int cutsetConstraint = this.createConstr();
				this.setConstrLB(cutsetConstraint, 2.0);
				for (Node node : component) {
					for (Map.Entry<Node, Edge> adjacent : graph
							.getAdjacentEdges(node).entrySet()) {
						if (!component.contains(adjacent.getKey())) {
							this.setConstrCoef(cutsetConstraint,
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
