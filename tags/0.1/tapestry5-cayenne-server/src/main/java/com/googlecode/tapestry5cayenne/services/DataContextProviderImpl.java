/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;

import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

/**
 * Implementation of provider for DataContext.
 *
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
