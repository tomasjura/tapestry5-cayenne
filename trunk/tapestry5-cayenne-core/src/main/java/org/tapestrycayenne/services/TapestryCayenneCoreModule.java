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

public class TapestryCayenneCoreModule {
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) {
        binder.bind(ValueEncoder.class,CayenneEntityEncoder.class)
            .withId("CayenneEntityEncoder");
        binder.bind(BeanModelSource.class, CayenneBeanModelSource.class)
            .withId("CayenneBeanModelSource");
        binder.bind(DataTypeAnalyzer.class,CayenneDataTypeAnalyzer.class)
            .withId("CayenneDataTypeAnalyzer");
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
