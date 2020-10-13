package hu.unideb.inf.dina.v2.utils;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;

import hu.unideb.inf.dina.commons.model.Vertex;

public class JGraphTUtils {

	public static DirectedPseudograph<Integer, DefaultEdge> convertDinaGraphToJGraphT(hu.unideb.inf.dina.commons.model.Graph dinaGraph) {
		var jGraph = new DirectedPseudograph<Integer, DefaultEdge>(DefaultEdge.class);

		dinaGraph.getGraph().values().stream().map(Vertex::getId).forEach(jGraph::addVertex);
		dinaGraph.getEdges().forEach(edge -> jGraph.addEdge(edge.getFrom(), edge.getTo()));

		return jGraph;
	}
}
