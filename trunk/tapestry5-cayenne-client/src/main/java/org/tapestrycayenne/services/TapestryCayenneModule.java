/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestFilter;
import org.tapestrycayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule
{
    /**
     * Configuration key for setting the URL for the Cayenne Web Service that the client should connect to.
     */
    public static final String WEB_SERVICE_URL = "tapestrycayenne.client.web_service_url";

    /**
     * Configuration key for setting the username to use in basic auth to the Cayenne Web Service.
     */
    public static final String USERNAME = "tapestrycayenne.client.username";

    /**
     * Configuration key for setting the password to use in basic auth ot the Cayenne Web Service.
     */
    public static final String PASSWORD = "tapestrycayenne.client.password";

    /**
     * Configuration key for setting the Hessian shared session name.
     */
    public static final String SHARED_SESSION_NAME = "tapestrycayenne.client.shared_session_name";

    public static void contributeFactoryDefaults(final MappedConfiguration<String,String> conf)
    {
        conf.add(WEB_SERVICE_URL, "http://localhost:8080/cws");
        conf.add(USERNAME, "");
        conf.add(PASSWORD, "");
        conf.add(SHARED_SESSION_NAME, "");
    }

    @SuppressWarnings("unchecked")
    public static void bind(final ServiceBinder binder)
    {
        binder.bind(ObjectContextProvider.class, CayenneContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("CayenneContext");

        binder.bind(RequestFilter.class, CayenneRequestFilter.class)
            .withId("CayenneFilter")
            .withMarker(Cayenne.class);
    }

    public static void contributeRequestHandler(final OrderedConfiguration<RequestFilter> configuration,
            @Cayenne
            RequestFilter filter,

            @Symbol(TapestryCayenneCoreModule.FILTER_LOCATION)
            String location)
    {
        configuration.add("cayenne", filter, location);
    }
}