package util;

import java.util.Map;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.collect.Maps;

public class IntHashIntHashDoubleTable implements IntIntDoubleTable {

	private Map<Integer, OpenIntToDoubleHashMap> values;

	public IntHashIntHashDoubleTable() {
		this.values = Maps.newHashMap();
	}

	@Override
	public double get(int constraint, int variable) {
		if (values.containsKey(constraint)) {
			OpenIntToDoubleHashMap constraintVals = values.get(constraint);
			if (constraintVals.containsKey(variable)) {
				return constraintVals.get(variable);
			}
		}
		return 0;

	}

	public boolean hasEntry(int constraint, int variable) {
		return values.containsKey(constraint)
				&& values.get(constraint).containsKey(variable);
	}

	@Override
	public void set(int constraint, int variable, double value) {
		ensureExists(constraint);
		values.get(constraint).put(variable, value);
	}

	@Override
	public OpenIntToDoubleHashMap.Iterator getConstraint(int constraint) {
		ensureExists(constraint);
		return values.get(constraint).iterator();
	}

	private void ensureExists(int constraint) {
		if (constraint < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (!values.containsKey(constraint)) {
			values.put(constraint, new OpenIntToDoubleHashMap());
		}
	}

}
