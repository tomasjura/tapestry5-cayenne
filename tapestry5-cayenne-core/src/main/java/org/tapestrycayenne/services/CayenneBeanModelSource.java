package org.tapestrycayenne.services;

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry.ComponentResources;
import org.apache.tapestry.beaneditor.BeanModel;
import org.apache.tapestry.ioc.annotation.Marker;
import org.apache.tapestry.ioc.annotation.InjectService;
import org.apache.tapestry.services.BeanModelSource;
import org.apache.tapestry.services.Environment;
import org.tapestrycayenne.annotations.Cayenne;
import org.tapestrycayenne.internal.BeanModelTypeHolder;

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
