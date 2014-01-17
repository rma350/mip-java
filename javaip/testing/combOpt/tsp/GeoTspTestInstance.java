package combOpt.tsp;

import java.util.List;

import com.google.common.collect.Lists;

public class GeoTspTestInstance {

	private List<Point> points;
	private UndirectedGraph graph;
	private int optSolution;

	public GeoTspTestInstance(List<Point> points, int optSolution) {
		this.points = points;
		this.graph = createGeoGraph(points);
		this.optSolution = optSolution;
	}

	public List<Point> getPoints() {
		return points;
	}

	public UndirectedGraph getGraph() {
		return graph;
	}

	public int getOptSolution() {
		return optSolution;
	}

	public static class Point {
		private int x;
		private int y;

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}

	public static int distance(Point p1, Point p2) {
		double xDiff = p1.getX() - p2.getX();
		double yDiff = p1.getY() - p2.getY();
		return (int) Math.round(Math.sqrt(xDiff * xDiff + yDiff * yDiff));
	}

	public static UndirectedGraph createGeoGraph(List<Point> points) {
		UndirectedGraph graph = new UndirectedGraph();
		List<Node> nodes = Lists.newArrayList();
		for (int i = 0; i < points.size(); i++) {
			nodes.add(graph.addNode());
			for (int j = 0; j < i; j++) {
				graph.addEdge(nodes.get(j), nodes.get(i),
						distance(points.get(j), points.get(i)));
			}
		}
		return graph;
	}

}
