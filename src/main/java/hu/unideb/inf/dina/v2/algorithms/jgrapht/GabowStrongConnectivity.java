package hu.unideb.inf.dina.v2.algorithms.jgrapht;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.result.AnalysisResult;
import hu.unideb.inf.dina.commons.result.FileResult;
import hu.unideb.inf.dina.commons.result.MapResult;
import hu.unideb.inf.dina.commons.result.TableResult;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;

import hu.unideb.inf.dina.v2.utils.JGraphTUtils;

public class GabowStrongConnectivity implements GraphAnalyzer {

	private static final String GABOW_NUMBER_OF_NODES = "# of nodes";

	private static final String GABOW_NUMBER_OF_COMPONENTS = "# of components";

	private static final String GABOW_BIGGEST_COMPONENT_SIZE = "Size of the biggest component";

	private static final String GABOW_TABLE_COMPONENT_NUM = "Component num";

	private static final String GABOW_TABLE_SIZE = "Size";

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		var inspector = new GabowStrongConnectivityInspector<>(JGraphTUtils.convertDinaGraphToJGraphT(graph));
		var stronglyConnectedSets = inspector.stronglyConnectedSets();

		var mapResult = new MapResult();

		mapResult.getResult().put(GABOW_NUMBER_OF_NODES, Integer.toString(graph.getGraph().values().size()));
		mapResult.getResult().put(GABOW_NUMBER_OF_COMPONENTS, Integer.toString(stronglyConnectedSets.size()));
		mapResult.getResult().put(GABOW_BIGGEST_COMPONENT_SIZE, stronglyConnectedSets.stream()
				.map(Set::size)
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList())
				.get(0).toString());

		var tableResult = TableResult.fromList(List.of(GABOW_TABLE_COMPONENT_NUM, GABOW_TABLE_SIZE), stronglyConnectedSets);

		var fileResult = FileResult.fromTableResult("gabow", tableResult);

		return List.of(mapResult, tableResult, fileResult);
	}

	@Override
	public String getName() {
		return "Gabow (JGraphT)";
	}

	@Override
	public String getDescription() {
		return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("jgrapht/gabow-strong-connectivity.html"));
	}
}
