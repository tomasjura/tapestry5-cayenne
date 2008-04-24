/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne;

import javax.sql.DataSource;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.map.DataMap;
import org.apache.tapestry.internal.InternalConstants;
import org.apache.tapestry.internal.SingleKeySymbolProvider;
import org.apache.tapestry.internal.TapestryAppInitializer;
import org.apache.tapestry.internal.test.PageTesterModule;
import org.apache.tapestry.ioc.Registry;
import org.apache.tapestry.ioc.services.SymbolProvider;
import org.testng.Assert;

public abstract class AbstractDBTest extends Assert {
    
    
    protected static void setupdb() throws Exception {
        DefaultConfiguration c = new DefaultConfiguration("cayenne.xml");
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
    
    /**
     * Builds a basic test registry
     * @param modules
     * @return
     */
    protected static Registry setupRegistry(String appName, Class<?>...modules) {
        SymbolProvider provider = new SingleKeySymbolProvider(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "org.tapestrycayenne.integration");
        TapestryAppInitializer initializer = new TapestryAppInitializer(provider, appName, PageTesterModule.TEST_MODE);
        if (modules.length > 0) {
            initializer.addModules(modules);
        }
        Registry ret = initializer.getRegistry();
        return ret;

    }

}
