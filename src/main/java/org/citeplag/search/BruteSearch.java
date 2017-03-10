package org.citeplag.search;

import org.citeplag.match.Similarity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Stange
 */
public class BruteSearch {

    public List<Similarity> getSimilarities(MathNode aTree, MathNode bTree) {
        List<Similarity> similarities = new ArrayList<>();
        findSimilarities(aTree, bTree, similarities, false);
        return similarities;
    }

    boolean findSimilarities(MathNode aTree, MathNode bTree, List<Similarity> similarities, boolean holdA) {
        if (isEqualTree(aTree, bTree)) {
            similarities.add(new Similarity(aTree.getId(), bTree.getId()));
            return true;
        }
        for (MathNode bTreeChild : bTree.children) {
            if (findSimilarities(aTree, bTreeChild, similarities, true)) {
                return true;
            }
        }
        if (!holdA) {
            for (MathNode aTreeChild : aTree.children) {
                findSimilarities(aTreeChild, bTree, similarities, false);
            }
        }
        return false;
    }

    boolean isEqualTree(MathNode aTree, MathNode bTree) {
        if (aTree.equals(bTree) && aTree.children.size() == bTree.children.size()) {
            for(int i = 0; i < aTree.children.size(); i++) {
                if (!isEqualTree(aTree.children.get(i), bTree.children.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
