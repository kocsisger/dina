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
import hu.unideb.inf.dina.v2.model.TrinodeJunction;
import hu.unideb.inf.dina.v2.utils.DescriptionReader;

public class TrinodeJunctions implements GraphAnalyzer {
    private static final String TRINODE_JUNCTIONS_COUNT_TOTAL = "Total junctions count";

    private static final String TRINODE_JUNCTIONS_COUNT_TYPE_PREFIX = "Junctions count type #";

    @Override
    public Collection<AnalysisResult<?>> analyze(Graph graph) {
        MapResult mapResult = new MapResult();
        FileResult fileResult = new FileResult();
        Set<TrinodeJunction> junctions = new HashSet<>();

        for (Vertex vertex : graph.getGraph().values()) {
            var adjs = vertex.getAllAdjacents();

            for (Vertex iVertex : adjs) {
                var iAdjs = iVertex.getAllAdjacents();
                for (int j = 0; j < iAdjs.size(); j++) {
                    var jVertex = iAdjs.get(j);
                    for (int k = j + 1; k < iAdjs.size(); k++) {
                        var kVertex = iVertex.getAllAdjacents().get(k);
                        junctions.add(new TrinodeJunction(iVertex, jVertex, kVertex));
                    }
                }
            }
        }

        junctions.forEach(TrinodeJunction::calculateType);

        for (int i = 0; i < TrinodeJunction.TrinodeJunctionType.values().length; i++) {
            int type = i;
            long count = junctions.stream()
                    .filter(junction -> junction.getType().ordinal() == type)
                    .count();
            mapResult.getResult().put(TRINODE_JUNCTIONS_COUNT_TYPE_PREFIX + (i + 1), Long.toString(count));
            fileResult.addLine(join(";", Integer.toString(i + 1), Long.toString(count)));
        }

        mapResult.getResult().put(TRINODE_JUNCTIONS_COUNT_TOTAL, Integer.toString(junctions.size()));
        fileResult.setFileName("trinode_junctions");

        return List.of(mapResult, fileResult);
    }

    @Override
    public String getName() {
        return "Trinode junctions";
    }

    @Override
    public String getDescription() {
        return Objects.requireNonNull(DescriptionReader.readDescriptionFromFile("trinode-junctions.html"));
    }
}
