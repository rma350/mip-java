package combOpt;

import java.util.Arrays;

import lpSolveBase.BasicLpSolver;
import lpSolveBase.ObjectiveSense;
import mipSolveJava.MipSolver;

import com.google.common.collect.ImmutableList;

import easy.EasyLp;

public class Knapsack {
	
	public static class Item{
		private final double weight;
		private final double profit;
		
		public Item(double weight, double profit){
			this.weight = weight;
			this.profit = profit;
		}
		
		public double getWeight(){
			return weight;
		}
		
		public double getProfit(){
			return this.profit;
		}
	}
	
	private double capacity;
	private ImmutableList<Item> items;
	private int numItems;
	
	private ImmutableList<Boolean> itemsSolution;
	private double profitSolution;
	
	public Knapsack(ImmutableList<Item> items, double capacity){
		this.capacity = capacity;
		this.items = items;
		numItems = items.size();
		
		
		
		BasicLpSolver lpSolver = EasyLp.easyLpSolver();
		lpSolver.createConstr();
		lpSolver.setConstrLB(0, 0);
		lpSolver.setConstrUB(0, capacity);
		lpSolver.createObj(ObjectiveSense.MAX);
		for(int i =0; i < numItems; i++){
			lpSolver.createVar();
			lpSolver.setVarLB(i, 0);
			lpSolver.setVarUB(i, 1);
			lpSolver.setConstrCoef(0,i, items.get(i).getWeight());
			lpSolver.setObjCoef(i, items.get(i).getProfit());
		}
		boolean[] integerVariables = new boolean[numItems];
		Arrays.fill(integerVariables, true);				
		MipSolver solver = new MipSolver(lpSolver,integerVariables);
		solver.solve();
		this.profitSolution = solver.getObjValue();
		ImmutableList.Builder<Boolean> inSolution = ImmutableList.builder();
		for(int i = 0; i < numItems; i++){
			inSolution.add(solver.getVarValue(i) >= 1 - .0001);
		}
		itemsSolution = inSolution.build();
		solver.destroy();
	}
	
	public double getProfitSolution(){
		return profitSolution;
	}
	
	public ImmutableList<Boolean> getItemsSolution(){
		return itemsSolution;
	}

}
