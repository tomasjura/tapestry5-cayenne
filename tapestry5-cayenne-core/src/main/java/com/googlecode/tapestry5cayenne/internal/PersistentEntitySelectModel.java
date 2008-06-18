package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.util.AbstractSelectModel;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;

/**
 * Defines a selection model for use with cayenne relationships.
 * The model will build a list of all objects of the type provided to the constructor.
 * Objects will be ordered according to the following rules:
 * 1) If orderings are explicitly provided via the varags constructor parameter, those orderings will be used.
 * 2) If the class type has a method annotated with the Label annotation, then the objects will be 
 *  ordered by the result of invoking the labeled method.  The model will check to see if the method
 *  corresponds to a known database property.  If so, the sort will occur at the database level.
 *  If not, the sort will occur in-memory.  
 * 3) If no label is found, but the class is an instance of comparable, the objects will be sorted in-memory
 *  according to their "comparable" ordering.
 * 4) If no method can be found and the objects are not comparable, no sorting will occur.
 * 
 * Each option in the model displays a single Persistent object.  The user-presented value for the object
 * is the result of invoking a @Label-annotated method, if present, or toString().
 * 
 * @author robertz
 * @see Label
 */
public class PersistentEntitySelectModel extends AbstractSelectModel {
    
    private final List<OptionModel> _options;
    
    @SuppressWarnings("unchecked")
    /**
     * Constructs the model by looking up the entities corresponding to type, using the provided ObjectContext.
     * All provided orderings will be honored.  
     */
    public PersistentEntitySelectModel(Class<?> type, ObjectContext context,Ordering...orderings) {
        final Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
        SelectQuery sq = new SelectQuery(type);
        QuerySortResult rslt = querySort(sq,label,context,type,orderings);
        List<?> options = context.performQuery(sq);
        rslt.type.sort(options, rslt.ordering, label);
        _options = new ArrayList<OptionModel>();
        for(Object obj : options) {
            _options.add(new LabelOptionModel(obj,label));
        }
    }
    
    /**
     * Converts the provided property names to orderings, assuming an ordering of ascending for all properties.
     * @param vals
     * @return an array of Ordering objects.
     */
    public static Ordering[] stringToOrdering(String...vals) {
        return stringToOrdering(true,vals);
    }
    
    /**
     * Converts the provided property names to orderings.  
     * All orderings will be ascending or descending, according to the ascending parameter.
     * @param ascending
     * @param vals
     * @return
     */
    public static Ordering[] stringToOrdering(boolean ascending,String...vals) {
        Ordering[] o = new Ordering[vals.length];
        for(int i=0;i<o.length;i++) {
            o[i]=new Ordering(vals[i],ascending);
        }
        return o;
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
              sq.addOrderings(Arrays.asList(stringToOrdering(order.ascending()[0],order.orderings())));
            } else if (order.ascending().length==order.orderings().length) {
                for(int i=0;i<order.orderings().length;i++) {
                    sq.addOrdering(new Ordering(order.orderings()[i],order.ascending()[i]));
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
    
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    public List<OptionModel> getOptions() {
        return _options;
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
