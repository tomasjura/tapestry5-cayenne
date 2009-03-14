package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;

public enum QueryType {

    EJBQL(EJBQLQuery.class) {
        public void setPageSize(Query q, int size) {
            //no-op. EJBQL doesn't support setPageSize in 3.0M5.
            //TODO add setPageSize here when 3.0M6 is released.
        }
    },
    SQLTEMPLATE(SQLTemplate.class) {
        public void setPageSize(Query q, int size) {
            ((SQLTemplate)q).setPageSize(size);
        }
    },
    SELECT(SelectQuery.class){
        public void setPageSize(Query q, int size) {
            ((SelectQuery)q).setPageSize(size);
        }
    };
    
    private Class<? extends Query> queryClass;
    
    private QueryType(Class<? extends Query> queryClass) {
        this.queryClass = queryClass;
    }
    
    public abstract void setPageSize(Query q,int size);
    
    public static QueryType typeForQuery(Query q) {
        for(QueryType type : values()) {
            if (type.queryClass.isAssignableFrom(q.getClass())) {
                return type;
            }
        }
        throw new RuntimeException("Error resolving query type - unknown type: " + q.getClass().getName());
    }
}
