package hu.unideb.inf.dina.v2.model;

import java.util.Collections;
import java.util.List;

import hu.unideb.inf.dina.commons.model.Vertex;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Triangle {
	public enum TriangleType {
		TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6, TYPE_7
	}

	private final Vertex vertexA;

	private final Vertex vertexB;

	private final Vertex vertexC;

	private TriangleType type;

	public Triangle(Vertex vertexA, Vertex vertexB, Vertex vertexC) {
		this.vertexA = vertexA;
		this.vertexB = vertexB;
		this.vertexC = vertexC;
	}

	public TriangleType getType() {
		return type;
	}

	public void calculateType() {
		EdgeType aToB = EdgeType.fromVertices(vertexA, vertexB);
		EdgeType bToC = EdgeType.fromVertices(vertexB, vertexC);
		EdgeType cToA = EdgeType.fromVertices(vertexC, vertexA);

		if (aToB == EdgeType.INOUT && bToC == EdgeType.INOUT && cToA == EdgeType.INOUT) {
			// All edges are in-out
			this.type = TriangleType.TYPE_1;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.INOUT)
						|| (aToB == EdgeType.INOUT && cToA == EdgeType.INOUT)
						|| (bToC == EdgeType.INOUT && cToA == EdgeType.INOUT)
		) {
			// Two of Three edges are in-out
			this.type = TriangleType.TYPE_2;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.OUT && cToA == EdgeType.IN)
						|| (bToC == EdgeType.INOUT && cToA == EdgeType.OUT && aToB == EdgeType.IN)
						|| (cToA == EdgeType.INOUT && aToB == EdgeType.OUT && bToC == EdgeType.IN)
		) {
			// One of Three edges are in-out, the other Two points to One Vertex
			this.type = TriangleType.TYPE_3;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.IN && cToA == EdgeType.OUT)
						|| (bToC == EdgeType.INOUT && cToA == EdgeType.IN && aToB == EdgeType.OUT)
						|| (cToA == EdgeType.INOUT && aToB == EdgeType.IN && bToC == EdgeType.OUT)
		) {
			// One of Three edges are in-out, the other Two points away from One Vertex
			this.type = TriangleType.TYPE_4;
		} else if (
				(aToB == EdgeType.INOUT && bToC == cToA)
						|| (bToC == EdgeType.INOUT && cToA == aToB)
						|| (cToA == EdgeType.INOUT && aToB == bToC)
		) {
			// One of Three edges are in-out, the other Two edges are pointing into different directions
			this.type = TriangleType.TYPE_5;
		} else if (aToB == bToC && bToC == cToA) {
			// All edges go in the same direction (in or out), like a circle
			this.type = TriangleType.TYPE_6;
		} else {
			// No in-out edges & the edges don't go in the same direction
			this.type = TriangleType.TYPE_7;
		}
	}

	@Override
	public int hashCode() {
		return vertexA.getId() & vertexB.getId() & vertexC.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Triangle)) {
			return false;
		}
		var objTriangle = (Triangle) obj;
		var thisVertices = new java.util.ArrayList<>(List.of(vertexA, vertexB, vertexC));
		var objVertices = new java.util.ArrayList<>(List.of(objTriangle.vertexA, objTriangle.vertexB, objTriangle.vertexC));
		Collections.sort(thisVertices);
		Collections.sort(objVertices);
		return thisVertices.equals(objVertices);
	}

	@Override
	public String toString() {
		var vertices = new java.util.ArrayList<>(List.of(vertexA, vertexB, vertexC));
		Collections.sort(vertices);
		return String.format("%d (%d, %d, %d)", this.type.ordinal() + 1, vertices.get(0).getId(), vertices.get(1).getId(),
				vertices.get(2).getId());
	}
}
