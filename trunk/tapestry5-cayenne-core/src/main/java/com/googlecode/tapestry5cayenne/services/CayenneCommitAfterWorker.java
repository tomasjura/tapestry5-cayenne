package com.googlecode.tapestry5cayenne.services;

import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.services.ClassTransformation;
import org.apache.tapestry5.services.ComponentClassTransformWorker;
import org.apache.tapestry5.services.ComponentMethodAdvice;
import org.apache.tapestry5.services.ComponentMethodInvocation;
import org.apache.tapestry5.services.TransformMethodSignature;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;

/**
 * Exactly analogous to the tapestry-hibernate CommitAfterWorker.
 *
 */
public class CayenneCommitAfterWorker implements ComponentClassTransformWorker {
    
    private final ObjectContextProvider provider;
    
    private final ComponentMethodAdvice advice = new ComponentMethodAdvice() {

        public void advise(ComponentMethodInvocation invocation) {
            
            try {
                
                invocation.proceed();
                provider.currentContext().commitChanges();
                
            } catch (RuntimeException e) {
                
                provider.currentContext().rollbackChanges();
                throw e;
                
            }
            
        }
        
    };

    public void transform(ClassTransformation transformation,
            MutableComponentModel model) {
        for(TransformMethodSignature sig: transformation.findMethodsWithAnnotation(CommitAfter.class)) {
            transformation.advise(sig, advice);
        }
    }
    
    public CayenneCommitAfterWorker(ObjectContextProvider provider) {
        this.provider = provider;
    }

}
