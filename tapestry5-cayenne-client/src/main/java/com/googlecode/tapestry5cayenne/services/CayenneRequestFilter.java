/**
 * Created by IntelliJ IDEA.
 * User: nirvdrum
 * Date: May 17, 2008
 * Time: 5:36:38 PM
 * To change this template use File | Settings | File Templates.
 */

package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.services.*;

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

    public CayenneRequestFilter(final ApplicationStateManager asm, final ObjectContextProvider provider)
    {
        this.asm = asm;
        this.provider = provider;
    }

    public boolean service(final Request request, final Response response, final RequestHandler handler) throws IOException 
    {
        if (false == asm.exists(ObjectContext.class))
        {
            asm.set(ObjectContext.class, provider.newContext());
        }

        return handler.service(request, response);
    }
}
