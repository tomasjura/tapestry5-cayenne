/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.AliasContribution;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ObjectContextProvider.class, DataContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("DataContext");
    }
    
    /**
     * Alias the DataContext-based object context provider to ObjectContextProvider.
     * @param conf
     * @param storer
     * @param provider
     */
    @SuppressWarnings("unchecked")
    public static void contributeAlias(Configuration<AliasContribution> conf,
            @Cayenne NonPersistedObjectStorer storer,
            @Cayenne ObjectContextProvider provider) {
        conf.add(AliasContribution.create(ObjectContextProvider.class, provider));
    }
}
