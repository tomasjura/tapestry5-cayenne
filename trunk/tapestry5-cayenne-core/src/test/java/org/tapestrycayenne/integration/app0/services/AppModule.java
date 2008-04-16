/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne.integration.app0.services;

import org.apache.tapestry.TapestryConstants;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.annotations.SubModule;

@SubModule(TapestryCayenneModule.class)
public class AppModule {

    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(TapestryConstants.SUPPORTED_LOCALES_SYMBOL, "en");
        configuration.add(TapestryConstants.PRODUCTION_MODE_SYMBOL,"false");
    }
}
