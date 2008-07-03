package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.tapestry5.PrimaryKeyEncoder;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.VersionUtils;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.PersistentFieldStrategy;
import org.apache.tapestry5.services.ValueEncoderFactory;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.internal.PersistentManagerImpl;

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
    
    /**
     * Configuration/symbol key for ascertaining the version of the tapestry5-cayenne library.
     */
    public static final String T5CAYENNE_VERSION="tapestry5cayene.version";
    
    public static final String T5CAYENNE_PERSISTENCE_STRATEGY="cayenneentity";
    
    
    public static void contributeFactoryDefaults(MappedConfiguration<String,String> conf) {
        conf.add(FILTER_LOCATION,"after:*");
        conf.add(UNPERSISTED_OBJECT_LIMIT,"500");
        conf.add(T5CAYENNE_VERSION,
                 VersionUtils.readVersionNumber(
                         "META-INF/maven/com.googlecode.tapestry5-cayenne/tapestry5-cayenne-core/pom.properties"));
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
        binder.bind(PersistentManager.class,PersistentManagerImpl.class);
        binder.bind(PrimaryKeyEncoder.class,CayennePrimaryKeyEncoder.class).withId("CayennePrimaryKeyEncoder");
    }
    
    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
        configuration.add(new LibraryMapping("t5cayenne", "com.googlecode.tapestry5cayenne"));
    }

    public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration,
                                                    @Cayenne final ObjectContextProvider provider,
                                                    final TypeCoercer coercer,
                                                    final PersistentManager manager,
                                                    final NonPersistedObjectStorer storer)
    {
        configuration.add(Persistent.class, new ValueEncoderFactory<Persistent>()
        {
            public ValueEncoder<Persistent> create(Class<Persistent> persistentClass)
            {
                return new CayenneEntityEncoder(provider,coercer,manager, storer);
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
        conf.add("Cayenne", analyzer, "after:Annotation");
    }
    
    public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> conf) {
        conf.add(new BeanBlockContribution("to_one", "t5cayenne/CayenneEditBlockContributions", "to_one_editor", true));
        conf.add(new BeanBlockContribution("to_one","t5cayenne/CayenneViewBlockContributions","to_one_viewer",false));
        conf.add(new BeanBlockContribution("to_many_map","t5cayenne/CayenneViewBlockContributions","to_many_map_viewer",false));
        conf.add(new BeanBlockContribution("to_many_collection","t5cayenne/CayenneViewBlockContributions","to_many_collection_viewer",false));
    }
    
    public static void contributeClasspathAssetAliasManager(MappedConfiguration<String,String> configuration,
                                                            @Symbol(T5CAYENNE_VERSION)
                                                            String version)
    {
        configuration.add("t5cayenne/" + version,"com/googlecode/tapestry5cayenne");
    }
    
    /**
     * Contributes the following: <dl> <dt>cayenneentity</dt> <dd>Stores the id of the entity and reloads from the {@link
     * ObjectContext}</dd> </dl>
     */
    public static void contributePersistentFieldManager(
            MappedConfiguration<String, PersistentFieldStrategy> configuration,
            ObjectLocator locator)
    {
        configuration.add("cayenneentity", locator.autobuild(CayenneEntityPersistentFieldStrategy.class));
    }       

}
