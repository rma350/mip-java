package lpSolveCplex;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import ilog.cplex.IloCplex.UnknownObjectException;
import lpSolveBase.AbstractLpSolver;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import com.google.common.collect.ImmutableBiMap;

public class LpSolveCplex extends
		AbstractLpSolver<IloNumVar, IloRange, IloObjective> {

	public static final ImmutableBiMap<ObjectiveSense, IloObjectiveSense> CPLEX_OBJECTIVE_SENSE = ImmutableBiMap
			.of(ObjectiveSense.MAX, IloObjectiveSense.Maximize,
					ObjectiveSense.MIN, IloObjectiveSense.Minimize);

	public static final ImmutableBiMap<SolutionStatus, Status> CPLEX_SOLUTION_STATUS = ImmutableBiMap
			.<SolutionStatus, Status> builder()
			.put(SolutionStatus.BOUNDED, Status.Bounded)
			.put(SolutionStatus.ERROR, Status.Error)
			.put(SolutionStatus.FEASIBLE, Status.Feasible)
			.put(SolutionStatus.INFEASIBLE, Status.Infeasible)
			.put(SolutionStatus.INFEASIBLE_OR_UNBOUNDED,
					Status.InfeasibleOrUnbounded)
			.put(SolutionStatus.OPTIMAL, Status.Optimal)
			.put(SolutionStatus.UNBOUNDED, Status.Unbounded)
			.put(SolutionStatus.UNKNOWN, Status.Unknown).build();

	private IloCplex cplex;

	public LpSolveCplex() {
		try {
			cplex = new IloCplex();
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IloNumVar createVar() {
		try {
			return cplex.numVar(Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setVarUB(IloNumVar variable, double upperBound) {
		try {
			variable.setUB(upperBound);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setVarLB(IloNumVar variable, double lowerBound) {
		try {
			variable.setLB(lowerBound);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void solve() {
		try {
			cplex.solve();
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getVarValue(IloNumVar variable) {
		try {
			return cplex.getValue(variable);
		} catch (UnknownObjectException e) {
			throw new RuntimeException(e);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getObjValue() {
		try {
			return cplex.getObjValue();
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SolutionStatus getSolutionStatus() {
		try {
			Status status = cplex.getStatus();
			return CPLEX_SOLUTION_STATUS.inverse().get(status);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IloRange createConstr() {
		try {
			return cplex.addRange(Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IloObjective createObj(lpSolveBase.ObjectiveSense objectiveSense) {
		try {
			return cplex
					.addObjective(CPLEX_OBJECTIVE_SENSE.get(objectiveSense));
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setObjCoef(IloObjective objective, IloNumVar variable,
			double value) {
		try {
			cplex.setLinearCoef(objective, variable, value);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setConstrCoef(IloRange constraint, IloNumVar variable,
			double value) {
		try {
			cplex.setLinearCoef(constraint, variable, value);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setConstrUB(IloRange constraint, double value) {
		try {
			constraint.setUB(value);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setConstrLB(IloRange constraint, double value) {
		try {
			constraint.setLB(value);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ObjectiveSense getObjectiveSense(IloObjective objective) {
		try {
			return CPLEX_OBJECTIVE_SENSE.inverse().get(objective.getSense());
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getVarUB(IloNumVar variable) {
		try {
			return variable.getUB();
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getVarLB(IloNumVar variable) {
		try {
			return variable.getLB();
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}
}
