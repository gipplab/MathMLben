package org.citeplag.match;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Stange
 */
public class Similarity {

    public String id = "";

    public List<Match> matches = new ArrayList<>();

    public Similarity(String id, String idMatch, Match.Type type) {
        this.id = id;
        // initialize with the first match
        matches.add(new Match(idMatch, type));
    }

    public Match addMatch(Match match) {
        this.matches.add(match);
        return match;
    }

}
