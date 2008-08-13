package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.annotations.OCType;

/**
 * Provides an ObjectProvider so pages and components can @Inject an ObjectContext directly.
 * @author robertz
 *
 */
public class ObjectContextObjectProvider implements ObjectProvider {
    
    private final ObjectContextProvider provider;
    
    /**
     * @param provider the ObjectContextProvider which is ultimately used to grab the appropriate context.
     */
    //putting @Cayenne on the parameter is the difference between T5-ioc
    //dying a gruesome death (recursive dependencies), and starting happily.
    public ObjectContextObjectProvider(@Cayenne final ObjectContextProvider provider) {
        this.provider = provider;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<T> type, AnnotationProvider ap, ObjectLocator locator) {
        if (!(ObjectContext.class.isAssignableFrom(type))) {
            return null;
        }
        OCType t = ap.getAnnotation(OCType.class);
        if (t == null) {
            return (T) provider.currentContext();
        }
        switch(t.value()) {
            case CURRENT:
                return (T) provider.currentContext();
            case NEW:
                return (T) provider.newContext();
            case CHILD:
                ObjectContext oc = provider.currentContext();
                if (oc instanceof DataContext) {
                    return (T) ((DataContext)oc).createChildDataContext();
                }
                throw new IllegalStateException("Cannot create child data context from current context: " + oc);    
            default:
                throw new RuntimeException("Unsupported ContextType: " + t.value());
        }
    }

}
