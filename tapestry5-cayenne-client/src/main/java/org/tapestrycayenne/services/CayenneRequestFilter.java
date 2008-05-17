/**
 * Created by IntelliJ IDEA.
 * User: nirvdrum
 * Date: May 17, 2008
 * Time: 5:36:38 PM
 * To change this template use File | Settings | File Templates.
 */

package org.tapestrycayenne.services;

import org.apache.tapestry.services.*;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.CayenneContext;
import org.apache.cayenne.ObjectContext;

import java.io.IOException;

/**
 * Provides a RequestFilter which ensures that there is a CayenneContext associated with the current request.
 * Currently uses a session-based strategy.
 *
 * @author Kevin Menard
 */
public class CayenneRequestFilter implements RequestFilter
{
    private final ApplicationStateManager asm;
    private final ObjectContextProvider provider;

    public CayenneRequestFilter(final ApplicationStateManager asm, final ObjectContextProvider provider) {
        this.asm = asm;
        this.provider = provider;
    }

    public boolean service(Request request, Response response, RequestHandler handler)
            throws IOException {
        ObjectContext context;

        if (asm.exists(CayenneContext.class))
        {
            context = asm.get(CayenneContext.class);
        }
        else
        {
            context = provider.newContext();
            asm.set(ObjectContext.class, context);
        }

        try
        {
            return handler.service(request, response);
        }
        finally
        {
            asm.set(ObjectContext.class, null);
        }
    }
}
