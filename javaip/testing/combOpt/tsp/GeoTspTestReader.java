package combOpt.tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import combOpt.tsp.GeoTspTestInstance.Point;

public class GeoTspTestReader {

	private static final String dataDir = "testData" + File.separator
			+ "combOpt" + File.separator + "tsp" + File.separator;

	public static GeoTspTestInstance readTest(String testName) {
		List<Point> points = readPointsFile(dataDir + testName + "-points.txt",
				",");
		int optSol = readValueFile(dataDir + testName + "-solution.txt");
		return new GeoTspTestInstance(points, optSol);
	}

	private static ImmutableList<Point> readPointsFile(String fileName,
			String delim) {
		try {
			Splitter splitter = Splitter.on(delim).omitEmptyStrings()
					.trimResults();
			ImmutableList.Builder<Point> ans = ImmutableList.builder();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader
					.readLine()) {
				String lineClean = nextLine.trim();
				if (!lineClean.isEmpty()) {
					List<String> lineSplit = splitter.splitToList(lineClean);
					ans.add(new Point(Integer.parseInt(lineSplit.get(0)),
							Integer.parseInt(lineSplit.get(1))));
				}
			}
			reader.close();
			return ans.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static int readValueFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String firstLine = reader.readLine().trim();
			int ans = Integer.parseInt(firstLine);
			reader.close();
			return ans;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
