package util;

import java.util.List;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.collect.Lists;

public class IntListIntHashDoubleTable implements IntIntDoubleTable {

	private List<OpenIntToDoubleHashMap> values;

	public IntListIntHashDoubleTable() {
		this.values = Lists.newArrayList();
	}

	@Override
	public double get(int constraint, int variable) {
		if (values.size() < constraint) {
			OpenIntToDoubleHashMap constraintVals = values.get(constraint);
			if (constraintVals.containsKey(variable)) {
				return constraintVals.get(variable);
			}
		}
		return 0;

	}

	public boolean hasEntry(int constraint, int variable) {
		return values.size() < constraint
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
		while (constraint < values.size()) {
			values.add(new OpenIntToDoubleHashMap());
		}
	}

}
