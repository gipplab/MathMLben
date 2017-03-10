package org.citeplag.match;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Stange
 */
public class Similarity {

    public String id = "";

    public List<Match> matches = new ArrayList<>();

    public Similarity(String id, String idMatch) {
        this.id = id;
        matches.add(new Match(idMatch));
    }

    public Match addMatch(Match match) {
        this.matches.add(match);
        return match;
    }

}
