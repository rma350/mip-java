package mipSolveIntegration;

import static org.junit.Assert.assertEquals;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;
import mipSolveBase.MipSolver;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import easy.EasyMip;
import easy.EasyMip.SolverType;

@RunWith(Theories.class)
public class MipSolverIntegrationTest {

	@DataPoints
	public static SolverType[] solverTypes = SolverType.values();

	private static double tolerance = .0001;

	@Theory
	public void testSimple(SolverType solverType) {
		// max x + y
		// s.t. 0 <= 3x + 4y <= 5
		// x,y integer
		MipSolver mipSolver = EasyMip.create(solverType);
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
