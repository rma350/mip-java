package combOpt.knapsack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.collect.ImmutableList;

import combOpt.knapsack.Knapsack.Item;

public class KnapsackTestReader {

	private static final String dataDir = "testData" + File.separator
			+ "combOpt" + File.separator + "knapsack" + File.separator;

	public static KnapsackTestInstance readTestInstance(int problemNumber) {
		if (problemNumber < 1 || problemNumber > 8) {
			throw new RuntimeException("Proiblem number must be in [1..8].");
		}
		int capacity = readValueFile(fileNameCapacity(problemNumber));
		ImmutableList<Integer> profits = readValueListFile(fileNameProfits(problemNumber));
		ImmutableList<Integer> weights = readValueListFile(fileNameWeights(problemNumber));
		ImmutableList<Integer> itemSolution = readValueListFile(fileNameItemsSolution(problemNumber));
		if (profits.size() != weights.size()) {
			throw new RuntimeException();
		}
		if (weights.size() != itemSolution.size()) {
			throw new RuntimeException();
		}
		ImmutableList.Builder<Item> items = ImmutableList.builder();
		ImmutableList.Builder<Boolean> inSolution = ImmutableList.builder();
		for (int i = 0; i < profits.size(); i++) {
			items.add(new Item(weights.get(i), profits.get(i)));
			inSolution.add(itemSolution.get(i).intValue() == 1);
		}
		return new KnapsackTestInstance(items.build(), capacity,
				inSolution.build());
	}

	private static String fileNameCapacity(int problemNumber) {
		return dataDir + "p0" + problemNumber + "_c.txt";
	}

	private static String fileNameProfits(int problemNumber) {
		return dataDir + "p0" + problemNumber + "_p.txt";
	}

	private static String fileNameWeights(int problemNumber) {
		return dataDir + "p0" + problemNumber + "_w.txt";
	}

	private static String fileNameItemsSolution(int problemNumber) {
		return dataDir + "p0" + problemNumber + "_s.txt";
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

	private static ImmutableList<Integer> readValueListFile(String fileName) {
		try {
			ImmutableList.Builder<Integer> ans = ImmutableList.builder();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader
					.readLine()) {
				String lineClean = nextLine.trim();
				if (!lineClean.isEmpty()) {
					ans.add(Integer.parseInt(lineClean));
				}
			}
			reader.close();
			return ans.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
