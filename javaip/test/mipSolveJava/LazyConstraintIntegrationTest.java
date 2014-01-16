package mipSolveJava;

import static org.junit.Assert.assertEquals;
import lpSolveBase.ObjectiveSense;

import org.junit.Test;

public class LazyConstraintIntegrationTest {

	private static double numTolerance = .0001;

	@Test
	public void test() {
		// max x1 + 2*x2
		// x1, x2 binary
		// lazy: x1 + x2 <= 1
		MipSolverInternal mipSolver = new MipSolverImpl();
		mipSolver.createObj(ObjectiveSense.MAX);
		for (int i = 0; i < 2; i++) {
			mipSolver.createIntVar();
			mipSolver.setVarLB(i, 0.0);
			mipSolver.setVarUB(i, 1.0);
			mipSolver.setObjCoef(i, i + 1.0);
		}
		CutCallback lazyCallback = new CutCallback(mipSolver) {

			@Override
			protected boolean onCallback(Solution solution) {
				double[] varVals = solution.getVariableValues();
				if (varVals[0] + varVals[1] > 1 + numTolerance) {
					int constrIndex = this.createConstr();
					this.setConstrLB(constrIndex, 0);
					this.setConstrUB(constrIndex, 1);
					this.setConstrCoef(constrIndex, 0, 1.0);
					this.setConstrCoef(constrIndex, 1, 1.0);
				}
				return true;
			}
		};
		mipSolver.addLazyConstraintCallback(lazyCallback);
		mipSolver.solve();
		assertEquals(2.0, mipSolver.getObjValue(), numTolerance);
		assertEquals(0.0, mipSolver.getVarValue(0), numTolerance);
		assertEquals(1.0, mipSolver.getVarValue(1), numTolerance);
		assertEquals(1, lazyCallback.getCountTotalConstraintsAdded());
		assertEquals(1, lazyCallback.getCountAtLeastOneConstraintAdded());
		assertEquals(2, lazyCallback.getCountChecked());
	}

}
