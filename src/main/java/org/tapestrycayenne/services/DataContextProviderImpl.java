/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;

/**
 * Default implementaiton of ObjectContextProvider
 * @author Robert Zeigler
 * @version 1.0
 */
public class DataContextProviderImpl implements ObjectContextProvider {

    public ObjectContext currentContext() {
        return DataContext.getThreadDataContext();
    }

    public ObjectContext newContext() {
        return DataContext.createDataContext();
    }
}
