package combOpt.graph;

public class DirectedEdge {

	private Node source;
	private Node target;

	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

	public DirectedEdge(Node source, Node target) {
		super();
		this.source = source;
		this.target = target;
	}

	@Override
	public String toString() {
		return "e" + source.getId() + "->" + target.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		DirectedEdge other = (DirectedEdge) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}
