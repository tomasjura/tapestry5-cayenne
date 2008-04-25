package org.tapestrycayenne.services;

import java.util.List;

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry.ComponentResources;
import org.apache.tapestry.beaneditor.BeanModel;
import org.apache.tapestry.ioc.annotations.InjectService;
import org.apache.tapestry.ioc.annotations.Marker;
import org.apache.tapestry.services.BeanModelSource;
import org.tapestrycayenne.annotations.Cayenne;

@Marker(Cayenne.class)
public class CayenneBeanModelSource implements BeanModelSource {
    
    private final BeanModelSource _source;
    private final ObjectContextProvider _provider;
    private static final String[] defaultExcludes = new String[] {
       "persistenceState",
       "snapshotVersion",
    };
    
    public CayenneBeanModelSource(
            @InjectService("BeanModelSource")
            final BeanModelSource source,
            final ObjectContextProvider provider) {
        _source = source;
        _provider = provider;
    }

    public <T> BeanModel<T> create(Class<T> type, boolean filterReadOnlyProperties, 
            ComponentResources resources) {
        BeanModel<T> model = _source.create(type, filterReadOnlyProperties, resources);
        ObjEntity ent = _provider.currentContext().getEntityResolver().lookupObjEntity(type);
        if (ent == null) {
            return model;
        }
        return model.exclude(defaultExcludes);
    }

}
