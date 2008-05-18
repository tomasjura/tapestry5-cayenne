package org.tapestrycayenne.services;

import org.apache.cayenne.Persistent;
import org.apache.tapestry.ValueEncoder;
import org.apache.tapestry.ioc.Configuration;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.ServiceBinder;
import org.apache.tapestry.ioc.services.TypeCoercer;
import org.apache.tapestry.services.AliasContribution;
import org.apache.tapestry.services.BeanBlockContribution;
import org.apache.tapestry.services.BeanModelSource;
import org.apache.tapestry.services.DataTypeAnalyzer;
import org.apache.tapestry.services.LibraryMapping;
import org.apache.tapestry.services.ValueEncoderFactory;
import org.tapestrycayenne.annotations.Cayenne;

/**
 * Core module.  This module is a "SubModule" of the TapestryModule, defined in
 * tapestry5-cayenne-client and tapestry5-cayenne-core.  Any shared services,
 * contributions, etc. will be here.
 * @author robertz
 */
public class TapestryCayenneCoreModule {

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

    public static void contributeFactoryDefaults(MappedConfiguration<String,String> conf) {
        conf.add(FILTER_LOCATION,"after:*");
        conf.add(UNPERSISTED_OBJECT_LIMIT,"500");
    }

    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) {
        binder.bind(ValueEncoder.class,CayenneEntityEncoder.class)
            .withId("CayenneEntityEncoder");

        binder.bind(BeanModelSource.class, CayenneBeanModelSource.class)
            .withId("CayenneBeanModelSource");

        binder.bind(DataTypeAnalyzer.class,CayenneDataTypeAnalyzer.class)
            .withId("CayenneDataTypeAnalyzer");

        binder.bind(NonPersistedObjectStorer.class,DefaultNonPersistedObjectStorer.class)
            .withId("DefaultNonPersistedObjectStorer").withMarker(Cayenne.class);
    }
    
    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
        configuration.add(new LibraryMapping("t5cayenne","org.tapestrycayenne"));
    }

    public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration,
                                                    @Cayenne final ObjectContextProvider provider,
                                                    final TypeCoercer coercer,
                                                    final NonPersistedObjectStorer storer)
    {
        configuration.add(Persistent.class, new ValueEncoderFactory<Persistent>()
        {
            public ValueEncoder<Persistent> create(Class<Persistent> persistentClass)
            {
                return new CayenneEntityEncoder(provider,coercer,storer);
            }
        });
    }
    
    public static void contributeAliasOverrides(Configuration<AliasContribution> conf,
            @Cayenne BeanModelSource source) {
        conf.add(AliasContribution.create(BeanModelSource.class, source));
    }
    
    public static void contributeDataTypeAnalyzer(
            OrderedConfiguration<DataTypeAnalyzer> conf,
            @Cayenne DataTypeAnalyzer analyzer) 
    {
        //add after Annotation; we want to make sure that explicitly-defined data types
        //are honored.
        conf.add("Cayenne", analyzer,"after:Annotation");
    }
    
    public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> conf) {
        conf.add(new BeanBlockContribution("to_one","t5cayenne/CayenneEditBlockContributions","to_one_editor",true));
    }
}
