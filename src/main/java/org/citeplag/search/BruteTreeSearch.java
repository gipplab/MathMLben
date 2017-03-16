package org.citeplag.search;

import org.citeplag.match.Match;
import org.citeplag.match.Similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vincent Stange
 */
public class BruteTreeSearch {

    final Match.Type type;

    public BruteTreeSearch(String type) {
        this.type = Match.Type.valueOf(type);
    }

    /**
     * @param aTree         First MathNode Tree (reference source)
     * @param bTree         Second MathNode Tree (comparison)
     * @param onlyOperators find similarities only between operations, no single identifier (end leafs) are checked
     * @return
     */
    public List<Similarity> getSimilarities(MathNode aTree, MathNode bTree, boolean onlyOperators) {
        List<Similarity> similarities = new ArrayList<>();
        findSimilarities(aTree, bTree, similarities, false, onlyOperators);
        return similarities;
    }

    /**
     * Recursive method that goes along every node of the first tree.
     *
     * @param aTree         First MathNode Tree
     * @param bTree         Second MathNode Tree
     * @param similarities  List of similarities, will be filled during process.
     * @param holdA         extra flag
     * @param onlyOperators find similarities only between operations, no single identifier (end leafs) are checked
     * @return
     */
    boolean findSimilarities(MathNode aTree, MathNode bTree, List<Similarity> similarities, boolean holdA, boolean onlyOperators) {
        if (isIdenticalTree(aTree, bTree)) {
            similarities.add(new Similarity(aTree.getId(), bTree.getId(), type));
            return true;
        }

        for (MathNode bTreeChild : bTree.getChildren()) {
            if (onlyOperators && !bTreeChild.isOperation())
                continue;

            if (findSimilarities(aTree, bTreeChild, similarities, true, onlyOperators)) {
                return true;
            }
        }
        if (!holdA) {
            for (MathNode aTreeChild : aTree.getChildren()) {
                if (onlyOperators && !aTreeChild.isOperation())
                    continue;

                findSimilarities(aTreeChild, bTree, similarities, false, onlyOperators);
            }
        }
        return false;
    }

    boolean isIdenticalTree(MathNode aTree, MathNode bTree) {
        if (aTree.equals(bTree) && aTree.getChildren().size() == bTree.getChildren().size()) {
            if (aTree.isOrderSensitive()) {
                // order sensitive
                for (int i = 0; i < aTree.getChildren().size(); i++) {
                    if (!isIdenticalTree(aTree.getChildren().get(i), bTree.getChildren().get(i))) {
                        return false;
                    }
                }
            } else {
                List<MathNode> bChildren = new ArrayList<>(bTree.getChildren());

                // no order
                OUTER:
                for (MathNode aChild : aTree.getChildren()) {
                    for (MathNode bChild : getSimilarChilds(aChild, bChildren)) {
                        if (isIdenticalTree(aChild, bChild)) {
                            bChildren.remove(bChild);
                            continue OUTER;
                        }
                    }
                    // aChild wasnt in bChildren
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
