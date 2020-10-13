package hu.unideb.inf.dina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import hu.unideb.inf.dina.commons.exception.GraphReaderException;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.model.Vertex;
import hu.unideb.inf.dina.commons.reader.FileGraphReader;

/**
 *
 * @author Gergely Kocsis
 */
public class VertexPairGraphReader extends FileGraphReader {

    private boolean allVerticesPreAdded;

    @Override
    public void readGraphFromInputStream(InputStream inputStream, Graph graph) throws GraphReaderException {
        graph.getGraph().clear();
        long vertexNum;
        allVerticesPreAdded = false;

        try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(isr);) {

            String firstLine = in.readLine();

            try {//Test if first line is number of vertices
                vertexNum = Integer.parseInt(firstLine);
                //if no NumberFormatException, then it is only one int holding the number of vertices
                for (int i = 0; i < vertexNum; i++) {
                    graph.add(new Vertex(i));
                }
                allVerticesPreAdded = true;
            } catch (NumberFormatException e) {
                processLine(firstLine, graph);
            }

            String line;
            while ((line = in.readLine()) != null) {
                processLine(line, graph);
            }

        } catch (IOException ex) {
            Logger.getLogger(VertexPairGraphReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new GraphReaderException("Invalid file format");
        }
    }

    private void processLine(String line, Graph g) throws GraphReaderException {
        int from;
        int to;
        try (Scanner ssc = new Scanner(line)) {
            ssc.useDelimiter("[\\s\\n,;]");
            from = ssc.nextInt();
           // System.out.print("<" + from);
            to = ssc.nextInt();
           // System.out.println(" - " + to + ">");
            if (!allVerticesPreAdded) {
                if (g.getGraph().get(from) == null) {
                    g.add(new Vertex(from));
                }
                if (g.getGraph().get(to) == null) {
                    g.add(new Vertex(to));
                }
            }
        }
        int vertexCount = g.getGraph().values().size();
        if (from >= vertexCount || to >= vertexCount) {
            throw new GraphReaderException("Invalid edge found between the following nodes: (" + from + ", " + to + ")");
        }
        Vertex fromGraph = g.getGraph().get(from);
        Vertex toGraph = g.getGraph().get(to);
        fromGraph.getOutAdjacents().add(toGraph);
        toGraph.getInAdjacents().add(fromGraph);
    }
}
