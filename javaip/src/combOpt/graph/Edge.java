package combOpt.graph;

import com.google.common.collect.ImmutableSet;

public class Edge {

	private ImmutableSet<Node> endpoints;

	public ImmutableSet<Node> getEndpoints() {
		return endpoints;
	}

	Edge(ImmutableSet<Node> endpoints) {
		super();
		this.endpoints = endpoints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endpoints == null) ? 0 : endpoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (endpoints == null) {
			if (other.endpoints != null)
				return false;
		} else if (!endpoints.equals(other.endpoints))
			return false;
		return true;
	}

}
