package org.tapestrycayenne.services;

import org.apache.cayenne.CayenneContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.remote.ClientChannel;
import org.apache.cayenne.remote.ClientConnection;
import org.apache.cayenne.remote.hessian.HessianConnection;

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

    private final String webServiceUrl;
    private final String username;
    private final String password;
    private final String sharedSession;

    public CayenneContextProviderImpl(final Map<String, String> configuration)
    {
        webServiceUrl = configuration.get(WEB_SERVICE_URL);
        username = configuration.get(USERNAME);
        password = configuration.get(PASSWORD);
        sharedSession = configuration.get(SHARED_SESSION);
    }

    public ObjectContext currentContext()
    {
        return null;
    }

    public ObjectContext newContext()
    {
        final ClientConnection conn = new HessianConnection(webServiceUrl, username, password, sharedSession);
        final DataChannel channel = new ClientChannel(conn);

        return new CayenneContext(channel);
    }
}
