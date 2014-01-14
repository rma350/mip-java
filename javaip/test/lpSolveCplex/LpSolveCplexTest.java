package lpSolveCplex;

import static org.junit.Assert.assertEquals;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;

import java.util.List;

import lpSolveBase.ObjectiveSense;

import org.junit.Test;

import com.google.common.collect.Lists;

public class LpSolveCplexTest {

	private static double tolerance = .00001;

	@Test
	public void test() {
		LpSolveCplex cplex = new LpSolveCplex();

		IloObjective obj = cplex.createObj(ObjectiveSense.MAX);
		List<IloNumVar> vars = Lists.newArrayList();
		for (int i = 0; i < 2; i++) {
			vars.add(cplex.createVar());
			cplex.setVarLB(vars.get(i), 0.0);
			cplex.setVarUB(vars.get(i), 1.0);
			cplex.setObjCoef(obj, vars.get(i), 1.0 + i);
		}
		IloRange constr = cplex.createConstr();
		cplex.setConstrLB(constr, 0);
		cplex.setConstrUB(constr, 5.0);
		cplex.setConstrCoef(constr, vars.get(0), 3.0);
		cplex.setConstrCoef(constr, vars.get(1), 4.0);
		cplex.solve();
		assertEquals(2 + 1.0 / 3, cplex.getObjValue(), tolerance);
		assertEquals(1.0 / 3, cplex.getVarValue(vars.get(0)), tolerance);
		assertEquals(1.0, cplex.getVarValue(vars.get(1)), tolerance);

	}

}
