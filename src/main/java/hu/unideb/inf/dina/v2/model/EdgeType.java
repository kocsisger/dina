package hu.unideb.inf.dina.v2.model;

import hu.unideb.inf.dina.commons.model.Vertex;

public enum EdgeType {
    IN, OUT, INOUT;

    static EdgeType fromVertices(Vertex a, Vertex b) {
        if (a.getOutAdjacents().contains(b)) {
            if (b.getOutAdjacents().contains(a)) {
                return INOUT;
            } else {
                return OUT;
            }
        } else {
            return IN;
        }
    }
}
