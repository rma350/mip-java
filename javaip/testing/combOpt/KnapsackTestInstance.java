package combOpt;

import com.google.common.collect.ImmutableList;
import combOpt.Knapsack.Item;

public class KnapsackTestInstance {

	private final ImmutableList<Item> items;
	private final double capacity;
	private final ImmutableList<Boolean> optimalItems;

	public KnapsackTestInstance(ImmutableList<Item> items, double capacity,
			ImmutableList<Boolean> optimalItems) {
		super();
		this.items = items;
		this.capacity = capacity;
		this.optimalItems = optimalItems;
	}

	public ImmutableList<Item> getItems() {
		return items;
	}

	public double getCapacity() {
		return capacity;
	}

	public ImmutableList<Boolean> getOptimalItems() {
		return optimalItems;
	}

}
