package org.citeplag.node;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This Object represents node of a mathematic expression tree (MAT).
 * If you start with this object, it is usually the root of a MAT.
 * Each child a branch.
 *
 * @author Vincent Stange
 */
public class MathNode {

    private String name = null;

    private String id = null;

    private String value = "";

    private Map<String, String> attributes = new HashMap<>();

    private MathNode operator = null;

    // Is the order of children nodes
    private boolean orderSensitive = true;

    private ArrayList<MathNode> children = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s:%s", name, value) + (operator != null ? "-" + operator.toString() : "");
    }

    public void setAttributes(NamedNodeMap attributes) {
        if (attributes == null)
            return;
        int numAttrs = attributes.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Node attr = attributes.item(i);
            String attrName = attr.getNodeName();
            if ("id".equals(attrName))
                setId(attr.getNodeValue());
            this.attributes.put(attrName, attr.getNodeValue());
        }
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MathNode getOperator() {
        return operator;
    }

    public void setOperator(MathNode operator) {
        this.operator = operator;
        orderSensitive = !(operator.getName().equals("times") || operator.getName().equals("plus"));
    }

    public boolean isOperation() {
        return operator != null;
    }

    public boolean isOrderSensitive() {
        return orderSensitive;
    }

    public ArrayList<MathNode> getChildren() {
        return children;
    }

    public void addChild(MathNode child) {
        this.children.add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MathNode mathNode = (MathNode) o;

        if (name != null ? !name.equals(mathNode.name) : mathNode.name != null) return false;
        if (!value.equals(mathNode.value)) return false;
        return operator != null ? operator.equals(mathNode.operator) : mathNode.operator == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value.hashCode();
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        return result;
    }

    public void print(String indent) {
        System.out.println(indent + this.toString());
        for (MathNode child : children) {
            child.print(indent + "  ");
        }
    }

}
