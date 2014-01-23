package combOpt.knapsack;

import static org.junit.Assert.assertEquals;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import easy.EasyMip.SolverType;

@RunWith(Theories.class)
public class KnapsackTest {

	@DataPoints
	public static SolverType[] solverTypes = SolverType.values();

	@DataPoints
	public static int[] knapsackInstances = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };

	@Theory
	public void testKnapsack(SolverType solverType, int knapsackInstance) {
		System.out.println("Testing knapsack instance: " + knapsackInstance
				+ " with solver: " + solverType);
		KnapsackTestInstance instance = KnapsackTestReader
				.readTestInstance(knapsackInstance);
		Knapsack solver = new Knapsack(instance.getItems(),
				instance.getCapacity(), solverType);
		assertEquals(instance.getOptimalItems(), solver.getItemsSolution());
	}

}
