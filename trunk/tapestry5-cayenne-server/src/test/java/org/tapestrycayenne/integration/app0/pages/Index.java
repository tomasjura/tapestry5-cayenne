/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne.integration.app0.pages;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tapestrycayenne.annotations.Cayenne;
import org.tapestrycayenne.services.ObjectContextProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Index {

    @ApplicationState
    private DataContext _context;
    
    @Inject
    @Cayenne
    private ObjectContextProvider _provider;
    
    public int getCurrentCacheSize() {
        return _context.getObjectStore().getDataRowCache().size();
    }
    
    public int getMaxCacheSize() {
        return _context.getObjectStore().getDataRowCache().maximumSize();
    }
    
    public List<ObjEntity> getObjEntities() {
        Collection<ObjEntity> c = _provider.currentContext().getEntityResolver().getObjEntities();
        return new ArrayList<ObjEntity>(c);
    }
}
