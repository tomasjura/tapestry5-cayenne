package org.tapestrycayenne.services;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.tapestry.ioc.services.PropertyAdapter;
import org.apache.tapestry.ioc.annotation.Marker;
import org.apache.tapestry.services.DataTypeAnalyzer;
import org.apache.tapestry.services.Environment;
import org.tapestrycayenne.annotations.Cayenne;
import org.tapestrycayenne.internal.BeanModelTypeHolder;

@Marker(Cayenne.class)
public class CayenneDataTypeAnalyzer implements DataTypeAnalyzer {
    
    private final ObjectContextProvider _provider;
    private final Environment _environment;
    
    public CayenneDataTypeAnalyzer(
            final ObjectContextProvider provider, 
            final Environment environment) {
        _provider = provider;
        _environment = environment;
    }
    
    public String identifyDataType(PropertyAdapter adapter)
    {
        EntityResolver er = _provider.currentContext().getEntityResolver();
        Class<?> type = _environment.peek(BeanModelTypeHolder.class).getType();
        ObjEntity ent = er.lookupObjEntity(type);
        ObjRelationship rel = (ObjRelationship) ent.getRelationship(adapter.getName());

        if (rel == null) { 
            return null;
        }

        if (rel.isToMany()) {
            if (rel.getCollectionType().equals(List.class.getName())) {
                return "to_many_list";
            } else if (rel.getCollectionType().equals(Map.class.getName())){
                return "to_many_map";
            } else {
                throw new UnsupportedOperationException(rel.getCollectionType());
            }
        }

        return "to_one";
    }
}
