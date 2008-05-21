package org.tapestrycayenne.internal;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.tapestrycayenne.annotations.Label;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a selection model for use with cayenne relationships.
 * @author robertz
 *
 */
public class RelationshipSelectModel extends AbstractSelectModel {
    
    private final List<OptionModel> _options;
    
    @SuppressWarnings("unchecked")
    public RelationshipSelectModel(Class<?> type, ObjectContext context) {
        final Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
        SelectQuery sq = new SelectQuery(type);
        QuerySortResult rslt = querySort(sq,label,context,type);
        List<?> options = context.performQuery(sq);
        rslt.type.sort(options, rslt.ordering, label);
        _options = new ArrayList<OptionModel>();
        for(Object obj : options) {
            _options.add(new LabelOptionModel(obj,label));
        }
    }
    
    static QuerySortResult querySort(SelectQuery sq,Method label, ObjectContext context, Class<?> type) {
        QuerySortResult res = new QuerySortResult();
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
