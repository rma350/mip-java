package easy;

import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import lpSolveBase.BasicLpSolver;
import lpSolveBase.BasicLpSolverImpl;
import lpSolveCplex.LpSolveCplex;

public class EasyLp {

	public static BasicLpSolver easyLpSolver() {
		return easyCPLEXLpSolver();
	}

	public static BasicLpSolver easyCPLEXLpSolver() {
		return new BasicLpSolverImpl<IloNumVar, IloRange, IloObjective>(
				new LpSolveCplex());
	}

}
