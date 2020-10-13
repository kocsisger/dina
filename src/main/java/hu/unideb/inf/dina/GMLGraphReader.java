package hu.unideb.inf.dina;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import hu.unideb.inf.dina.commons.exception.GraphReaderException;
import hu.unideb.inf.dina.commons.model.Graph;
import hu.unideb.inf.dina.commons.model.Vertex;
import hu.unideb.inf.dina.commons.reader.FileGraphReader;

/**
 *
 * @author Gergely Kocsis
 */
public class GMLGraphReader extends FileGraphReader {

    /* public static void main(String args[]) throws IOException {
        GraphReader gReader = new GMLGraphReader();
        Graph g1 = new Graph();
        Graph g2 = new Graph();
        gReader.readGraphFromFile(new File("simple.gml"), g1);
        System.out.println("g1: " + g1.getVertices().size());
        System.out.println(g1.getEdges());
        gReader = new GMLGraphReader();
        gReader.readGraphFromFile(new File("complex.gml"), g2);
        System.out.println("g2: " + g2.getVertices().size());
        System.out.println(g2.getEdges());
    }*/

    private Graph g;
    private int id = -1;
    private Map<String, Integer> idMapper = new HashMap<>();

    @Override
    public void readGraphFromInputStream(InputStream inputStream, Graph graph) throws GraphReaderException {
        graph.getGraph().clear();
        g = graph;
        long vertexNum;

        try (Scanner sc = new Scanner(inputStream);) {
            String nextWord;
            String rx = "[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\"";
            while (sc.hasNext()) {
                nextWord = sc.findWithinHorizon(rx, 0);

                switch (nextWord.toLowerCase()) {
                    case "graph":
                        if (!"[".equals(sc.findWithinHorizon(rx, 0))) {
                            throw new GraphReaderException("GML Syntax error at keyword \"graph\". \"[\" is missing.");
                        }
                        processGraphElement(sc, rx);
                        break;
                    case "creator ":
                        sc.findWithinHorizon(rx, 0);
                        break; //skip - not implemented yet
                    case "version":
                        sc.findWithinHorizon(rx, 0);
                        break;  //skip - not implemented yet
                    default:
                        throw new GraphReaderException("Not supported GML keyword: " + nextWord);
                }
            }
        }
    }

    private void processGraphElement(Scanner sc, String rx) throws GraphReaderException {
        String nextWord;
        while (sc.hasNext()) {
            nextWord = sc.findWithinHorizon(rx, 0);

            switch (nextWord.toLowerCase()) {
                case "node":
                    if (!"[".equals(sc.findWithinHorizon(rx, 0))) {
                        throw new GraphReaderException("GML Syntax error at keyword \"node\". \"[\" is missing.");
                    }
                    procesNodeElement(sc, rx);
                    break;
                case "edge":
                    if (!"[".equals(sc.findWithinHorizon(rx, 0))) {
                        throw new GraphReaderException("GML Syntax error at keyword \"edge\". \"[\" is missing.");
                    }
                    processEdgeElement(sc, rx);
                    break;
                case "comment":
                case "directed":
                case "id":
                case "label":
                case "hierarchic":
                    sc.findWithinHorizon(rx, 0);
                    break;  //skip - not implemented yet
                case "]":
                    return;
                default:
                    throw new GraphReaderException("Not supported GML keyword: " + nextWord);
            }
        }
    }

    private boolean stringId = false;
    private boolean intId = false;

    private void procesNodeElement(Scanner sc, String rx) throws GraphReaderException {
        Vertex newNode = null;
        String label = "";
        String nextWord;
        String gmlId;
        while (sc.hasNext()) {
            nextWord = sc.findWithinHorizon(rx, 0);

            switch (nextWord.toLowerCase()) {
                case "id":
                    gmlId = sc.findWithinHorizon(rx, 0);
                    try {
                        id = Integer.parseInt(gmlId);
                        intId=true;
                        if (stringId) {
                            throw new GraphReaderException("GML syntax error: All IDs have to be either String or Integer. Mixed types are not supported. Use \"1\", \"2\"... ");
                        }
                    } catch (NumberFormatException ex) {
                        id++;
                        stringId = true;
                        if (intId) {
                            throw new GraphReaderException("GML syntax error: All IDs have to be either String or Integer. Mixed types are not supported. Use \"1\", \"2\"... ");
                        }
                    }
                    newNode = new Vertex(id);
                    newNode.getProperties().put("gml.id", gmlId);
                    idMapper.put((String) newNode.getProperties().get("gml.id"), id);
                    newNode.getProperties().put("gml.label", label);
                    g.add(newNode);
                    break;
                case "label":
                    label = sc.findWithinHorizon(rx, 0);
                    if (newNode != null) {
                        newNode.getProperties().put("gml.label", label);
                    }
                    break;
                case "]":
                    return;
                case "thisisasampleattribute":
                    sc.findWithinHorizon(rx, 0);
                    break;  //skip - not implemented yet
                default:
                    throw new GraphReaderException("Not supported GML keyword: " + nextWord);
            }
        }//while
    }

    private void processEdgeElement(Scanner sc, String rx) throws GraphReaderException {
        String nextWord;
        String source = null, target = null;
        while (sc.hasNext()) {
            nextWord = sc.findWithinHorizon(rx, 0);

            switch (nextWord.toLowerCase()) {
                case "source":
                    if (source != null) {
                        throw new GraphReaderException("GML syntax error when reading " + nextWord + " (duplicated source: " + source + ")");
                    }
                    source = sc.findWithinHorizon(rx, 0);
                    if (target != null) {
                        Vertex sourceVertex = g.getGraph().get(idMapper.get(source));
                        Vertex targetVertex = g.getGraph().get(idMapper.get(target));
                        sourceVertex.getOutAdjacents().add(targetVertex);
                        targetVertex.getInAdjacents().add(sourceVertex);
                    }
                    break;
                case "target":
                    if (target != null) {
                        throw new GraphReaderException("GML syntax error when reading " + nextWord + " (duplicated source: " + source + ")");
                    }
                    target = sc.findWithinHorizon(rx, 0);
                    if (source != null) {
                        Vertex sourceVertex = g.getGraph().get(idMapper.get(source));
                        Vertex targetVertex = g.getGraph().get(idMapper.get(target));
                        sourceVertex.getOutAdjacents().add(targetVertex);
                        targetVertex.getInAdjacents().add(sourceVertex);
                    }
                    break;
                case "label":
                    sc.findWithinHorizon(rx, 0); //edge labels are not supported yet
                    break;
                case "thisisasampleattribute":
                    sc.findWithinHorizon(rx, 0);
                    break;  //skip - not implemented yet
                case "]":
                    return;
                default:
                    throw new GraphReaderException("Not supported GML keyword: " + nextWord);
            }
        }//while
    }

}
