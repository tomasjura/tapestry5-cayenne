/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import java.io.IOException;

import org.apache.cayenne.access.DataContext;
import org.apache.tapestry.services.ApplicationStateManager;
import org.apache.tapestry.services.Request;
import org.apache.tapestry.services.RequestFilter;
import org.apache.tapestry.services.RequestHandler;
import org.apache.tapestry.services.Response;

/**
 * Provides a RequestFilter which ensures that there is a DataContext associated with the current request.
 * Currently uses a session-based strategy.
 * @author robertz
 *
 */
public class CayenneRequestFilter implements RequestFilter {
    
    private final ApplicationStateManager _asm;
    
    public CayenneRequestFilter(final ApplicationStateManager asm) {
        _asm = asm;
    }

    public boolean service(Request request, Response response, RequestHandler handler)
            throws IOException {
        DataContext dc;
        if (_asm.exists(DataContext.class)) {
            dc = _asm.get(DataContext.class);
        } else {
            dc = DataContext.createDataContext();
            _asm.set(DataContext.class, dc);
        }
        DataContext.bindThreadDataContext(dc);
        try {
            return handler.service(request, response);
        } finally {
            DataContext.bindThreadDataContext(null);
        }
    }

}
