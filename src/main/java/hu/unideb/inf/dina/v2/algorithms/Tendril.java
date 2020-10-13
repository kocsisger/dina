package hu.unideb.inf.dina.v2.algorithms;

import static java.lang.String.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.model.Vertex;
import hu.unideb.inf.dina.commons.result.AnalysisResult;
import hu.unideb.inf.dina.commons.result.FileResult;
import hu.unideb.inf.dina.commons.result.TableResult;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;

public class Tendril implements GraphAnalyzer {

	public enum Type {
		IN, OUT, INOUT, UNDEFINED
	}

	private static final String TENDRIL_TABLE_LAYER = "Layer";

	private static final String TENDRIL_TABLE_IN = "In";

	private static final String TENDRIL_TABLE_IN_OUT = "In Out";

	private static final String TENDRIL_TABLE_OUT = "Out";

	private FileResult fileResult = new FileResult();

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		graph.getGraph().values().forEach(v -> {
			v.getProperties().put("type", Type.UNDEFINED);
			v.getProperties().put("layer", -1);
		});

		var tarjan = new Tarjan();
		tarjan.analyze(graph);

		var inOut = tarjan.getGs();
		var in = new ArrayList<Vertex>();
		var out = new ArrayList<Vertex>();
		int layerNum = 0;

		for (Vertex vertex : inOut) {
			vertex.getProperties().put("layer", layerNum);
			vertex.getProperties().put("type", Type.INOUT);
		}

		do {
			inOut.addAll(in);
			inOut.addAll(out);
			in.clear();
			out.clear();

			findGinIterative(inOut, in, layerNum);
			findGoutIterative(inOut, out, layerNum);

			in.forEach(v -> {
				if (out.contains(v)) {
					v.getProperties().put("type", Type.INOUT);
				}
			});

			layerNum++;
		} while (!in.isEmpty() || !out.isEmpty());

		List<List<Integer>> elementsIn = new ArrayList<>();
		List<List<Integer>> elementsInOut = new ArrayList<>();
		List<List<Integer>> elementsOut = new ArrayList<>();

		for (int i = 0; i < layerNum - 1; i++) {
			elementsIn.add(new ArrayList<>());
			elementsInOut.add(new ArrayList<>());
			elementsOut.add(new ArrayList<>());
		}

		for (Vertex v : graph.getGraph().values()) {
			switch ((Type) v.getProperties().get("type")) {
			case IN:
				elementsIn.get((Integer) v.getProperties().get("layer")).add(v.getId());
				break;
			case OUT:
				elementsOut.get((Integer) v.getProperties().get("layer")).add(v.getId());
				break;
			case INOUT:
				elementsInOut.get((Integer) v.getProperties().get("layer")).add(v.getId());
				break;
			}
		}

		var tableResult = new TableResult();
		tableResult.addHeader(List.of(TENDRIL_TABLE_LAYER, TENDRIL_TABLE_IN, TENDRIL_TABLE_IN_OUT, TENDRIL_TABLE_OUT));
		for (int i = 0; i < layerNum - 1; i++) {
			tableResult.addRow(List.of(
					Integer.toString(i),
					elementListToString(elementsIn.get(i)),
					elementListToString(elementsInOut.get(i)),
					elementListToString(elementsOut.get(i))
			));
		}
		var fileResult = FileResult.fromTableResult("tendril", tableResult);
		return List.of(tableResult, fileResult);
	}

	@Override
	public String getName() {
		return "Tendril";
	}

	@Override
	public String getDescription() {
		return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("tendril.html"));
	}

	private void findGinIterative(List<Vertex> inOut, List<Vertex> in, int layerNum) {
		var nodesToProcess = new ArrayList<Vertex>();
		// Put all the 1st level in adjacents to the list
		inOut.forEach(vertex -> addInAdjacentsToProcessList(vertex, nodesToProcess, layerNum));

		while (!nodesToProcess.isEmpty()) {
			var v = nodesToProcess.remove(0);
			addInAdjacentsToProcessList(v, nodesToProcess, layerNum);
			in.add(v);
			v.getProperties().put("layer", layerNum);
		}
	}

	private void findGoutIterative(List<Vertex> inOut, List<Vertex> out, int layerNum) {
		var nodesToProcess = new ArrayList<Vertex>();
		// Put all the 1st level in adjacents to the list
		inOut.forEach(vertex -> addOutAdjacentsToProcessList(vertex, nodesToProcess, layerNum));

		while (!nodesToProcess.isEmpty()) {
			var v = nodesToProcess.remove(0);
			addOutAdjacentsToProcessList(v, nodesToProcess, layerNum);
			out.add(v);
			v.getProperties().put("layer", layerNum);
		}
	}

	private void addInAdjacentsToProcessList(Vertex v, List<Vertex> nodesToProcess, int layerNum) {
		v.getInAdjacents().forEach(vin -> {
			if (vin.getProperties().get("type") == Type.UNDEFINED) {
				vin.getProperties().put("type", Type.IN);
				nodesToProcess.add(vin);
			} else if (vin.getProperties().get("type") == Type.OUT && ((Integer) vin.getProperties().get("layer")) == layerNum) {
				vin.getProperties().put("type", Type.INOUT);
				nodesToProcess.add(vin);
			}
		});
	}

	private void addOutAdjacentsToProcessList(Vertex v, List<Vertex> nodesToProcess, int layerNum) {
		v.getOutAdjacents().forEach(vout -> {
			if (vout.getProperties().get("type") == Type.UNDEFINED) {
				vout.getProperties().put("type", Type.OUT);
				nodesToProcess.add(vout);
			} else if (vout.getProperties().get("type") == Type.IN && ((Integer) vout.getProperties().get("layer")) == layerNum) {
				vout.getProperties().put("type", Type.INOUT);
				nodesToProcess.add(vout);
			}
		});
	}

	private String elementListToString(List<Integer> elements) {
		return elements.size() + "("
				+ join(",", elements.stream().map(Object::toString).collect(Collectors.toList()))
				+ ")";
	}
}
