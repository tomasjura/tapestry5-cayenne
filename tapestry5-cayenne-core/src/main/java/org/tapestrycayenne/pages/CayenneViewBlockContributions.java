package org.tapestrycayenne.pages;

import java.lang.reflect.Method;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.services.PropertyOutputContext;
import org.tapestrycayenne.annotations.Label;
import org.tapestrycayenne.internal.AnnotationFinder;

public class CayenneViewBlockContributions {
    
    @Environmental
    private PropertyOutputContext _context;
    
    public String getToOneString() throws Exception {
        Object val = _context.getPropertyValue();
        if (val == null) {
            return "&nbsp;";
        }
        Method m =AnnotationFinder.methodForAnnotation(Label.class,val.getClass());
        if (m == null) {
            return val.toString();
        }
        return m.invoke(val).toString();
    }

}
