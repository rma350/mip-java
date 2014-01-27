package mipSolveCplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.LazyConstraintCallback;
import ilog.cplex.IloCplex.Status;
import ilog.cplex.IloCplex.UnknownObjectException;
import ilog.cplex.IloCplex.UserCutCallback;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;
import util.cplex.CplexConstants;
import wrappedMipSolver.MipSolverWrapper;
import wrappedMipSolver.WrappedCutCallback;
import wrappedMipSolver.WrappedCutCallbackMipView;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class MipSolverCplex implements
		MipSolverWrapper<IloNumVar, IloRange, IloObjective> {

	private IloCplex cplex;
	private final List<WrappedCutCallback<IloNumVar, IloRange, IloObjective>> wrappedLazyCallbacks;
	private final List<WrappedCutCallback<IloNumVar, IloRange, IloObjective>> wrappedUserCallbacks;
	private Optional<CplexLazyConstraintCallback> lazyConstraint;
	private Optional<CplexUserCutCallback> userCut;

	public MipSolverCplex() {
		try {
			cplex = new IloCplex();
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
		lazyConstraint = Optional.absent();
		userCut = Optional.absent();
		wrappedLazyCallbacks = Lists.newArrayList();
		wrappedUserCallbacks = Lists.newArrayList();
	}

	@Override
	public IloIntVar createIntVar() {
		try {
			return cplex.intVar(Integer.MIN_VALUE, Integer.MAX_VALUE);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IloNumVar createNumVar() {
		try {
			return cplex.numVar(Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isIntVar(IloNumVar variable) {
		return variable instanceof IloIntVar;
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
			return CplexConstants.CPLEX_SOLUTION_STATUS.inverse().get(status);
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
			return cplex.addObjective(CplexConstants.CPLEX_OBJECTIVE_SENSE
					.get(objectiveSense));
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
			return CplexConstants.CPLEX_OBJECTIVE_SENSE.inverse().get(
					objective.getSense());
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

	@Override
	public void destroy() {
		cplex.end();
	}

	@Override
	public void addLazyConstrCallback(
			WrappedCutCallback<IloNumVar, IloRange, IloObjective> lazyConstrCallback) {
		try {
			if (!lazyConstraint.isPresent()) {

				lazyConstraint = Optional.of(new CplexLazyConstraintCallback());

				this.cplex.use(lazyConstraint.get());
			}
			this.wrappedLazyCallbacks.add(lazyConstrCallback);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addUserCutCallback(
			WrappedCutCallback<IloNumVar, IloRange, IloObjective> userCutCallback) {
		try {
			if (!userCut.isPresent()) {

				userCut = Optional.of(new CplexUserCutCallback());

				this.cplex.use(userCut.get());
			}
			this.wrappedUserCallbacks.add(userCutCallback);
		} catch (IloException e) {
			throw new RuntimeException(e);
		}
	}

	private static class ConstraintsBuilder {

		private Table<IloRange, IloNumVar, Double> constraintValues;
		private Set<IloRange> constraints;
		private IloCplex cplex;

		public ConstraintsBuilder(IloCplex cplex) {
			constraintValues = HashBasedTable.create();
			constraints = Sets.newHashSet();
			this.cplex = cplex;
		}

		public void clear() {
			constraintValues.clear();
			constraints.clear();
		}

		public IloRange createConstraint() {

			try {
				IloRange ans = cplex.range(Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY);
				constraints.add(ans);
				return ans;
			} catch (IloException e) {
				throw new RuntimeException(e);
			}

		}

		public void setConstraintCoef(IloRange range, IloNumVar var, double coef) {
			if (!constraints.contains(range)) {
				throw new RuntimeException(
						"Can only modify constraints that have been added inside callback, but this constraint not found: "
								+ range);
			}
			constraintValues.put(range, var, coef);
		}

		public Set<IloRange> getConstraintsAdded() {
			return this.constraints;
		}

		public Map<IloNumVar, Double> getConstraintValues(IloRange range) {
			return this.constraintValues.row(range);
		}

	}

	private class CplexLazyConstraintCallback extends LazyConstraintCallback
			implements
			WrappedCutCallbackMipView<IloNumVar, IloRange, IloObjective> {

		private ConstraintsBuilder constraintsBuilder = new ConstraintsBuilder(
				cplex);

		@Override
		protected void main() throws IloException {
			constraintsBuilder.clear();
			for (WrappedCutCallback<IloNumVar, IloRange, IloObjective> wrappedLazy : wrappedLazyCallbacks) {
				boolean continueCallbacks = wrappedLazy.onCallback(this);
				if (!continueCallbacks) {
					break;
				}
			}
			for (IloRange newConstraint : constraintsBuilder
					.getConstraintsAdded()) {
				IloLinearNumExpr sum = cplex.linearNumExpr();
				for (Map.Entry<IloNumVar, Double> term : constraintsBuilder
						.getConstraintValues(newConstraint).entrySet()) {
					sum.addTerm(term.getValue(), term.getKey());
				}
				newConstraint.setExpr(sum);
				this.add(newConstraint);
			}
		}

		@Override
		public long nodesCreated() {
			try {
				return this.getNnodes64();
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long nodeStackSize() {
			try {
				return this.getNremainingNodes64();
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public double getLPVarValue(IloNumVar var) {
			try {
				return this.getValue(var);
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public IloRange createConstr() {
			return this.constraintsBuilder.createConstraint();
		}

		@Override
		public void setConstrCoef(IloRange constr, IloNumVar var, double value) {
			this.constraintsBuilder.setConstraintCoef(constr, var, value);
		}

		@Override
		public void setConstrUB(IloRange constr, double value) {
			try {
				constr.setUB(value);
			} catch (IloException e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public void setConstrLB(IloRange constr, double value) {
			try {
				constr.setLB(value);
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public double[] getLPVarValues(List<IloNumVar> vars) {
			try {
				return this.getValues(vars.toArray(new IloNumVar[vars.size()]));
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private class CplexUserCutCallback extends UserCutCallback implements
			WrappedCutCallbackMipView<IloNumVar, IloRange, IloObjective> {

		private ConstraintsBuilder constraintsBuilder = new ConstraintsBuilder(
				cplex);

		@Override
		protected void main() throws IloException {
			constraintsBuilder.clear();
			for (WrappedCutCallback<IloNumVar, IloRange, IloObjective> wrappedLazy : wrappedLazyCallbacks) {
				boolean continueCallbacks = wrappedLazy.onCallback(this);
				if (!continueCallbacks) {
					break;
				}
			}
			for (IloRange newConstraint : constraintsBuilder
					.getConstraintsAdded()) {
				IloLinearNumExpr sum = cplex.linearNumExpr();
				for (Map.Entry<IloNumVar, Double> term : constraintsBuilder
						.getConstraintValues(newConstraint).entrySet()) {
					sum.addTerm(term.getValue(), term.getKey());
				}
				newConstraint.setExpr(sum);
				this.add(newConstraint);
			}
		}

		@Override
		public long nodesCreated() {
			try {
				return this.getNnodes64();
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long nodeStackSize() {
			try {
				return this.getNremainingNodes64();
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public double getLPVarValue(IloNumVar var) {
			try {
				return this.getValue(var);
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public IloRange createConstr() {
			return this.constraintsBuilder.createConstraint();
		}

		@Override
		public void setConstrCoef(IloRange constr, IloNumVar var, double value) {
			this.constraintsBuilder.setConstraintCoef(constr, var, value);
		}

		@Override
		public void setConstrUB(IloRange constr, double value) {
			try {
				constr.setUB(value);
			} catch (IloException e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public void setConstrLB(IloRange constr, double value) {
			try {
				constr.setLB(value);
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public double[] getLPVarValues(List<IloNumVar> vars) {
			try {
				return this.getValues(vars.toArray(new IloNumVar[vars.size()]));
			} catch (IloException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/*
	 * @Override public WrappedCutCallbackMipView<IloNumVar, IloRange,
	 * IloObjective> getLazyConstrMipView() { if
	 * (!this.lazyConstraint.isPresent()) { throw new
	 * RuntimeException("Lazy constraint not created."); } return
	 * this.lazyConstraint.get(); }
	 * 
	 * @Override public WrappedCutCallbackMipView<IloNumVar, IloRange,
	 * IloObjective> getUserCutMipView() { if (!this.userCut.isPresent()) {
	 * throw new RuntimeException("User cut not created."); } return
	 * this.userCut.get();// this.lazyConstraint.get(); }
	 */

}
