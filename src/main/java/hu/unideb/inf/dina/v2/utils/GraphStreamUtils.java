package hu.unideb.inf.dina.v2.utils;

import java.util.UUID;

import org.graphstream.graph.implementations.MultiGraph;

import hu.unideb.inf.dina.commons.model.Vertex;

public class GraphStreamUtils {

	public static MultiGraph convertDinaGraphToGraphStream(hu.unideb.inf.dina.commons.model.Graph dinaGraph) {
		var multi = new MultiGraph(UUID.randomUUID().toString());

		dinaGraph.getGraph().values().stream().map(Vertex::getId).map(id -> Integer.toString(id)).forEach(multi::addNode);
		dinaGraph.getEdges().forEach(edge ->
				multi.addEdge(
						UUID.randomUUID().toString(),
						Integer.toString(edge.getFrom()),
						Integer.toString(edge.getTo()),
						true
				));

		return multi;
	}
}
