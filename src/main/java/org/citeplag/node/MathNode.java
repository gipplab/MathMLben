package org.citeplag.node;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This Object represents the node of a mathematic expression tree (MET).
 * <br/>
 * If you start with this object, it is usually the root of a MET.
 * <br/>
 * This object should not be dependent on the former document and any
 * namespace-dependent attributes should be ignored.
 *
 * @author Vincent Stange
 */
public class MathNode {

    /**
     * tag-name of the node (element name, e.g. <apply> = "apply")
     */
    private String name = null;

    /**
     * specific id attribute of the original node
     */
    private String id = null;

    /**
     * text value of a node
     */
    private String value = "";

    /**
     * all attributes of node
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Are children nodes order sensitive?
     */
    private boolean orderSensitive = true;

    private boolean marked = false;

    /**
     * all children in order via an ArrayList
     */
    private ArrayList<MathNode> children = new ArrayList<>();

    public void setAttributes(NamedNodeMap attributes) {
        if (attributes == null)
            return;
        // extract all attributes into a simple map
        int numAttrs = attributes.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Node attr = attributes.item(i);
            String attrName = attr.getNodeName();
            if ("id".equals(attrName)) {
                setId(attr.getNodeValue());
            }
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

    public boolean isLeaf() {
        return children.isEmpty();
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
        return value != null ? value.equals(mathNode.value) : mathNode.value == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, value);
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}
