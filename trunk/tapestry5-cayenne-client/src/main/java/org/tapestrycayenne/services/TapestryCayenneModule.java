/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry.ioc.annotation.SubModule;
import org.apache.tapestry.ioc.annotation.Symbol;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.ServiceBinder;
import org.apache.tapestry.services.RequestFilter;
import org.tapestrycayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {

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

    public static void contributeFactoryDefaults(MappedConfiguration<String,String> conf) {
        conf.add(WEB_SERVICE_URL, "http://localhost:8080/cws");
        conf.add(USERNAME, "");
        conf.add(PASSWORD, "");
        conf.add(SHARED_SESSION_NAME, "");
    }

    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ObjectContextProvider.class, CayenneContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("CayenneContext");
    }

    /*
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
            @Cayenne
            RequestFilter filter,
            @Symbol(TapestryCayenneCoreModule.FILTER_LOCATION)
            String location)
    {
        configuration.add("cayenne", filter, location);
    }
    */
}
