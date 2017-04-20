package org.citeplag.search;

import org.citeplag.match.Match;
import org.citeplag.match.Similarity;
import org.citeplag.node.MathNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple approach to find similar subtrees between two math expression trees.
 *
 * @author Vincent Stange
 */
public class SubTreeComparison {

    private final Match.Type type;

    public SubTreeComparison(String type) {
        this.type = Match.Type.valueOf(type);
    }

    /**
     * @param aTree         First MathNode Tree (reference source)
     * @param bTree         Second MathNode Tree (comparison)
     * @param onlyOperators find similarities only between operations, no single identifier (end leafs) are checked
     * @return list of similarities, never null
     */
    public List<Similarity> getSimilarities(MathNode aTree, MathNode bTree, boolean onlyOperators) {
        List<Similarity> similarities = new ArrayList<>();
        findSimilarities(aTree, bTree, similarities, false, onlyOperators);
        return similarities;
    }

    /**
     * Recursive method that goes along every node of the reference tree.
     *
     * @param refTree         Reference MathNode tree
     * @param comTree         Comparison MathNode tree
     * @param similarities  List of similarities, will be filled during process.
     * @param holdA         extra flag
     * @param onlyOperators find similarities only between operations, no single identifier (end leafs) are checked
     * @return if the current aTree ad bTree are identical subtrees
     */
    boolean findSimilarities(MathNode refTree, MathNode comTree, List<Similarity> similarities, boolean holdA, boolean onlyOperators) {
        if (isIdenticalTree(refTree, comTree)) {
            comTree.setMarked(true);
            similarities.add(new Similarity(refTree.getId(), comTree.getId(), type));
            return true;
        }

        for (MathNode bTreeChild : comTree.getChildren()) {
            if ((onlyOperators && bTreeChild.isLeaf())) {
                continue;
            }
            if (findSimilarities(refTree, bTreeChild, similarities, true, onlyOperators)) {
                return true;
            }
        }
        if (!holdA) {
            for (MathNode aTreeChild : refTree.getChildren()) {
                if (onlyOperators && aTreeChild.isLeaf())
                    continue;

                findSimilarities(aTreeChild, comTree, similarities, false, onlyOperators);
            }
        }
        return false;
    }

    /**
     * Are aTree and bTree identical subtrees? If the root node is equal,
     * all subsequent children will be compared.
     *
     * @param aTree first MathNode tree
     * @param bTree second MathNode tree
     * @return true - if both trees are identical subtrees, false otherwise
     */
    boolean isIdenticalTree(MathNode aTree, MathNode bTree) {
        // first check if they have the same number of children
        if (aTree.equals(bTree) && aTree.getChildren().size() == bTree.getChildren().size()) {
            if (aTree.isOrderSensitive()) {
                // all children order sensitive
                for (int i = 0; i < aTree.getChildren().size(); i++) {
                    if (!isIdenticalTree(aTree.getChildren().get(i), bTree.getChildren().get(i))) {
                        return false;
                    }
                }
            } else {
                // order insensitive
                List<MathNode> bChildren = new ArrayList<>(bTree.getChildren());
                OUTER:
                for (MathNode aChild : aTree.getChildren()) {
                    for (MathNode bChild : getSimilarChilds(aChild, bChildren)) {
                        if (isIdenticalTree(aChild, bChild)) {
                            // found an identical child
                            bChildren.remove(bChild);
                            continue OUTER;
                        }
                    }
                    // aChild is missing in bChildren
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Filter for similar nodes from a list. Only the node itself will be compared.
     * We will not look at the children.
     *
     * @param searchNode node we want to find.
     * @param list       list we want to search.
     * @return new list with the same node we search for.
     */
    List<MathNode> getSimilarChilds(MathNode searchNode, List<MathNode> list) {
        return list.stream().filter(searchNode::equals).collect(Collectors.toList());
    }

}
