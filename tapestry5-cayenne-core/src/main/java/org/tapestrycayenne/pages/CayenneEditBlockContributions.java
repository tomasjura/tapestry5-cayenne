package org.tapestrycayenne.pages;

import org.apache.tapestry.annotation.Component;
import org.tapestrycayenne.components.ToOneEditor;

/**
 * Contains all of the custom "bean editor" blocks used for editing cayenne objects.
 * @author robertz
 *
 */
public class CayenneEditBlockContributions {
    
    @SuppressWarnings("unused")
    @Component
    private ToOneEditor _toOne;
    
}
