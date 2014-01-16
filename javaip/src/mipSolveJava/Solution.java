package mipSolveJava;

import java.util.List;

public class Solution {
	private final double objValue;
	private final double[] variableValues;
	private final List<Boolean> integerVariables;
	private final double integralityTol;
	private final boolean integral;

	public double getObjValue() {
		return objValue;
	}

	public double[] getVariableValues() {
		return variableValues;
	}

	public boolean isIntegral() {
		return integral;
	}

	public Solution(double objValue, double[] variableValues,
			double integralityTol, List<Boolean> integerVariables) {
		super();
		this.objValue = objValue;
		this.variableValues = variableValues;
		this.integralityTol = integralityTol;
		this.integerVariables = integerVariables;
		this.integral = isInteger();
	}

	public boolean indexIntegral(int varIndex) {
		double val = variableValues[varIndex];
		return Math.abs(val - Math.round(val)) < integralityTol;
	}

	private boolean isInteger() {
		for (int i = 0; i < variableValues.length; i++) {
			if (integerVariables.get(i) && !indexIntegral(i)) {
				return false;
			}
		}
		return true;
	}
}