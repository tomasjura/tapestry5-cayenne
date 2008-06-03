package com.googlecode.tapestry5cayenne.services;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.apache.tapestry5.services.Environment;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.internal.BeanModelTypeHolder;

/**
 * DataTypeAnalyzer to handle cayenne properties. In particular, 
 * the CayenneDataTypeAnalyzer handles Cayenne toOne and toMany relationships.
 * @author robertz
 *
 */
@Marker(Cayenne.class)
public class CayenneDataTypeAnalyzer implements DataTypeAnalyzer {
    
    private final ObjectContextProvider _provider;
    private final Environment _environment;
    private final DataTypeAnalyzer _defaultAnalyzer;
    
    public CayenneDataTypeAnalyzer(
            final ObjectContextProvider provider, 
            final Environment environment,
            final @InjectService("DefaultDataTypeAnalyzer") DataTypeAnalyzer analyzer) 
    {
        _provider = provider;
        _environment = environment;
        _defaultAnalyzer = analyzer;
    }
    
    public String identifyDataType(PropertyAdapter adapter)
    {
        String dt = _defaultAnalyzer.identifyDataType(adapter);
        if (dt != null && !dt.equals("")) {
            return dt;
        }
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
