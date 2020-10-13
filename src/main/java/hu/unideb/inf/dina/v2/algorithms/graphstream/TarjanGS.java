package hu.unideb.inf.dina.v2.algorithms.graphstream;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.result.AnalysisResult;
import hu.unideb.inf.dina.commons.result.FileResult;
import hu.unideb.inf.dina.commons.result.MapResult;
import hu.unideb.inf.dina.commons.result.TableResult;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;

import hu.unideb.inf.dina.v2.utils.GraphStreamUtils;

public class TarjanGS implements GraphAnalyzer {

	private static final String GS_NUMBER_OF_NODES = "# of nodes";

	private static final String GS_NUMBER_OF_COMPONENTS = "# of components";

	private static final String GS_BIGGEST_COMPONENT_SIZE = "Size of the biggest component";

	private static final String GS_TABLE_COMPONENT_NUM = "Component num";

	private static final String GS_TABLE_SIZE = "Size";

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		var tscc = new TarjanStronglyConnectedComponents();
		var gsGraph = GraphStreamUtils.convertDinaGraphToGraphStream(graph);

		tscc.init(gsGraph);
		tscc.compute();
		var sccs = gsGraph.nodes()
				.map(node -> (Integer) node.getAttribute(tscc.getSCCIndexAttribute()))
				.collect(Collectors.groupingBy(i -> i, Collectors.counting()));

		var mapResult = new MapResult();
		mapResult.getResult().put(GS_NUMBER_OF_NODES, Integer.toString(graph.getGraph().values().size()));
		mapResult.getResult().put(GS_NUMBER_OF_COMPONENTS, Integer.toString(sccs.keySet().size()));
		mapResult.getResult().put(GS_BIGGEST_COMPONENT_SIZE,
				sccs.values().stream().sorted().collect(Collectors.toList()).get(0).toString()
		);

		var tableResult = new TableResult();
		tableResult.addHeader(List.of(GS_TABLE_COMPONENT_NUM, GS_TABLE_SIZE));
		for (Integer key : sccs.keySet()) {
			tableResult.addRow(List.of(
					Integer.toString(key),
					Long.toString(sccs.get(key))
			));
		}

		var fileResult = FileResult.fromTableResult("tarjan_gs", tableResult);

		return List.of(mapResult, tableResult, fileResult);
	}

	@Override
	public String getName() {
		return "Tarjan (GraphStream)";
	}

	@Override
	public String getDescription() {
		return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("graphstream/tarjan-gs.html"));
	}
}
