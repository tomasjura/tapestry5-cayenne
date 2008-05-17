package org.tapestrycayenne.model;

import org.tapestrycayenne.annotations.Label;
import org.tapestrycayenne.model.auto._Artist;



@SuppressWarnings("serial")
public class Artist extends _Artist implements Comparable<Artist>{
    
    @Label
    public String getName() {
        return super.getName();
    }
    
    public Integer getNumPaintings() {
        return getPaintingList().size();
    }
    
    public int numPaintings() {
        return getPaintingList().size();
    }

    public int compareTo(Artist o) {
        return getName().compareTo(o.getName());
    }

}
