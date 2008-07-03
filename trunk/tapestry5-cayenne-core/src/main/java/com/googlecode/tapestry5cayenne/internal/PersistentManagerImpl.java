package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.services.TypeCoercer;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

public class PersistentManagerImpl implements PersistentManager {
    
    private final ObjectContextProvider _provider;
    private final TypeCoercer _coercer;
    
    public PersistentManagerImpl(ObjectContextProvider provider, TypeCoercer coercer) {
        _provider = provider;
        _coercer = coercer;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listAll(Class<T> type,
            Ordering... orderings) {
        SelectQuery sq = new SelectQuery(type);
        ObjectContext context = _provider.currentContext();
        Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
        QuerySortResult rslt = querySort(sq,label,context,type,orderings);
        List<T> values = context.performQuery(sq);
        rslt.type.sort(values,rslt.ordering,label);
        return values;
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> listMatching(Class<T> type, Expression qualifier, Ordering... orderings) {
        SelectQuery sq = new SelectQuery(type);
        ObjectContext context = _provider.currentContext();
        Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
        QuerySortResult rslt = querySort(sq,label,context,type,orderings);
        sq.setQualifier(qualifier);
        List<T> values = context.performQuery(sq);
        rslt.type.sort(values,rslt.ordering,label);
        return values;
    }

    /**
     * Determines what type of sorting to use for the given class.
     * Only reason this is not private is so that TestPersistentEntitySelectModel 
     * can more easily test it. 
     * @param sq
     * @param label
     * @param context
     * @param type
     * @param orderings
     * @return
     */
    static QuerySortResult querySort(SelectQuery sq,Method label, ObjectContext context, Class<?> type,Ordering[] orderings) {
        QuerySortResult res = new QuerySortResult();
        //first check to see if there's anything in orderings...
        if (orderings.length>0) {
            sq.addOrderings(Arrays.asList(orderings));
            res.type=QuerySortType.QUERY;
            return res;
        }
        //check for ordering annotation...
        DefaultOrder order = type.getAnnotation(DefaultOrder.class);
        if (order != null) {
            if (order.ascending().length==1) {
              sq.addOrderings(Arrays.asList(OrderingUtils.stringToOrdering(order.ascending()[0],order.value())));
            } else if (order.ascending().length==order.value().length) {
                for(int i=0;i<order.value().length;i++) {
                    sq.addOrdering(new Ordering(order.value()[i],order.ascending()[i]));
                }
            } else {
                throw new RuntimeException("DefaultOrdering.ascending.length != 1 and DefaultOrdering.ascending.length != DefaultOrdering.orderings.length for type: " + type.getName());
            }
            res.type=QuerySortType.QUERY;
            return res;
        }
        if (label == null) {
            //check to see if the objs are comparable...
            if (Comparable.class.isAssignableFrom(type)) {
                res.type=QuerySortType.COMPARABLE;
            } else {
                res.type=QuerySortType.NOSORT;
            }
            return res;
        }
        if (!label.getName().startsWith("get")) {
            res.type=QuerySortType.METHOD;
            return res;
        }
        //extract the property name.
        String name = label.getName();
        if (name.length() == 4) {
            name = name.substring(3).toLowerCase();
        } else if (Character.isLowerCase(name.charAt(4))) {
            name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
        } else {
            name = name.substring(3);
        }
        Ordering o = new Ordering(name,true);
        res.ordering=o;
        
        ObjEntity ent = context.getEntityResolver().lookupObjEntity(type);
        ObjAttribute attr = (ObjAttribute) ent.getAttribute(name);
        if (attr != null) {
            sq.addOrdering(o);
            res.type=QuerySortType.QUERY;
        } else {
            res.type=QuerySortType.ORDERING;
        }
        return res;
    }

    public <T extends Persistent> T find(Class<T> type, Object id) {
        Object pk = _coercer.coerce(id, pkTypeForEntity(type));
        return DataObjectUtils.objectForPK(_provider.currentContext(),type,pk);
    }
    
    private Class<?> pkTypeForEntity(String name) {
        return pkTypeForEntity(_provider.currentContext().getEntityResolver().getObjEntity(name));
    }
    
    private Class<?> pkTypeForEntity(Class<?> type) {
        return pkTypeForEntity(_provider.currentContext().getEntityResolver().lookupObjEntity(type));
    }
    
    private Class<?> pkTypeForEntity(ObjEntity entity) {
        Collection<ObjAttribute> atts = entity.getPrimaryKeys();
        if (atts.size() != 1) {
            throw new RuntimeException("T5Cayenne integration currently only handles entities with single-column primary keys");
        }
        ObjAttribute attribute = atts.iterator().next(); 
        return attribute.getJavaClass();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T find(String entity, Object id) {
        Object pk = _coercer.coerce(id,pkTypeForEntity(entity));
        return (T) DataObjectUtils.objectForPK(_provider.currentContext(), entity, pk);
    }

}

/**
 * Data Storage class for holding information about how the query sort should take place.
 * @author robertz
 *
 */
class QuerySortResult {
  
    QuerySortResult() {
        
    }
    
    QuerySortResult(QuerySortType type, Ordering ordering) {
        this.type=type;
        this.ordering = ordering;
    }
    QuerySortType type;
    Ordering ordering;
}
