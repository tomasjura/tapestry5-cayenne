package org.tapestrycayenne.internal;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.query.Ordering;

public enum QuerySortType {
    NOSORT,
    METHOD {
        public void sort(final List<?> results, final Ordering ordering, final Method label) {
            Collections.sort(results,new Comparator<Object>() {

                @SuppressWarnings("unchecked")
                public int compare(Object o1, Object o2) {
                    Object lbl1;
                    Object lbl2;
                    try {
                        lbl1 = label.invoke(o1);
                        lbl2 = label.invoke(o2);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (lbl1 instanceof Comparable) {
                        return ((Comparable)lbl1).compareTo(lbl2);
                    }
                    return lbl1.toString().compareTo(lbl2.toString());
                }
                
            });
        }
    },
    ORDERING {
        public void sort(final List<?> results, final Ordering ordering, final Method label) {
            ordering.orderList(results);
        }
    },
    COMPARABLE {
        @SuppressWarnings("unchecked")
        public void sort(final List<?> results, final Ordering ordering, final Method label) {
            Collections.sort((List<Comparable>)results);
        }
    },
    QUERY;
    
    public void sort(final List<?> results,final Ordering ordering, final Method label) {
        //default is no=op;
    }
}
