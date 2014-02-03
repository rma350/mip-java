package combOpt.tsp;

import static org.junit.Assert.assertEquals;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import easy.EasyMip.SolverType;
//import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class TspTest {

	private static double solutionTolerance = .0001;

	@DataPoints
	public static SolverType[] solverTypes = SolverType.values();

	@DataPoints
	public static String[] instanceNames = new String[] { "minimal",
			"shortCycles", "eil51", "bier127" };

	@Theory
	public void test(SolverType solverType, String instanceName) {

		// assumeTrue(instanceName.equals("bier127")
		// && solverType == SolverType.JAVA);
		System.out.println("Testing tsp on instance: " + instanceName
				+ " with solver: " + solverType);
		GeoTspTestInstance instance = GeoTspTestReader.readTest(instanceName);
		Tsp tsp = new Tsp(instance.getGraph(), solverType);
		assertEquals(instance.getOptSolution(), tsp.getSolutionCost(),
				solutionTolerance);
	}

}
