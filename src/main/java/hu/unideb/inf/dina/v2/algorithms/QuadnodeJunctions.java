package hu.unideb.inf.dina.v2.algorithms;

import static java.lang.String.join;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.model.Vertex;
import hu.unideb.inf.dina.commons.result.AnalysisResult;
import hu.unideb.inf.dina.commons.result.FileResult;
import hu.unideb.inf.dina.commons.result.MapResult;
import hu.unideb.inf.dina.v2.model.QuadnodeJunction;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;

public class QuadnodeJunctions implements GraphAnalyzer {

	private static final String QUADNODE_JUNCTIONS_COUNT_TOTAL = "Total junctions count";

	private static final String QUADNODE_JUNCTIONS_COUNT_TYPE_PREFIX = "Junctions count type #";

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		MapResult mapResult = new MapResult();
		FileResult fileResult = new FileResult();

		Set<QuadnodeJunction> junctions = new HashSet<>();

		for (Vertex vertexA : graph.getGraph().values()) {
			var aAdjacents = vertexA.getAllAdjacents();
			for (Vertex vertexB : aAdjacents) {
				var bAdjacents = vertexB.getAllAdjacents();
				bAdjacents.remove(vertexA);
				for (Vertex vertexC : bAdjacents) {
					var cAdjacents = vertexC.getAllAdjacents();
					cAdjacents.remove(vertexA);
					cAdjacents.remove(vertexB);
					for (Vertex vertexD : cAdjacents) {
						junctions.add(new QuadnodeJunction(vertexA, vertexB, vertexC, vertexD));
					}
				}
			}
		}

		junctions.forEach(QuadnodeJunction::calculateType);

		for (int i = 0; i < QuadnodeJunction.NUMBER_OF_TYPES; i++) {
			int type = i;
			long count = junctions.stream()
					.filter(junction -> junction.getType() == type)
					.count();
			mapResult.getResult().put(QUADNODE_JUNCTIONS_COUNT_TYPE_PREFIX + (i + 1), Long.toString(count));
			fileResult.addLine(join(";", Integer.toString(i + 1), Long.toString(count)));
		}

		mapResult.getResult().put(QUADNODE_JUNCTIONS_COUNT_TOTAL, Integer.toString(junctions.size()));
		fileResult.setFileName("quadnode_junctions");

		return List.of(mapResult, fileResult);
	}

	@Override
	public String getName() {
		return "Quadnode junctions";
	}

	@Override
	public String getDescription() {
		return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("quadnode-junctions.html"));
	}
}
