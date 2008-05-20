/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.RequestFilter;
import org.tapestrycayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ObjectContextProvider.class, DataContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("DataContext");

        binder.bind(RequestFilter.class, CayenneRequestFilter.class)
            .withId("CayenneFilter")
            .withMarker(Cayenne.class);
    }
    
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
            @Cayenne 
            RequestFilter filter,
            @Symbol(TapestryCayenneCoreModule.FILTER_LOCATION)  
            String location)
    {
        configuration.add("cayenne", filter, location);
    }
    
    public static void contributeAlias(Configuration<AliasContribution> conf,
            @Cayenne NonPersistedObjectStorer storer) {
        conf.add(AliasContribution.create(NonPersistedObjectStorer.class, storer));
    }

}
