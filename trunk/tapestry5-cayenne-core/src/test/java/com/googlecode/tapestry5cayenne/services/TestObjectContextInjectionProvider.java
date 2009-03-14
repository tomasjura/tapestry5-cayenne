package com.googlecode.tapestry5cayenne.services;

import java.lang.annotation.Annotation;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.services.ClassTransformation;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.ContextType;
import com.googlecode.tapestry5cayenne.annotations.OCType;
import com.googlecode.tapestry5cayenne.internal.ObjectContextWrapper;

@Test(groups="all")
public class TestObjectContextInjectionProvider extends Assert {
    
    private ObjectContextInjectionProvider p;
    private ObjectContextProvider provider;
    private PerthreadManager threadManager;
    private PropertyShadowBuilder builder;
    private ClassTransformation transformation;
    
    @BeforeMethod
    public void setup() {
        provider = EasyMock.createMock(ObjectContextProvider.class);
        threadManager = EasyMock.createMock(PerthreadManager.class);
        builder = EasyMock.createMock(PropertyShadowBuilder.class);
        p = new ObjectContextInjectionProvider(provider,threadManager,builder);
        transformation = EasyMock.createMock(ClassTransformation.class);
    }
    
    @AfterMethod
    void verify() {
        EasyMock.verify(transformation,provider,threadManager,builder);
    }
    
    void replay() {
        EasyMock.replay(transformation,provider,threadManager,builder);
    }
    
    public void testProvide_NonOCClass() {
        replay();
        assertFalse(p.provideInjection("analyzer",DataTypeAnalyzer.class, null,transformation, null));
    }
    
    public void testProvide_OCClass_NoAnnotation_ReturnsCurrentContext() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);
        
        EasyMock.expect(transformation.getFieldAnnotation("context",OCType.class)).andReturn(null);
        
        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("currentContext"), EasyMock.eq(ObjectContext.class)))
                
                .andReturn(mock);
        
        transformation.injectField("context", mock);
        
        replay();
                
        assertTrue(p.provideInjection("context",ObjectContext.class, null,transformation, null));
    }
    
    public void testProvide_AnnotationCurrent_ReturnsCurrent() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);
        
        EasyMock.expect(transformation.getFieldAnnotation("context",OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.CURRENT;
                    }
                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                    
                } );
        
        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("currentContext"), EasyMock.eq(ObjectContext.class)))
                .andReturn(mock);
        
        transformation.injectField("context", mock);
        
        replay();
        
        assertTrue(p.provideInjection("context",ObjectContext.class,null,transformation,null));
    }
    
    public void testProvide_AnnotationNew_ReturnsNew() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);
        
        EasyMock.expect(transformation.getFieldAnnotation("context",OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.NEW;
                    }

                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                } );
        
        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("newContext"), EasyMock.eq(ObjectContext.class)))
            .andReturn(mock);
        
        transformation.injectField("context", mock);
        
        replay();
        
        assertTrue(p.provideInjection("context",ObjectContext.class,null,transformation,null));
    }

    public void testProvide_AnnotationChild_childsupported_returnschild() {
        MockDataContext mdc = new MockDataContext();
        
        EasyMock.expect(transformation.getFieldAnnotation("context",OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.CHILD;
                    }
                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                });
        
        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("childContext"), EasyMock.eq(ObjectContext.class)))
                .andReturn(mdc);
        
        transformation.injectField("context", mdc);
        
        replay();
        
        assertTrue(p.provideInjection("context",ObjectContext.class,null,transformation,null));
    }
}

@SuppressWarnings("serial")
class MockDataContext extends DataContext {
    boolean createChildCalled=false;
    
    MockDataContext() {}
    
    @Override
    public DataContext createChildDataContext() {
        createChildCalled=true;
        return this;
    }
}