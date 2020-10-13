package hu.unideb.inf.dina;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * @author Gergely Kocsis
 */
public class SampleGraphGenerator {

	/**
	 * Generates a random graph with properties set by the parameters.
	 * The output is generates in .ssn format. (First line is the number of vertices.
	 * The next lines are describing edges in "from to" format.
	 *
	 * @param vertexNum Number of vertices
	 * @param edgeNum   number of edges
	 * @param filename  Name of the graph file
	 */
	public static void generateSample(int vertexNum, int edgeNum, String filename) throws FileNotFoundException {
		Random rand = new Random();

		try (PrintWriter pw = new PrintWriter(filename)) {
			pw.println(vertexNum);
			for (int i = 0; i < edgeNum; i++) {
				pw.println(Math.abs(rand.nextInt() % vertexNum) + " " + Math.abs(rand.nextInt() % vertexNum));
			}
		}
	}
}
