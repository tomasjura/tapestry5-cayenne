/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.map.DataMap;

public abstract class AbstractDBTest extends Assert {
    
    
    protected static void setupdb() throws Exception {
        DefaultConfiguration c = new DefaultConfiguration("/cayenne.xml");
        Configuration.initializeSharedConfiguration(c);
        DbAdapter adapt = HSQLDBAdapter.class.newInstance();
        DataContext dc = DataContext.createDataContext();
        for(Object obj : dc.getEntityResolver().getDataMaps()) {
            DataMap map = (DataMap) obj;
            System.out.println(map.getName());
            DataNode node = dc.getParentDataDomain().lookupDataNode(map);
            System.out.println(node.getName());
            DataSource src = node.getDataSource();
            DbGenerator dbgen = new DbGenerator(adapt,map);
            dbgen.setShouldCreatePKSupport(true);
            dbgen.setShouldCreateFKConstraints(true);
            dbgen.setShouldCreateTables(true);
            dbgen.runGenerator(src);
        }
        DataContext.bindThreadDataContext(dc);
    }

}
