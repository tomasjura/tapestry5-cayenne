/*
 * Created on Apr 5, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.tapestry5.ioc.MappedConfiguration;

public class TestModule {
    //allows us to contribute the alias mode for tests.
    public void contributeApplicationDefaults(MappedConfiguration<String, String> conf) {
        System.out.println("In contributeApplicationDefaults?");
        conf.add("tapestry.alias-mode","production");
    }
    
}
