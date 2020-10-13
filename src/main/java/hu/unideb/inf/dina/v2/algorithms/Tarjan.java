package hu.unideb.inf.dina.v2.algorithms;

import static java.lang.String.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

import hu.unideb.inf.dina.commons.analysis.GraphAnalyzer;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.model.Vertex;
import hu.unideb.inf.dina.commons.result.AnalysisResult;
import hu.unideb.inf.dina.commons.result.FileResult;
import hu.unideb.inf.dina.commons.result.MapResult;
import hu.unideb.inf.dina.commons.result.TableResult;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;

public class Tarjan implements GraphAnalyzer {

	private static final String TARJAN_NUMBER_OF_NODES = "# of nodes";

	private static final String TARJAN_NUMBER_OF_COMPONENTS = "# of components";

	private static final String TARJAN_BIGGEST_COMPONENT_SIZE = "Size of the biggest component";

	private static final String TARJAN_TABLE_COMPONENT_NUM = "Component num";

	private static final String TARJAN_TABLE_SIZE = "Size";

	private List<List<Vertex>> scc = new ArrayList<>();

	private Stack<Vertex> stack = new Stack<>();

	private FileResult fileResult = new FileResult();

	@Override
	public Collection<AnalysisResult<?>> analyze(Graph graph) {
		graph.getGraph().values().forEach(v -> {
			v.getProperties().put("index", -1);
			v.getProperties().put("lowlink", -1);
			v.getProperties().put("caller", null);
			v.getProperties().put("adjIdx", 0);
		});

		graph.getGraph().values().forEach(v -> {
			if (((Integer) v.getProperties().get("index")) < 0) {
				this.strongConnect(v);
			}
		});

		var mapResult = new MapResult();
		mapResult.getResult().put(TARJAN_NUMBER_OF_NODES, Integer.toString(graph.getGraph().values().size()));
		mapResult.getResult().put(TARJAN_NUMBER_OF_COMPONENTS, Integer.toString(scc.size()));
		mapResult.getResult().put(TARJAN_BIGGEST_COMPONENT_SIZE, scc.stream()
				.map(List::size)
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList())
				.get(0).toString());

		var tableResult = TableResult.fromList(List.of(TARJAN_TABLE_COMPONENT_NUM, TARJAN_TABLE_SIZE), scc);

		var fileResult = FileResult.fromTableResult("tarjan", tableResult);
		return List.of(mapResult, tableResult, fileResult);
	}

	@Override
	public String getName() {
		return "Tarjan";
	}

	@Override
	public String getDescription() {
		return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("tarjan.html"));
	}

	/**
	 * {@link #analyze(Graph)} must be called to generate the components used in this method.
	 */
	public List<Vertex> getGs() {
		List<Vertex> gs = null;
		for (List<Vertex> component : scc) {
			if (gs == null) {
				gs = component;
			} else if (component.size() > gs.size()) {
				gs = component;
			}
		}
		return gs;
	}

	private void strongConnect(Vertex vertex) {
		Vertex top;
		int index = 0;

		vertex.getProperties().put("index", index);
		vertex.getProperties().put("lowlink", index);
		index++;

		stack.push(vertex);

		Vertex last = vertex;
		while (true) {
			var adjIdx = (Integer) last.getProperties().get("adjIdx");
			if (adjIdx < last.getOutAdjacents().size()) {
				var w = last.getOutAdjacents().get(adjIdx);
				last.getProperties().put("adjIdx", ++adjIdx);
				if (((Integer) w.getProperties().get("index")) == -1) {
					w.getProperties().put("caller", last);

					w.getProperties().put("index", index);
					w.getProperties().put("lowlink", index);
					index++;
					stack.push(w);
					last = w;
				} else if (stack.contains(w)) {
					last.getProperties().put("lowlink",
							Math.min(((Integer) last.getProperties().get("lowlink")), ((Integer) w.getProperties().get("index"))));
				}
			} else {
				if (last.getProperties().get("lowlink").equals(last.getProperties().get("index"))) {
					var vertices = new ArrayList<Vertex>();
					top = stack.pop();
					vertices.add(top);

					while (!top.equals(last)) {
						top = stack.pop();
						vertices.add(top);
					}
					scc.add(vertices);
				}
				Vertex newLast = (Vertex) last.getProperties().get("caller");
				if (newLast != null) {
					newLast.getProperties().put("lowlink",
							Math.min(((Integer)newLast.getProperties().get("lowlink")), ((Integer) last.getProperties().get("lowlink"))));
					last = newLast;
				} else {
					break; //while
				}
			}
		}//while
	}
}
