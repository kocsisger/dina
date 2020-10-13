package hu.unideb.inf.dina.v2.model;

import java.util.Collections;
import java.util.List;

import hu.unideb.inf.dina.commons.model.Vertex;

public class QuadnodeJunction {

	public static final Integer NUMBER_OF_TYPES = 15;

	private final Vertex vertexA;

	private final Vertex vertexB;

	private final Vertex vertexC;

	private final Vertex vertexD;

	private int type;

	public QuadnodeJunction(Vertex vertexA, Vertex vertexB, Vertex vertexC, Vertex vertexD) {
		this.vertexA = vertexA;
		this.vertexB = vertexB;
		this.vertexC = vertexC;
		this.vertexD = vertexD;
	}

	public int getType() {
		return type;
	}

	public void calculateType() {
		EdgeType aToB = EdgeType.fromVertices(vertexA, vertexB);
		EdgeType bToC = EdgeType.fromVertices(vertexB, vertexC);
		EdgeType cToD = EdgeType.fromVertices(vertexC, vertexD);

		if (aToB == EdgeType.INOUT
				&& bToC == EdgeType.INOUT
				&& cToD == EdgeType.INOUT) {
			this.type = 0;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.INOUT && cToD == EdgeType.OUT)
				|| (aToB == EdgeType.IN && bToC == EdgeType.INOUT && cToD == EdgeType.INOUT)
		) {
			this.type = 1;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.INOUT && cToD == EdgeType.IN)
						|| (aToB == EdgeType.OUT && bToC == EdgeType.INOUT && cToD == EdgeType.INOUT)
		) {
			this.type = 2;
		} else if (aToB == EdgeType.INOUT && bToC != EdgeType.INOUT && cToD == EdgeType.INOUT) {
			this.type = 3;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.OUT && cToD == EdgeType.OUT)
						|| (aToB == EdgeType.IN && bToC == EdgeType.IN && cToD == EdgeType.INOUT)
		) {
			this.type = 4;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.OUT && cToD == EdgeType.IN)
						|| (aToB == EdgeType.OUT && bToC == EdgeType.IN && cToD == EdgeType.INOUT)
		) {
			this.type = 5;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.IN && cToD == EdgeType.IN)
						|| (aToB == EdgeType.OUT && bToC == EdgeType.OUT && cToD == EdgeType.INOUT)
		) {
			this.type = 6;
		} else if (
				(aToB == EdgeType.INOUT && bToC == EdgeType.IN && cToD == EdgeType.OUT)
						|| (aToB == EdgeType.IN && bToC == EdgeType.OUT && cToD == EdgeType.INOUT)
		) {
			this.type = 7;
		} else if (bToC == EdgeType.INOUT && aToB == cToD && aToB != EdgeType.INOUT) {
			this.type = 8;
		} else if (aToB == EdgeType.OUT && bToC == EdgeType.INOUT && cToD == EdgeType.IN) {
			this.type = 9;
		} else if (aToB == EdgeType.IN && bToC == EdgeType.INOUT && cToD == EdgeType.OUT) {
			this.type = 10;
		} else if (aToB == bToC && bToC == cToD && aToB != EdgeType.INOUT) {
			this.type = 11;
		} else if (
				(aToB == EdgeType.OUT && bToC == EdgeType.OUT && cToD == EdgeType.IN)
						|| (aToB == EdgeType.OUT && bToC == EdgeType.IN && cToD == EdgeType.IN)
		) {
			this.type = 12;
		} else if (
				(aToB == EdgeType.IN && bToC == EdgeType.IN && cToD == EdgeType.OUT)
						|| (aToB == EdgeType.IN && bToC == EdgeType.OUT && cToD == EdgeType.OUT)
		) {
			this.type = 13;
		} else if (
				(aToB == EdgeType.OUT && bToC == EdgeType.IN && cToD == EdgeType.OUT)
						|| (aToB == EdgeType.IN && bToC == EdgeType.OUT && cToD == EdgeType.IN)
		) {
			this.type = 14;
		}
	}

	@Override
	public int hashCode() {
		return vertexA.getId() & vertexB.getId() & vertexC.getId() & vertexD.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof QuadnodeJunction)) {
			return false;
		}

		var objTrinode = (QuadnodeJunction) obj;
		var thisVertices = new java.util.ArrayList<>(List.of(vertexA, vertexB, vertexC, vertexD));
		var objVertices = new java.util.ArrayList<>(List.of(objTrinode.vertexA, objTrinode.vertexB, objTrinode.vertexC, objTrinode.vertexD));
		Collections.sort(thisVertices);
		Collections.sort(objVertices);
		return thisVertices.equals(objVertices);
	}

	@Override
	public String toString() {
		var vertices = new java.util.ArrayList<>(List.of(vertexA, vertexB, vertexC, vertexD));
		Collections.sort(vertices);
		return String.format("%d (%d, %d, %d, %d)", this.type + 1,
				vertices.get(0).getId(),
				vertices.get(1).getId(),
				vertices.get(2).getId(),
				vertices.get(3).getId());
	}
}
