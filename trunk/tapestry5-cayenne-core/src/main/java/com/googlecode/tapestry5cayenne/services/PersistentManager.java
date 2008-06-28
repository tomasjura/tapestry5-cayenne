package com.googlecode.tapestry5cayenne.services;

import java.util.List;

import org.apache.cayenne.exp.Expression;
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
    
    /**
     * Returns a list of objects of type <T> that match the provided expression.
     * The list is ordered as for listAll.
     * @param type The type of object to return
     * @param qualifier  the expression used to match the arguments
     * @param orderings (optional) the order in which the objects should be returned.
     */
    <T> List<T> listMatching(Class<T> type, Expression qualifier, Ordering... orderings);
}
