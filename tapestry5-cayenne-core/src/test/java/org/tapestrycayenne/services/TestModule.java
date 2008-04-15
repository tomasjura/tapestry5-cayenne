/*
 * Created on Apr 5, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry.ioc.MappedConfiguration;

public class TestModule {
    //allow us to contribute the alias mode for tests.
    public void contributeApplicationDefaults(MappedConfiguration<String, String> conf) {
        conf.add("tapestry.alias-mode","production");
    }

}
