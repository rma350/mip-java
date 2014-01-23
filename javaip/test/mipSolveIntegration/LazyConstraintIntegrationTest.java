package mipSolveIntegration;

import static org.junit.Assert.assertEquals;
import lpSolveBase.ObjectiveSense;
import mipSolveBase.CutCallback;
import mipSolveBase.CutCallbackMipView;
import mipSolveBase.MipSolver;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import easy.EasyMip;
import easy.EasyMip.SolverType;

@RunWith(Theories.class)
public class LazyConstraintIntegrationTest {

	@DataPoints
	public static SolverType[] solverTypes = SolverType.values();

	private static double numTolerance = .0001;

	@Theory
	public void test(SolverType solverType) {
		// max x1 + 2*x2
		// x1, x2 binary
		// lazy: x1 + x2 <= 1
		MipSolver mipSolver = EasyMip.create(solverType);
		mipSolver.createObj(ObjectiveSense.MAX);
		for (int i = 0; i < 2; i++) {
			mipSolver.createIntVar();
			mipSolver.setVarLB(i, 0.0);
			mipSolver.setVarUB(i, 1.0);
			mipSolver.setObjCoef(i, i + 1.0);
		}
		CutCallback lazyCallback = new CutCallback() {

			@Override
			public boolean onCallback(CutCallbackMipView mipView) {
				double[] varVals = { mipView.getLPVarValue(0),
						mipView.getLPVarValue(1) };
				if (varVals[0] + varVals[1] > 1 + numTolerance) {
					int constrIndex = mipView.createConstr();
					mipView.setConstrLB(constrIndex, 0);
					mipView.setConstrUB(constrIndex, 1);
					mipView.setConstrCoef(constrIndex, 0, 1.0);
					mipView.setConstrCoef(constrIndex, 1, 1.0);
				}
				return true;
			}
		};
		mipSolver.addLazyConstraintCallback(lazyCallback);
		mipSolver.solve();
		assertEquals(2.0, mipSolver.getObjValue(), numTolerance);
		assertEquals(0.0, mipSolver.getVarValue(0), numTolerance);
		assertEquals(1.0, mipSolver.getVarValue(1), numTolerance);
		// assertEquals(1, lazyCallback.getCountTotalConstraintsAdded());
		// assertEquals(1, lazyCallback.getCountAtLeastOneConstraintAdded());
		// assertEquals(2, lazyCallback.getCountChecked());
	}

}
