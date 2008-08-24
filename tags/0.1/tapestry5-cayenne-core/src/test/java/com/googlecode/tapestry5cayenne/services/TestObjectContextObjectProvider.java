package com.googlecode.tapestry5cayenne.services;

import java.lang.annotation.Annotation;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.ContextType;
import com.googlecode.tapestry5cayenne.annotations.OCType;

@Test(groups="all")
public class TestObjectContextObjectProvider extends Assert {
    
    private ObjectContextObjectProvider p;
    private AnnotationProvider ap;
    private ObjectContextProvider provider;
    
    @BeforeMethod
    public void setup() {
        provider = EasyMock.createMock(ObjectContextProvider.class);
        p = new ObjectContextObjectProvider(provider);
        ap = EasyMock.createMock(AnnotationProvider.class);
    }
    
    @AfterMethod
    void verify() {
        EasyMock.verify(ap,provider);
    }
    
    void replay() {
        EasyMock.replay(ap,provider);
    }
    
    public void testProvide_NonOCClass() {
        replay();
        assertNull(p.provide(DataTypeAnalyzer.class, ap, null));
    }
    
    public void testProvide_OCClass_NoAnnotation_ReturnsCurrentContext() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);
        
        EasyMock.expect(ap.getAnnotation(OCType.class)).andReturn(null);
        EasyMock.expect(provider.currentContext()).andReturn(mock);
        
        replay();
        assertEquals(p.provide(ObjectContext.class, ap, null),mock);
    }
    
    public void testProvide_AnnotationCurrent_ReturnsCurrent() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);
        
        EasyMock.expect(ap.getAnnotation(OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.CURRENT;
                    }
                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                    
                } );
        EasyMock.expect(provider.currentContext()).andReturn(mock);
        replay();
        assertEquals(p.provide(ObjectContext.class,ap,null),mock);
    }
    
    public void testProvide_AnnotationNew_ReturnsNew() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);
        
        EasyMock.expect(ap.getAnnotation(OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.NEW;
                    }

                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                } );
        EasyMock.expect(provider.newContext()).andReturn(mock);
        replay();
        assertEquals(p.provide(ObjectContext.class,ap,null),mock);
    }

    public void testProvide_AnnotationChild_childsupported_returnschild() {
        MockDataContext mdc = new MockDataContext();
        
        EasyMock.expect(ap.getAnnotation(OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.CHILD;
                    }
                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                });
        EasyMock.expect(provider.currentContext()).andReturn(mdc);
        replay();
        assertEquals(p.provide(ObjectContext.class,ap,null),mdc);
        assertTrue(mdc.createChildCalled);
    }
    
    @Test(expectedExceptions=IllegalStateException.class)
    public void testProvide_AnnotationChild_childunspported_throwsexception() {
        ObjectContext oc = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(oc);
        
        EasyMock.expect(ap.getAnnotation(OCType.class)).andReturn(
                new OCType() {
                    public ContextType value() {
                        return ContextType.CHILD;
                    }
                    public Class<? extends Annotation> annotationType() {
                        return OCType.class;
                    }
                });
        EasyMock.expect(provider.currentContext()).andReturn(oc);
        replay();
        p.provide(ObjectContext.class, ap, null);
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