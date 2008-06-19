package com.googlecode.tapestry5cayenne.services;

import java.util.List;

import org.apache.cayenne.query.Ordering;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;

/**
 * PersistentManager is a service for managing basic, generic operations on data objects,
 * such as fetching an ordered list of objects of a particular type.
 * @author robertz
 *
 */

public interface PersistentManager {
    
    /**
     * Returns a list of all objects of type <T>.  
     * The list is ordered by the first rule that matches in the following list:
     *  1) passed in orderings, if any
     *  2) the {@link DefaultOrder} annotation, if present
     *  3) the ordering provided by a method marked with the {@link Label} annotation, if present
     *  4) The "natural" ordering of the objects, if they implement Comparable
     *  5) No particular ordering if all of the above are false
     * @param <T>
     * @param type 
     * @param orderings
     * @return
     */
    <T> List<T> listAll(Class<T> type, Ordering...orderings);

}
