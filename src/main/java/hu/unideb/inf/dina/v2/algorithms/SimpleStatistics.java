package hu.unideb.inf.dina.v2.algorithms;

import static java.lang.String.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.model.LineChartDataSet;
import hu.unideb.inf.dina.commons.model.Vertex;
import hu.unideb.inf.dina.commons.result.AnalysisResult;
import hu.unideb.inf.dina.commons.result.FileResult;
import hu.unideb.inf.dina.commons.result.LineChartResult;
import hu.unideb.inf.dina.commons.result.MapResult;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;

public class SimpleStatistics implements GraphAnalyzer {

	private static final String STATS_NODE_NUM = "# of nodes";

	private static final String STATS_LINK_NUM = "# of links";

	private static final String STATS_SELF_LOOP_NUM = "# of self loops";

	private static final String STATS_UNIDIR_LINK_NUM = "# of unidirectional links";

	private static final String STATS_BIDIR_LINK_NUM = "# of bidirectional links";

	private static final String STATS_MULTI_LINK_NUM = "# of multilinks";

	private Graph graph;

	private MapResult mapResult = new MapResult();

	private LineChartResult lineChartResult = new LineChartResult();

	private FileResult inDegreeFileResult = new FileResult();

	private FileResult outDegreeFileResult = new FileResult();

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		this.graph = graph;
		calculateLinkStatistics();
		calculateDegreeDistribution();
		lineChartResult.getLineChart().setTitle("Degree distribution");
		return List.of(mapResult, lineChartResult, inDegreeFileResult, outDegreeFileResult);
	}

	@Override
	public String getName() {
		return "Simple graph statistics";
	}

	@Override
	public String getDescription() {
        return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("simple-statistics.html"));
	}

	private void calculateLinkStatistics() {
		int unidirLinkNum = 0;
		int bidirLinkNum = 0;
		int selfLoopNum = 0;
		int multilinkNum = 0;
		var multiEdges = new HashMap<Vertex, Integer>();

		for (Vertex v : graph.getGraph().values()) {
			multiEdges.clear();
			for (Vertex out : v.getOutAdjacents()) {
				if (multiEdges.containsKey(out)) {
					int multiCount = multiEdges.get(out);
					// this check is here to eliminate multiplicities in the outgoing vertices
					if (multiCount == 1) {
						multilinkNum++;
					}
					multiEdges.put(out, multiCount + 1);
				} else {
					multiEdges.put(out, 1);

					if (v.equals(out)) {
						selfLoopNum++;
					} else if (v.getInAdjacents().contains(out)) {
						bidirLinkNum++;
					} else {
						unidirLinkNum++;
					}
				}
			}
		}
		bidirLinkNum = bidirLinkNum / 2;

		mapResult.getResult().put(STATS_NODE_NUM, Integer.toString(graph.getGraph().values().size()));
		mapResult.getResult().put(STATS_LINK_NUM, Integer.toString(graph.getGraph().values().stream().mapToInt(v -> v.getOutAdjacents().size()).sum()));
		mapResult.getResult().put(STATS_SELF_LOOP_NUM, Integer.toString(selfLoopNum));
		mapResult.getResult().put(STATS_UNIDIR_LINK_NUM, Integer.toString(unidirLinkNum));
		mapResult.getResult().put(STATS_BIDIR_LINK_NUM, Integer.toString(bidirLinkNum));
		mapResult.getResult().put(STATS_MULTI_LINK_NUM, Integer.toString(multilinkNum));
	}

	private void calculateDegreeDistribution() {
		var inDegDist = new ArrayList<Integer>();
		var outDegDist = new ArrayList<Integer>();

		for (Vertex v : graph.getGraph().values()) {
			var inCount = v.getInAdjacents().size();
			var inN = inCount - inDegDist.size() + 1;
			if (inN > 0) {
				inDegDist.addAll(Collections.nCopies(inN, 0));
			}
			inDegDist.set(inCount, inDegDist.get(inCount) + 1);

			var outCount = v.getOutAdjacents().size();
			var outN = outCount - outDegDist.size() + 1;
			if (outN > 0) {
				outDegDist.addAll(Collections.nCopies(outN, 0));
			}
			outDegDist.set(outCount, outDegDist.get(outCount) + 1);
		}

		int numberOfNodes = graph.getGraph().values().size();
		List<Integer> inDegreeHorizontal = IntStream.range(0, inDegDist.size())
				.boxed()
				.collect(Collectors.toList());
		List<Double> inDegreeVertical = inDegDist.stream()
				.map(integer -> integer / (double) numberOfNodes)
				.collect(Collectors.toList());

		lineChartResult.getLineChart().getDataSets().add(
				new LineChartDataSet("In-degree", inDegreeVertical, inDegreeHorizontal)
		);
		inDegreeFileResult.setFileName("in-degree");
		for (int i = 0; i < inDegreeHorizontal.size(); i++) {
			inDegreeFileResult.addLine(join(";", inDegreeHorizontal.get(i).toString(), inDegreeVertical.get(i).toString()));
		}

		List<Integer> outDegreeHorizontal = IntStream.range(0, outDegDist.size())
				.boxed()
				.collect(Collectors.toList());
		List<Double> outDegreeVertical = outDegDist.stream()
				.map(integer -> integer / (double) numberOfNodes)
				.collect(Collectors.toList());

		lineChartResult.getLineChart().getDataSets().add(
				new LineChartDataSet("Out-degree", outDegreeVertical, outDegreeHorizontal)
		);

		outDegreeFileResult.setFileName("out-degree");
		for (int i = 0; i < outDegreeHorizontal.size(); i++) {
			outDegreeFileResult.addLine(join(";", outDegreeHorizontal.get(i).toString(), outDegreeVertical.get(i).toString()));
		}
	}
}
