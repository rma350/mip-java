package combOpt.knapsack;

import lpSolveBase.ObjectiveSense;
import mipSolveBase.MipSolver;

import com.google.common.collect.ImmutableList;

import easy.EasyMip;
import easy.EasyMip.SolverType;

public class Knapsack {

	public static class Item {
		private final double weight;
		private final double profit;

		public Item(double weight, double profit) {
			this.weight = weight;
			this.profit = profit;
		}

		public double getWeight() {
			return weight;
		}

		public double getProfit() {
			return this.profit;
		}
	}

	private double capacity;
	private ImmutableList<Item> items;
	private int numItems;

	private ImmutableList<Boolean> itemsSolution;
	private double profitSolution;

	public Knapsack(ImmutableList<Item> items, double capacity,
			SolverType solverType) {
		this.capacity = capacity;
		this.items = items;
		numItems = items.size();

		MipSolver solver = EasyMip.create(solverType);
		solver.createConstr();
		solver.setConstrLB(0, 0);
		solver.setConstrUB(0, capacity);
		solver.createObj(ObjectiveSense.MAX);
		for (int i = 0; i < numItems; i++) {
			solver.createIntVar();
			solver.setVarLB(i, 0);
			solver.setVarUB(i, 1);
			solver.setConstrCoef(0, i, items.get(i).getWeight());
			solver.setObjCoef(i, items.get(i).getProfit());
		}
		solver.solve();
		this.profitSolution = solver.getObjValue();
		ImmutableList.Builder<Boolean> inSolution = ImmutableList.builder();
		for (int i = 0; i < numItems; i++) {
			inSolution.add(solver.getVarValue(i) >= 1 - .0001);
		}
		itemsSolution = inSolution.build();
		solver.destroy();
	}

	public double getProfitSolution() {
		return profitSolution;
	}

	public ImmutableList<Boolean> getItemsSolution() {
		return itemsSolution;
	}

}
