package org.citeplag.match;

/**
 * JSON wrapper for a single match between two math expression trees.
 * The Id relates to the original MathML.
 *
 * @author Vincent Stange
 */
public class Match {

    public enum Type {
        similar, identical
    }

    public String id = "";

    public double assessment = 1.0;

    public String type = "similar";

    public Match(String id, Type type) {
        this.id = id;
        this.type = type.name();
    }

}
