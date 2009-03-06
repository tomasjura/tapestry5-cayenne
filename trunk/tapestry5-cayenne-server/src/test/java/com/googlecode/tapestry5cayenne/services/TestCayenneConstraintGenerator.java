package com.googlecode.tapestry5cayenne.services;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.BeanEditContext;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.ValidationConstraintGenerator;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.model.Bid;
import com.googlecode.tapestry5cayenne.model.Painting;
import com.googlecode.tapestry5cayenne.model.StringPKEntity;

@Test
public class TestCayenneConstraintGenerator extends Assert {
    
    private ValidationConstraintGenerator gen;
    private PropertyEditContext propCtxt;
    private Registry registry;
    private BeanEditContext bec;
    
    @BeforeClass
    void setup() throws Exception {
        TestUtils.setupdb();
        registry = TestUtils.setupRegistry("app0", TapestryCayenneModule.class);
    }
    
    @BeforeMethod
    void initMocks() {
        propCtxt = EasyMock.createMock(PropertyEditContext.class);
        bec = EasyMock.createMock(BeanEditContext.class);
        Environment e = registry.getService(Environment.class);
        e.push(PropertyEditContext.class, propCtxt);
        e.push(BeanEditContext.class, bec);
        
        gen = registry.getService("CayenneConstraintGenerator",ValidationConstraintGenerator.class);
    }
    
    void replay() {
        EasyMock.replay(propCtxt,bec);
    }
    
    @AfterMethod
    void verify() {
        EasyMock.verify(propCtxt,bec);
    }
    
    public void test_non_DO_returns_null() {
        EasyMock.expect(bec.getBeanClass()).andStubReturn(String.class);
        replay();
        assertNull(gen.buildConstraints(byte[].class, new NullAnnotationProvider()));
    }
    
    public void test_noconstraints_returns_null() {
        EasyMock.expect(bec.getBeanClass()).andStubReturn(StringPKEntity.class);
        EasyMock.expect(propCtxt.getPropertyId()).andReturn(StringPKEntity.INT_PROP1_PROPERTY);
        replay();
        assertNull(gen.buildConstraints(int.class, new NullAnnotationProvider()));
    }
    
    public void test_mandatory_property_returns_required() {
        EasyMock.expect(bec.getBeanClass()).andStubReturn(Bid.class);
        EasyMock.expect(propCtxt.getPropertyId()).andReturn(Bid.AMOUNT_PROPERTY);
        replay();
        assertEquals(gen.buildConstraints(String.class, new NullAnnotationProvider()),
                Arrays.asList("required"));
    }
    
    public void test_mandatory_relationship_returns_required() {
        
        EasyMock.expect(bec.getBeanClass()).andStubReturn(Bid.class);
        EasyMock.expect(propCtxt.getPropertyId()).andReturn(Bid.PAINTING_PROPERTY);
        replay();
        assertEquals(gen.buildConstraints(Painting.class, new NullAnnotationProvider()),
                Arrays.asList("required"));
    }
    
    public void test_tomany_relationship_returns_nothing() {
        EasyMock.expect(bec.getBeanClass()).andStubReturn(Artist.class);
        EasyMock.expect(propCtxt.getPropertyId()).andReturn(Artist.PAINTING_LIST_PROPERTY);
        replay();
        assertNull(gen.buildConstraints(List.class, new NullAnnotationProvider()));
    }
    
    public void test_maxlength_property() {
        EasyMock.expect(bec.getBeanClass()).andStubReturn(StringPKEntity.class);
        EasyMock.expect(propCtxt.getPropertyId()).andReturn(StringPKEntity.STRING_PROP2_PROPERTY);
        replay();
        assertEquals(gen.buildConstraints(String.class, new NullAnnotationProvider()),
                Arrays.asList("maxlength=64"));
    }
    
    public void test_maxlength_and_required() {
        EasyMock.expect(bec.getBeanClass()).andStubReturn(Painting.class);
        EasyMock.expect(propCtxt.getPropertyId()).andReturn(Painting.TITLE_PROPERTY);
        replay();
        
        assertEquals(gen.buildConstraints(String.class, new NullAnnotationProvider()),
                Arrays.asList("required","maxlength=1024"));
    }
}

class NullAnnotationProvider implements AnnotationProvider {
    public <T extends Annotation> T getAnnotation(Class<T> arg0) {
        return null;
    }
}