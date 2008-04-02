/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.cayenne.Persistent;
import org.apache.tapestry.ValueEncoder;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.ServiceBinder;
import org.apache.tapestry.services.RequestFilter;
import org.apache.tapestry.services.ValueEncoderFactory;
import org.tapestrycayenne.annotations.Cayenne;
import org.tapestrycayenne.annotations.CayenneClient;

public class TapestryCayenneModule {
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ValueEncoder.class,CayenneEntityEncoder.class)
            .withId("CayenneEntityEncoder").withMarker(Cayenne.class);

        binder.bind(ObjectContextProvider.class, CayenneContextProviderImpl.class)
            .withMarker(CayenneClient.class);

        binder.bind(ObjectContextProvider.class, DataContextProviderImpl.class)
            .withMarker(Cayenne.class);

        binder.bind(RequestFilter.class, CayenneRequestFilter.class)
            .withId("CayenneFilter")
            .withMarker(Cayenne.class);
    }
    
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
            @Cayenne
            RequestFilter filter)
    {
        configuration.add("cayenne", filter, "after:Localization");
    }

    public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration,
                                                    @CayenneClient final ObjectContextProvider provider)
    {
        configuration.add(Persistent.class, new ValueEncoderFactory<Persistent>()
        {
            public ValueEncoder<Persistent> create(Class<Persistent> persistentClass)
            {
                return new CayenneEntityEncoder(provider);
            }
        });
    }
}
