package org.tapestrycayenne.services;

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry.ComponentResources;
import org.apache.tapestry.beaneditor.BeanModel;
import org.apache.tapestry.ioc.annotations.InjectService;
import org.apache.tapestry.ioc.annotations.Marker;
import org.apache.tapestry.services.BeanModelSource;
import org.apache.tapestry.services.Environment;
import org.tapestrycayenne.annotations.Cayenne;
import org.tapestrycayenne.internal.BeanModelTypeHolder;

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
            ComponentResources resources) {
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
