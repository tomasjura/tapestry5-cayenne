package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;

public class EJBQLBinding extends AbstractBinding {
    
    private final EJBQLQuery query;
    private final String toString;

    public EJBQLBinding(final Location location, EJBQLQuery query, String toString) {
        super(location);
        this.query = query;
        this.toString = toString;
    }

    public boolean isInvariant() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public Class getBindingType() {
        return EJBQLQuery.class;
    }

    public Object get() {
        return query;
    }
    
    public String toString() {
        return toString;
    }
    
}
