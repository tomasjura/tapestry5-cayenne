/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry.ioc.Configuration;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.ServiceBinder;
import org.apache.tapestry.ioc.annotation.SubModule;
import org.apache.tapestry.ioc.annotation.Symbol;
import org.apache.tapestry.services.AliasContribution;
import org.apache.tapestry.services.RequestFilter;
import org.tapestrycayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {
    
    /**
     * Key which provides the default location to insert the CayenneRequestFilter.
     * By default, this is at the end of the requestfilter pipeline.
     */
    public static final String FILTER_LOCATION="tapestrycayenne.filterlocation";
    
    /**
     * Configuration key providing the limit of the number of unpersisted objects to retain in memory.
     * This is used by the default implementation of NonPersistedObjectStorer to set an upper bounds 
     * to the amount of objects allowed to accrue in memory.  The default is 500.
     */
    public static final String UNPERSISTED_OBJECT_LIMIT="tapestrycayenne.unpersistedlimit";
    
    /**
     * 
     * @param conf
     */
    
    public static void contributeFactoryDefaults(MappedConfiguration<String,String> conf) {
        conf.add(FILTER_LOCATION,"after:*");
        conf.add(UNPERSISTED_OBJECT_LIMIT,"500");
    }
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ObjectContextProvider.class, DataContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("DataContext");
        
        binder.bind(NonPersistedObjectStorer.class,DefaultNonPersistedObjectStorer.class)
            .withId("DefaultNonPersistedObjectStorer").withMarker(Cayenne.class);

        binder.bind(RequestFilter.class, CayenneRequestFilter.class)
            .withId("CayenneFilter")
            .withMarker(Cayenne.class);
        
    }
    
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
            @Cayenne 
            RequestFilter filter,
            @Symbol(FILTER_LOCATION)  
            String location)
    {
        configuration.add("cayenne", filter, location);
    }
    
    public static void contributeAlias(Configuration<AliasContribution> conf,
            @Cayenne NonPersistedObjectStorer storer) {
        conf.add(AliasContribution.create(NonPersistedObjectStorer.class, storer));
    }

}
