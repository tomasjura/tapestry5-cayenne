package org.tapestrycayenne.services;

import org.apache.cayenne.CayenneContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.remote.ClientChannel;
import org.apache.cayenne.remote.ClientConnection;
import org.apache.cayenne.remote.hessian.HessianConnection;
import org.apache.tapestry.services.ApplicationStateManager;

import java.util.Map;

/**
 * Implementation of provider for CayenneContext.
 *
 * @author Kevin Menard
 * @version 1.0
 */
public class CayenneContextProviderImpl implements ObjectContextProvider
{
    public static final String WEB_SERVICE_URL = "WEB_SERVICE_URL";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String SHARED_SESSION = "SHARED_SESSION";

    private final ApplicationStateManager asm;
    private final String webServiceUrl;
    private final String username;
    private final String password;
    private final String sharedSession;

    public CayenneContextProviderImpl(final Map<String, String> configuration, final ApplicationStateManager asm)
    {
        this.asm = asm;

        webServiceUrl = configuration.get(WEB_SERVICE_URL);
        username = configuration.get(USERNAME);
        password = configuration.get(PASSWORD);
        sharedSession = configuration.get(SHARED_SESSION);
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
        final ClientConnection conn = new HessianConnection(webServiceUrl, username, password, sharedSession);
        final DataChannel channel = new ClientChannel(conn);

        return new CayenneContext(channel);
    }
}
