package testJava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MyCode<T> {
	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		try {

			String CurrentLine;
			int count = 0;
			Graph<String> graph = new Graph<String>();

			//System.out.println("Enter input");
			br = new BufferedReader(new InputStreamReader(System.in));
			while ((CurrentLine = br.readLine()) != null) {
				count++;
				String[] splited = CurrentLine.split("\\s+");

				if (splited[0].equals("add")) {
					graph.addEdge(splited[1], splited[2]);

				} else if (splited[0].equals("remove")) {

					if (!graph.containsVertex(splited[1]) || !graph.containsVertex(splited[2])) {
						throw new RuntimeException(
								"Edge you are trying to remove on line " + count + ", does not exist.");
					}

					graph.removeEdge(splited[1], splited[2]);

				} else if (splited[0].equals("is")) {

					if (splited[1].equals("linked")) {
						System.out.println(graph.runBFS(splited[2], splited[3]));
					}

				} else {
					// System.out.println("Invalid input.. Please enter the correct input operation");
					// throw new RuntimeException("Input of line" + count + "unidentifiable");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}

// Graph class ---------------------
class Graph<T> {

	private Map<T, Node<T>> adjacencyList;

	public Graph() {
		adjacencyList = new HashMap<>();
	}

	public void addVertex(T vertex) {
		if (adjacencyList.containsKey(vertex) == false) {
			adjacencyList.put(vertex, new Node<>(vertex));
		}

	}

	public void addEdge(T vertex1, T vertex2) {

		if (!containsVertex(vertex1)) {
			addVertex(vertex1);
		}
		if (!containsVertex(vertex2)) {
			addVertex(vertex2);
		}

		// add the edge
		Node<T> node1 = getNode(vertex1);
		Node<T> node2 = getNode(vertex2);
		node1.addEdge(node2);
		node2.addEdge(node1);
	}

	public void removeEdge(T vertex1, T vertex2) {
		if (containsVertex(vertex1) || containsVertex(vertex2)) {
			getNode(vertex1).removeEdge(getNode(vertex2));
			getNode(vertex2).removeEdge(getNode(vertex1));
		}
	}

	public boolean containsVertex(T vertex) {
		return adjacencyList.containsKey(vertex);
	}

	public boolean runBFS(T startVertex, T endVertex) {
		if (!containsVertex(startVertex)) {
			throw new RuntimeException("Start Vertex you specified does not exist.");
		}
		
		if (!containsVertex(endVertex)) {
            throw new RuntimeException("End Vertex you specified does not exist.");
        }

		resetGraph();

		// Queue Initialization
		Queue<Node<T>> queue = new LinkedList<>();
		Node<T> start = getNode(startVertex);
		queue.add(start);

		// explore the graph
		while (!queue.isEmpty()) {
			Node<T> first = queue.remove();
			first.setVisited(true);
			first.edges().forEach(edge -> {
				Node<T> neighbour = edge.toNode();
				if (!neighbour.isVisited()) {
					queue.add(neighbour);
				}
			});
		}

		Node<T> end = getNode(endVertex);

		if (end.isVisited()) {
			return true;
		} else {
			return false;
		}
	}

	private Node<T> getNode(T value) {
		return adjacencyList.get(value);
	}

	private void resetGraph() {
		adjacencyList.keySet().forEach(key -> {
			Node<T> node = getNode(key);
			node.setVisited(false);
		});
	}
}

// Node class ------------------------
class Node<T> {

	private T vertex;
	private List<Edge<T>> edges;
	private boolean isVisited;

	public Node(T vertex) {
		this.vertex = vertex;
		this.edges = new ArrayList<>();
	}

	public void addEdge(Node<T> node) {
		if (!hasEdge(node)) {
			Edge<T> newEdge = new Edge<>(this, node);
			edges.add(newEdge);
		}
	}

	public boolean removeEdge(Node<T> node) {
		Optional<Edge<T>> optional = findEdge(node);
		if (optional.isPresent()) {
			return edges.remove(optional.get());
		}
		return false;
	}

	public boolean hasEdge(Node<T> node) {
		return findEdge(node).isPresent();
	}

	private Optional<Edge<T>> findEdge(Node<T> node) {
		return edges.stream().filter(edge -> edge.isBetween(this, node)).findFirst();
	}

	public List<Edge<T>> edges() {
		return edges;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

}

// edge class-------------------
class Edge<T> {
	private Node<T> node1;
	private Node<T> node2;

	public Edge(Node<T> node1, Node<T> node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public Node<T> toNode() {
		return node2;
	}

	public boolean isBetween(Node<T> node1, Node<T> node2) {
		return (this.node1 == node1 && this.node2 == node2);
	}
}
