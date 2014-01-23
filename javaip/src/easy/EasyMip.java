package easy;

import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import mipSolveBase.MipSolver;
import mipSolveBase.WrappedMipSolver;
import mipSolveCplex.MipSolverCplex;
import mipSolveJava.MipSolverImpl;

public class EasyMip {

	public static enum SolverType {
		JAVA, CPLEX;
	}

	public static MipSolver create() {
		return createJava();
	}

	public static MipSolver create(SolverType solverType) {
		if (solverType == SolverType.JAVA) {
			return createJava();
		} else if (solverType == SolverType.CPLEX) {
			return createCplex();
		} else {
			throw new RuntimeException("Unknown solver type: " + solverType);
		}
	}

	public static MipSolverImpl createJava() {
		return new MipSolverImpl();
	}

	public static WrappedMipSolver<IloNumVar, IloRange, IloObjective> createCplex() {
		return new WrappedMipSolver<IloNumVar, IloRange, IloObjective>(
				new MipSolverCplex());
	}

}
