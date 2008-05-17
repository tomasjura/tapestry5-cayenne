package org.tapestrycayenne.services;

import org.apache.cayenne.CayenneContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.remote.ClientChannel;
import org.apache.cayenne.remote.ClientConnection;
import org.apache.cayenne.remote.hessian.HessianConnection;
import org.apache.tapestry.services.ApplicationStateManager;
import org.apache.tapestry.ioc.annotation.Symbol;
import org.apache.tapestry.ioc.annotation.Value;
import org.apache.tapestry.ioc.annotation.Inject;

/**
 * Implementation of provider for CayenneContext.
 *
 * @author Kevin Menard
 * @version 1.0
 */
public class CayenneContextProviderImpl implements ObjectContextProvider
{
    private final ApplicationStateManager asm;
    private final String webServiceUrl;
    private final String username;
    private final String password;
    private final String sharedSessionName;

    @SuppressWarnings("unchecked")
    public CayenneContextProviderImpl(
            final ApplicationStateManager asm,

            @Inject
            @Symbol(TapestryCayenneModule.WEB_SERVICE_URL)
            final String webServiceUrl,

            @Inject
            @Symbol(TapestryCayenneModule.USERNAME)
            final String username,

            @Inject
            @Symbol(TapestryCayenneModule.PASSWORD)
            final String password,

            @Inject
            @Symbol(TapestryCayenneModule.SHARED_SESSION_NAME)
            final String sharedSessionName)
    {
        this.asm = asm;

        this.webServiceUrl = webServiceUrl;
        this.username = username;
        this.password = password;
        this.sharedSessionName = sharedSessionName;
    }

    // TODO (KJM 4/1/08) We shouldn't create a new instance if it doesn't exist . . . that's somebody else's job.
    public ObjectContext currentContext()
    {
        if (false == asm.exists(ObjectContext.class))
        {
            asm.set(ObjectContext.class, newContext());
        }

        return asm.get(ObjectContext.class);
    }

    public ObjectContext newContext()
    {
        final ClientConnection conn = new HessianConnection(webServiceUrl, username, password, sharedSessionName);
        final DataChannel channel = new ClientChannel(conn);

        return new CayenneContext(channel);
    }
}
