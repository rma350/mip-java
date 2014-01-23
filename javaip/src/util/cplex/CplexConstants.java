package util.cplex;

import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex.Status;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import com.google.common.collect.ImmutableBiMap;

public class CplexConstants {

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

}
