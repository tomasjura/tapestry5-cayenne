/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne.integration.app0.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry.ioc.annotation.Inject;
import org.apache.tapestry.annotation.ApplicationState;
import org.tapestrycayenne.annotations.Cayenne;
import org.tapestrycayenne.services.ObjectContextProvider;

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
