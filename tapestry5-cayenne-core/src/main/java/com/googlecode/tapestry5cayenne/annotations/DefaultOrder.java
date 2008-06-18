package com.googlecode.tapestry5cayenne.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the default sort order for a class.
 * Used when providing lists of objects, such as that provided by PersistentEntitySelectModel.
 * @author robertz
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultOrder {
    /**
     * @return the names of the properties by which to order. The properties must correspond to cayenne-mapped properties.
     */
    String[] orderings();
    /**
     * Allows more control over the ordering by specifying the sort direction (true => ascending; false => descending).
     * This can either be a single value, representing the sort order for every specified property,
     * or it can be an array of the same length as orderings.  An array of any other length will result
     * in a runtime exception.
     * @return An array of booleans representing whether the corresponding orderings should be ascending or descending.
     */
    boolean[] ascending() default {true};
}
