package combOpt.knapsack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import combOpt.knapsack.Knapsack;

public class KnapsackTest {

	@Test
	public void test() {
		for (int i = 1; i <= 8; i++) {
			System.out.println("Testing knapsack instance: " + i);
			KnapsackTestInstance instance = KnapsackTestReader
					.readTestInstance(i);
			Knapsack solver = new Knapsack(instance.getItems(),
					instance.getCapacity());
			assertEquals(instance.getOptimalItems(), solver.getItemsSolution());
		}
	}

}
