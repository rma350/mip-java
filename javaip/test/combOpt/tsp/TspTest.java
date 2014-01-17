package combOpt.tsp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TspTest {

	private static double solutionTolerance = .0001;

	@Test
	public void test() {
		String[] instanceNames = new String[] { "minimal", "shortCycles",
				"eil51" };
		// "bier127" };
		for (String instanceName : instanceNames) {
			System.out.println("Testing tsp on instance: " + instanceName);
			GeoTspTestInstance instance = GeoTspTestReader
					.readTest(instanceName);
			Tsp tsp = new Tsp(instance.getGraph());
			// EnumSet.noneOf(TspOption.class));
			assertEquals(instance.getOptSolution(), tsp.getSolutionCost(),
					solutionTolerance);
		}
	}

}
