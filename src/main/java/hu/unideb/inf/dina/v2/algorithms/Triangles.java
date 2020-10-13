package hu.unideb.inf.dina.v2.algorithms;

import static java.lang.String.join;

import java.util.ArrayList;
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
import hu.unideb.inf.dina.v2.model.Triangle;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;

public class Triangles implements GraphAnalyzer {

	private static final String TRIANGLES_COUNT_TOTAL = "Total triangles count";

	private static final String TRIANGLES_COUNT_TYPE_PREFIX = "Triangles count type #";

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		MapResult mapResult = new MapResult();
		FileResult fileResult = new FileResult();
		Set<Triangle> triangles = new HashSet<>();

		for (Vertex vertex : graph.getGraph().values()) {
			var adjs = vertex.getAllAdjacents();

			for (int i = 0; i < adjs.size(); i++) {
				for (int j = i + 1; j < adjs.size(); j++) {
					var iVertex = adjs.get(i);
					var jVertex = adjs.get(j);
					var commonAdjs = new ArrayList<>(iVertex.getAllAdjacents());
					commonAdjs.retainAll(jVertex.getAllAdjacents());
					for (Vertex cVertex : commonAdjs) {
						triangles.add(new Triangle(iVertex, jVertex, cVertex));
					}
				}
			}
		}

		triangles.forEach(Triangle::calculateType);

		for (int i = 0; i < Triangle.TriangleType.values().length; i++) {
			int type = i;
			long count = triangles.stream()
					.filter(triangle -> triangle.getType().ordinal() == type)
					.count();
			mapResult.getResult().put(TRIANGLES_COUNT_TYPE_PREFIX + (i + 1), Long.toString(count));
			fileResult.addLine(join(";", Integer.toString(i + 1), Long.toString(count)));
		}
		mapResult.getResult().put(TRIANGLES_COUNT_TOTAL, Integer.toString(triangles.size()));
		fileResult.setFileName("triangles");
		return List.of(mapResult, fileResult);
	}

	@Override
	public String getName() {
		return "Triangles";
	}

	@Override
	public String getDescription() {
		return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("triangles.html"));
	}

}
