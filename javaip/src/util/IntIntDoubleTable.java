package util;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

public interface IntIntDoubleTable {

	public double get(int constraint, int variable);

	public void set(int constraint, int variable, double value);

	public boolean hasEntry(int constraint, int variable);

	public OpenIntToDoubleHashMap.Iterator getConstraint(int constraint);

}
