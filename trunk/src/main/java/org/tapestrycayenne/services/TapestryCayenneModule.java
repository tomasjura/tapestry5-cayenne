/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry.ValueEncoder;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.ServiceBinder;
import org.apache.tapestry.services.RequestFilter;
import org.tapestrycayenne.annotations.Cayenne;

public class TapestryCayenneModule {
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ValueEncoder.class,CayenneEntityEncoder.class)
            .withId("CayenneEntityEncoder").withMarker(Cayenne.class);
        binder.bind(ObjectContextProvider.class, ObjectContextProviderImpl.class)
            .withMarker(Cayenne.class);
        binder.bind(RequestFilter.class,CayenneRequestFilter.class)
            .withId("CayenneFilter")
            .withMarker(Cayenne.class);
    }
    
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
            @Cayenne
            RequestFilter filter)
    {
        configuration.add("cayenne",filter,"after:Localization");
    }

}
