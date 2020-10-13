package hu.unideb.inf.dina.v2;

import hu.unideb.inf.dina.VertexPairGraphReader;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.v2.algorithms.QuadnodeJunctions;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
//        var graph = new Graph();
//        new VertexPairGraphReader().readGraphFromFile(
//                Paths.get("DiNA/test-graphs/quadnodes/quadnode15.csv").toFile(),
//                graph
//        );
//
//        var result = new QuadnodeJunctions().analyze(graph);
//        result.forEach(System.out::println);

        var adapter = new GraphAnalyzerAdapter();
        adapter.init(ClassLoader.getSystemClassLoader());
//        System.out.println(adapter.getGraphAnalyzer(adapter.getGraphAnalyzers().get(0).getClass().getName()).getDescription());
        System.out.println(adapter.getAllTypes());
    }
}
