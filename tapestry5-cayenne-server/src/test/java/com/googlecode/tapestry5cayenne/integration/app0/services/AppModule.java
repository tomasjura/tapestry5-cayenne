/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.integration.app0.services;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ApplicationInitializer;
import org.apache.tapestry5.services.ApplicationInitializerFilter;
import org.apache.tapestry5.services.Context;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.services.TapestryCayenneModule;

@SubModule(TapestryCayenneModule.class)
public class AppModule {

    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        configuration.add(SymbolConstants.PRODUCTION_MODE,"false");
    }
    
    public static void contributeApplicationInitializer(OrderedConfiguration<ApplicationInitializerFilter> conf) {
        conf.add("setupdb", new ApplicationInitializerFilter() {
            public void initializeApplication(Context context, ApplicationInitializer handler) {
                try {
                    TestUtils.setupdb();
                    DataContext dc = DataContext.getThreadDataContext();
                    List<Artist> artists = TestUtils.basicData(dc);
                    TestUtils.addPaintings(artists.get(0),15,dc);
                    TestUtils.addPaintings(artists.get(1),18,dc);
                } catch (Exception e) { throw new RuntimeException(e); }
                //put in some artists and paintings...
                handler.initializeApplication(context);
            }
        });
    }
}
