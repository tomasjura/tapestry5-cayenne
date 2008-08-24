package com.googlecode.tapestry5cayenne;

/**
 * Enum for use in conjunction with the OCType annotation for injecting
 * object context's into pages, components, and services.
 * @author robertz
 *
 */
public enum ContextType {
    
    /**
     * Causes injection of a new object context
     */
    NEW,
    /**
     * Causes injection of the "current" context
     */
    CURRENT,
    /**
     * Causes injection of a child context of the current 
     * context.  Note that if your ObjectContext implementation does not
     * support nested context (CayenneContext does not), use of this 
     * value will result in a runtime exception.
     */
    CHILD

}
