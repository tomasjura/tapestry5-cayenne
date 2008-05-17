package org.tapestrycayenne;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
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
import org.tapestrycayenne.model.Artist;
import org.tapestrycayenne.model.Painting;

public class TestUtils {
    
    
    public static void setupdb() throws Exception {
        DefaultConfiguration c = new DefaultConfiguration("cayenne.xml");
        Configuration.initializeSharedConfiguration(c);
        DbAdapter adapt = HSQLDBAdapter.class.newInstance();
        DataContext dc = DataContext.createDataContext();
        for(Object obj : dc.getEntityResolver().getDataMaps()) {
            DataMap map = (DataMap) obj;
            DataNode node = dc.getParentDataDomain().lookupDataNode(map);
            DataSource src = node.getDataSource();
            DbGenerator dbgen = new DbGenerator(adapt,map);
            dbgen.setShouldCreatePKSupport(true);
            dbgen.setShouldCreateFKConstraints(true);
            dbgen.setShouldCreateTables(true);
            dbgen.runGenerator(src);
        }
        DataContext.bindThreadDataContext(dc);
    }
    
    public static List<Artist> basicData(ObjectContext context) {
        List<Artist> ret = new ArrayList<Artist>();
        Artist a = context.newObject(Artist.class);
        a.setName("Picasso");
        
        Painting p = context.newObject(Painting.class);
        p.setArtist(a);
        p.setPrice(10000.0);
        p.setTitle("Portrait of Igor Stravinsky");
        
        p = context.newObject(Painting.class);
        p.setArtist(a);
        p.setPrice(15000.0);
        p.setTitle("Dora Maar au Chat");
        
        ret.add(a);
        
        a = context.newObject(Artist.class);
        a.setName("Dali");
        
        p = context.newObject(Painting.class);
        p.setArtist(a);
        p.setTitle("Self-portrait");
        p.setPrice(12000.0);
        
        p = context.newObject(Painting.class);
        p.setArtist(a);
        p.setTitle("The Persistence of Memory");
        p.setPrice(19000.0);
        
        ret.add(a);
        
        context.commitChanges();
        
        return ret;
    }
    
    public static Registry setupRegistry(String appName, Class<?>...modules) {
        SymbolProvider provider = new SingleKeySymbolProvider(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "org.tapestrycayenne.integration");
        TapestryAppInitializer initializer = new TapestryAppInitializer(provider, appName, PageTesterModule.TEST_MODE);
        if (modules.length > 0) {
            initializer.addModules(modules);
        }
        Registry ret = initializer.getRegistry();
        return ret;

    }

}
