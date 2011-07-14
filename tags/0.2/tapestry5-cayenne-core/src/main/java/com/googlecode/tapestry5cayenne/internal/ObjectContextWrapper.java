package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ioc.services.PerthreadManager;

import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

/**
 * Object that wraps ObjectContextProvider for use, in conjunction with PropertyShadowBuilder, 
 * in injected fields in pages and components.
 * @author robertz
 *
 */
/*
 * Tapestry injects an object into pages, components, and services, but that injection
 * is a one-time deal when the page/component/service is initialized.
 * If we want to be able to inject a thread-bound ObjectContext, we have to provide a wrapper.
 * We'll use the shadow property builder to build actually retrieve the object for us at runtime,
 * but it requires the use of a "service" to build from, and a property to retrieve.
 * ObjectContextProvider doesn't uses javabeans properties for its methods, so we can't use it
 * directly.  We also don't /want/ to use it directly in the case of child context or
 * new context, b/c we don't want to create a new context for every method invocation.
 * Rather, we want to create a new context for the /first/ method invocation, and then return
 * that context for subsequent invocations for the same request.
 */
public class ObjectContextWrapper {
    
    /**
     * Key for the base request attribute key.
     * Note that we have to uniquify this to guarantee that there is no collision between
     * different @Inject'ed fields.
     */
    private static final String REQUEST_BOUND_CONTEXT="tapestry5cayenne.thread.context";
    
    /**
     * Use the request to store the new or child context for the duration of the request.
     */
    private final PerthreadManager threadManager;
    
    private final ObjectContextProvider provider;
    
    private final String threadContextKey;
    
    public ObjectContextWrapper(PerthreadManager threadManager,ObjectContextProvider provider) {
        this.threadManager = threadManager;
        this.provider = provider;
        threadContextKey = REQUEST_BOUND_CONTEXT + this.hashCode();
    }
    
    /**
     * @return a new object context for this thread.
     * Notes:
     *  1) Only a single ObjectContext will be created per thread.  It is created "on demand".
     *  2) The context is guaranteed to be different from the session-bound context
     *  3) The context will only live for the duration of the thread.
     */
    public ObjectContext getNewContext() {
        ObjectContext context = (ObjectContext) threadManager.get(threadContextKey);
        if (context == null) {
            context = provider.newContext();
            threadManager.put(threadContextKey,context);
        }
        return context;
    }
    
    /**
     * @return the current ObjectContext (normally session-bound).
     */
    public ObjectContext getCurrentContext() {
        return provider.currentContext();
    }
    
    /**
     * 
     * @return a (new) child ObjectContext of the thread-bound (normally session-bound) ObjectContext.
     * As for getNewContext, only one child context is created per thread, and it only lasts the duration of the thread.
     */
    public ObjectContext getChildContext() {
        ObjectContext context = (ObjectContext) threadManager.get(threadContextKey);
        if (context == null ) {
            if (context instanceof DataContext) {
                context = ((DataContext)context).createChildDataContext();
                threadManager.put(threadContextKey,context);
            } else {
                throw new UnsupportedOperationException("Cannot create a child context from ObjectContext other than DataContext! (Base support not added until after cayenne 3.0M5)");
            }
        }
        return context;
    }
}