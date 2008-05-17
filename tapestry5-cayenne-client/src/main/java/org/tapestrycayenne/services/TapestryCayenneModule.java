/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry.ioc.annotation.SubModule;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.ServiceBinder;
import org.apache.tapestry.services.RequestFilter;
import org.tapestrycayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {
    
    /**
     * Key which provides the default location to insert the CayenneRequestFilter.
     * By default, this is at the end of the requestfilter pipeline.
     */
    public static final String FILTER_LOCATION="tapestrycayenne.filterlocation";
    
    public static void contributeFactoryDefaults(MappedConfiguration<String,String> conf) {
        //conf.add();
    }
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ObjectContextProvider.class, CayenneContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("CayenneContext");
    }
    
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
            @Cayenne
            RequestFilter filter)
    {
        configuration.add("cayenne", filter, "after:*");
    }
}
