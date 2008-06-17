package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Environment;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.internal.BeanModelTypeHolder;

/**
 * Provides a cayenne-specific implementation of BeanModelSource.
 * This is used to override the default implementation.
 * It is capable of handling Persistent and non-persistent objects.
 * It ensures that extraneous properties inherited from, eg, 
 * CayenneDataObject don't show up in the default model.
 * @author robertz
 *
 */
@Marker(Cayenne.class)
public class CayenneBeanModelSource implements BeanModelSource {
    
    private final BeanModelSource _source;
    private final ObjectContextProvider _provider;
    private final Environment _environment;
    
    private static final String[] defaultExcludes = new String[] {
       "persistenceState",
       "snapshotVersion",
    };
    
    public CayenneBeanModelSource(
            @InjectService("BeanModelSource")
            final BeanModelSource source,
            final ObjectContextProvider provider,
            final Environment environment) {
        _source = source;
        _provider = provider;
        _environment = environment;
    }

    @SuppressWarnings("unchecked")
    public <T> BeanModel<T> create(Class<T> type, boolean filterReadOnlyProperties, 
            ComponentResources resources)
    {
        _environment.push(BeanModelTypeHolder.class, new BeanModelTypeHolder(type));
        BeanModel<T> model = _source.create(type, filterReadOnlyProperties, resources);

        _environment.pop(BeanModelTypeHolder.class);
        ObjEntity ent = _provider.currentContext().getEntityResolver().lookupObjEntity(type);

        if (ent == null) {
            return model;
        }

        return model.exclude(defaultExcludes);
    }
}