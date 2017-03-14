package org.citeplag.search;

import org.citeplag.match.Match;
import org.citeplag.match.Similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vincent Stange
 */
public class BruteSearch {

    public List<Similarity> getSimilarities(MathNode aTree, MathNode bTree) {
        List<Similarity> similarities = new ArrayList<>();
        findSimilarities(aTree, bTree, similarities, false);
        return similarities;
    }

    /**
     * Recursive method that goes along every node of the first tree.
     *
     * @param aTree First MathNode Tree
     * @param bTree Second MathNode Tree
     * @param similarities List of similarities, will be filled during process.
     * @param holdA
     * @return
     */
    boolean findSimilarities(MathNode aTree, MathNode bTree, List<Similarity> similarities, boolean holdA) {
        if (isIdenticalTree(aTree, bTree)) {
            similarities.add(new Similarity(aTree.getId(), bTree.getId(), Match.Type.identical));
            return true;
        }
        for (MathNode bTreeChild : bTree.getChildren()) {
            if (findSimilarities(aTree, bTreeChild, similarities, true)) {
                return true;
            }
        }
        if (!holdA) {
            for (MathNode aTreeChild : aTree.getChildren()) {
                findSimilarities(aTreeChild, bTree, similarities, false);
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

    List<MathNode> getSimilarChilds(MathNode searchNode, List<MathNode> list) {
        return list.stream().filter(searchNode::equals).collect(Collectors.toList());
    }

}
