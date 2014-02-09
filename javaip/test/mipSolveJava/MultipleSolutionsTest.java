package mipSolveJava;

import static org.junit.Assert.assertEquals;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class MultipleSolutionsTest {

	private static double tolerance = .0001;

	public static OpenIntToDoubleHashMap start1 = makeStart(0, 1);

	public static OpenIntToDoubleHashMap start2 = makeStart(1, 0);

	public static OpenIntToDoubleHashMap start3 = makeStart(0, 0);

	public MipSolverImpl makeMipSolver() {
		// max x + 2*y
		// s.t. 0 <= 3x + 4y <= 5
		// x,y integer
		MipSolverImpl mipSolver = new MipSolverImpl();
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

		return mipSolver;
	}

	@Test
	public void testTwoSolutions() {
		MipSolverImpl solver = makeMipSolver();
		solver.setNumSolutions(2, 0);
		solver.solve();
		assertEquals(SolutionStatus.OPTIMAL, solver.getSolutionStatus());
		assertEquals(2, solver.getOptimalSolutions().size());
		assertSolutionIs(solver.getOptimalSolutions().first(), 0, 1, 2);
		assertSolutionIs(solver.getOptimalSolutions().last(), 1, 0, 1);

	}

	@Test
	public void testThreeSolutions() {
		MipSolverImpl solver = makeMipSolver();
		solver.setNumSolutions(3, 0);
		solver.solve();
		assertEquals(SolutionStatus.OPTIMAL, solver.getSolutionStatus());
		assertEquals(3, solver.getOptimalSolutions().size());
		assertSolutionIs(solver.getOptimalSolutions().first(), 0, 1, 2);
		assertSolutionIs(Iterables.get(solver.getOptimalSolutions(), 1), 1, 0,
				1);
		assertSolutionIs(solver.getOptimalSolutions().last(), 0, 0, 0);
	}

	@Test
	public void testExtraSolutions() {
		MipSolverImpl solver = makeMipSolver();
		solver.setNumSolutions(1, 1);
		solver.suggestAdvancedStart(start2);
		solver.suggestAdvancedStart(start3);
		solver.solve();
		assertEquals(SolutionStatus.OPTIMAL, solver.getSolutionStatus());
		assertEquals(1, solver.getOptimalSolutions().size());
		assertSolutionIs(solver.getOptimalSolutions().first(), 0, 1, 2);
		assertEquals(1, solver.getFeasibleSolutions().size());
		assertSolutionIs(solver.getFeasibleSolutions().first(), 1, 0, 1);
	}

	@Test
	public void testFeasibleSolutions() {
		MipSolverImpl solver = makeMipSolver();
		solver.setNumSolutions(1, 1);
		solver.suggestAdvancedStart(start3);
		solver.solve();
		assertEquals(SolutionStatus.OPTIMAL, solver.getSolutionStatus());
		assertEquals(1, solver.getOptimalSolutions().size());
		assertSolutionIs(solver.getOptimalSolutions().first(), 0, 1, 2);
		assertEquals(1, solver.getFeasibleSolutions().size());
		assertSolutionIs(solver.getFeasibleSolutions().first(), 0, 0, 0);
	}

	private static void assertSolutionIs(Solution solution, double x, double y,
			double objectiveValue) {

		assertEquals(x, solution.getVariableValues()[0], tolerance);
		assertEquals(y, solution.getVariableValues()[1], tolerance);
		assertEquals(objectiveValue, solution.getObjValue(), tolerance);
	}

	private static OpenIntToDoubleHashMap makeStart(double x, double y) {
		OpenIntToDoubleHashMap ans = new OpenIntToDoubleHashMap();
		ans.put(0, x);
		ans.put(1, y);
		return ans;
	}

}
