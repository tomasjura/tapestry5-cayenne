/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.RequestFilter;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.services.NonPersistedObjectStorer;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import com.googlecode.tapestry5cayenne.services.TapestryCayenneCoreModule;

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
            @Cayenne NonPersistedObjectStorer storer,
            @Cayenne ObjectContextProvider provider) {
        conf.add(AliasContribution.create(NonPersistedObjectStorer.class, storer));
        conf.add(AliasContribution.create(ObjectContextProvider.class, provider));
    }

}
