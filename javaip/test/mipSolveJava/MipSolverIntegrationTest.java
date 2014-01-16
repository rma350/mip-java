package mipSolveJava;

import static org.junit.Assert.assertEquals;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import org.junit.Test;

public class MipSolverIntegrationTest {

	private static double tolerance = .0001;

	@Test
	public void testSimple() {
		// max x + y
		// s.t. 0 <= 3x + 4y <= 5
		// x,y integer
		MipSolver mipSolver = new MipSolverImpl();
		mipSolver.createObj(ObjectiveSense.MAX);
		for (int i = 0; i < 2; i++) {
			mipSolver.createIntVar();
			mipSolver.setVarLB(i, 0.0);
			mipSolver.setVarUB(i, 1.0);
			mipSolver.setObjCoef(i, 1.0 + i);
		}
		mipSolver.createConstr();
		mipSolver.setConstrLB(0, 0.0);
		mipSolver.setConstrUB(0, 5.0);
		mipSolver.setConstrCoef(0, 0, 3.0);
		mipSolver.setConstrCoef(0, 1, 4.0);

		mipSolver.solve();
		assertEquals(SolutionStatus.OPTIMAL, mipSolver.getSolutionStatus());
		assertEquals(2.0, mipSolver.getObjValue(), tolerance);
		assertEquals(0.0, mipSolver.getVarValue(0), tolerance);
		assertEquals(1.0, mipSolver.getVarValue(1), tolerance);
	}

}
