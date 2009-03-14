/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import java.io.IOException;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;

/**
 * Provides a RequestFilter which ensures that there is a DataContext associated with the current request.
 * Currently uses a session-based strategy.
 * @author robertz
 */
public class CayenneRequestFilter implements RequestFilter {
    
    private final ApplicationStateManager _asm;
    private final ObjectContextProvider _provider;
    
    public CayenneRequestFilter(
            final ApplicationStateManager asm,
            final ObjectContextProvider provider) {
        _asm = asm;
        _provider = provider;
    }

    public boolean service(Request request, Response response, RequestHandler handler)
            throws IOException {
        ObjectContext oc;
        if (_asm.exists(ObjectContext.class)) {
            oc = _asm.get(ObjectContext.class);
        } else {
            oc = _provider.newContext();
            _asm.set(ObjectContext.class, oc);
        }

        BaseContext.bindThreadObjectContext(oc);

        try {
            return handler.service(request, response);
        } finally {
            BaseContext.bindThreadObjectContext(null);
        }
    }
}
