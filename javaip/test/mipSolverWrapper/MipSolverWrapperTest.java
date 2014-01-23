package mipSolverWrapper;

import static org.junit.Assert.assertEquals;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;

import java.util.List;

import lpSolveBase.ObjectiveSense;
import mipSolveCplex.MipSolverCplex;

import org.junit.Test;

import wrappedMipSolver.MipSolverWrapper;
import wrappedMipSolver.WrappedCutCallback;
import wrappedMipSolver.WrappedCutCallbackMipView;

import com.google.common.collect.Lists;

public class MipSolverWrapperTest {

	private static double numTolerance = .0001;

	public static <V, C, O> void testLazy(
			MipSolverWrapper<V, C, O> mipSolverWrapper) {
		// max x1 + 2*x2
		// x1, x2 binary
		// lazy: x1 + x2 <= 1
		O obj = mipSolverWrapper.createObj(ObjectiveSense.MAX);
		final List<V> vars = Lists.newArrayList();
		for (int i = 0; i < 2; i++) {
			V var = mipSolverWrapper.createIntVar();
			vars.add(var);
			mipSolverWrapper.setVarLB(var, 0.0);
			mipSolverWrapper.setVarUB(var, 1.0);
			mipSolverWrapper.setObjCoef(obj, var, i + 1.0);
		}
		WrappedCutCallback<V, C, O> wrappedCutCallback = new WrappedCutCallback<V, C, O>() {

			@Override
			public boolean onCallback(WrappedCutCallbackMipView<V, C, O> mipView) {
				System.out.println("Entering wrapped callback");
				double varVal0 = mipView.getLPVarValue(vars.get(0));
				double varVal1 = mipView.getLPVarValue(vars.get(1));
				if (varVal0 + varVal1 > 1 + numTolerance) {
					System.out.println("Cut is violated.");
					C constraint = mipView.createConstr();
					mipView.setConstrLB(constraint, 0);
					mipView.setConstrUB(constraint, 1.0);
					mipView.setConstrCoef(constraint, vars.get(0), 1.0);
					mipView.setConstrCoef(constraint, vars.get(1), 1.0);
				}
				return true;
			}

		};

		mipSolverWrapper.addLazyConstrCallback(wrappedCutCallback);
		mipSolverWrapper.solve();
		assertEquals(2.0, mipSolverWrapper.getObjValue(), numTolerance);
		assertEquals(0.0, mipSolverWrapper.getVarValue(vars.get(0)),
				numTolerance);
		assertEquals(1.0, mipSolverWrapper.getVarValue(vars.get(1)),
				numTolerance);
		// assertEquals(1, lazyCallback.getCountTotalConstraintsAdded());
		// assertEquals(1, lazyCallback.getCountAtLeastOneConstraintAdded());
		// assertEquals(2, lazyCallback.getCountChecked());
	}

	@Test
	public void testLazyCplex() {
		MipSolverWrapper<IloNumVar, IloRange, IloObjective> mipSolverWrapper = new MipSolverCplex();
		testLazy(mipSolverWrapper);
		mipSolverWrapper.destroy();

	}

}
