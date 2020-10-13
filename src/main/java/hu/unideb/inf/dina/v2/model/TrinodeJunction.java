package hu.unideb.inf.dina.v2.model;

import java.util.Collections;
import java.util.List;

import hu.unideb.inf.dina.commons.model.Vertex;

public class TrinodeJunction {

    public enum TrinodeJunctionType {
        TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, TYPE_6
    }

    private final Vertex centerVertex;

    private final Vertex vertexA;

    private final Vertex vertexC;

    private TrinodeJunctionType type;

    public TrinodeJunction(Vertex centerVertex, Vertex vertexA, Vertex vertexC) {
        this.centerVertex = centerVertex;
        this.vertexA = vertexA;
        this.vertexC = vertexC;
    }

    public TrinodeJunctionType getType() {
        return type;
    }

    public void calculateType() {
        EdgeType aToB = EdgeType.fromVertices(vertexA, centerVertex);
        EdgeType bToC = EdgeType.fromVertices(centerVertex, vertexC);

        if (aToB == EdgeType.INOUT && bToC == EdgeType.INOUT) {
            this.type = TrinodeJunctionType.TYPE_1;
        } else if (
                (aToB == EdgeType.INOUT && bToC == EdgeType.OUT)
                || (aToB == EdgeType.IN && bToC == EdgeType.INOUT)
        ) {
            this.type = TrinodeJunctionType.TYPE_2;
        } else if (
                (aToB == EdgeType.INOUT && bToC == EdgeType.IN)
                        || (aToB == EdgeType.OUT && bToC == EdgeType.INOUT)
        ) {
            this.type = TrinodeJunctionType.TYPE_3;
        } else if (
                (aToB == EdgeType.OUT && bToC == EdgeType.OUT)
                        || (aToB == EdgeType.IN && bToC == EdgeType.IN)
        ) {
            this.type = TrinodeJunctionType.TYPE_4;
        } else if (aToB == EdgeType.IN && bToC == EdgeType.OUT) {
            this.type = TrinodeJunctionType.TYPE_5;
        } else {
            this.type = TrinodeJunctionType.TYPE_6;
        }
    }

    @Override
    public int hashCode() {
        return vertexA.getId() & centerVertex.getId() & vertexC.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TrinodeJunction)) {
            return false;
        }

        var objTrinode = (TrinodeJunction) obj;
        var thisVertices = new java.util.ArrayList<>(List.of(vertexA, centerVertex, vertexC));
        var objVertices = new java.util.ArrayList<>(List.of(objTrinode.vertexA, objTrinode.centerVertex, objTrinode.vertexC));
        Collections.sort(thisVertices);
        Collections.sort(objVertices);
        return thisVertices.equals(objVertices);
    }

    @Override
    public String toString() {
        var vertices = new java.util.ArrayList<>(List.of(vertexA, centerVertex, vertexC));
        Collections.sort(vertices);
        return String.format("%d (%d, %d, %d)", this.type.ordinal() + 1, vertices.get(0).getId(), vertices.get(1).getId(),
                vertices.get(2).getId());
    }
}
