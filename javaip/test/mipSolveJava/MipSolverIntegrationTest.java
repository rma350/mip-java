package mipSolveJava;

import static org.junit.Assert.assertEquals;
import lpSolveBase.BasicLpSolver;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import org.junit.Test;

import easy.EasyLp;

public class MipSolverIntegrationTest {

	private static double tolerance = .0001;

	@Test
	public void testSimple() {
		// max x + y
		// s.t. 0 <= 3x + 4y <= 5
		// x,y integer
		BasicLpSolver lpSolver = EasyLp.easyLpSolver();
		lpSolver.createObj(ObjectiveSense.MAX);
		for (int i = 0; i < 2; i++) {
			lpSolver.createVar();
			lpSolver.setVarLB(i, 0.0);
			lpSolver.setVarUB(i, 1.0);
			lpSolver.setObjCoef(i, 1.0 + i);
		}
		lpSolver.createConstr();
		lpSolver.setConstrLB(0, 0.0);
		lpSolver.setConstrUB(0, 5.0);
		lpSolver.setConstrCoef(0, 0, 3.0);
		lpSolver.setConstrCoef(0, 1, 4.0);

		boolean[] integerVariables = { true, true };

		MipSolver mip = new MipSolver(lpSolver, integerVariables);
		mip.solve();
		assertEquals(SolutionStatus.OPTIMAL, mip.getSolutionStatus());
		assertEquals(2.0, mip.getObjValue(), tolerance);
		assertEquals(0.0, mip.getVarValue(0), tolerance);
		assertEquals(1.0, mip.getVarValue(1), tolerance);
	}

}
