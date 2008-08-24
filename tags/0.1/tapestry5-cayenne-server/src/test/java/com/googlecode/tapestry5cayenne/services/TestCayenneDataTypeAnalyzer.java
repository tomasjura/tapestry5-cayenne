package com.googlecode.tapestry5cayenne.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.ClassPropertyAdapter;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.apache.tapestry5.services.Environment;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.internal.BeanModelTypeHolder;
import com.googlecode.tapestry5cayenne.model.Artist;

@Test(groups="all")
public class TestCayenneDataTypeAnalyzer extends Assert {
    
    private Registry _reg;
    private DataTypeAnalyzer _analyzer;
    
    @BeforeClass
    void setup() throws Exception {
        TestUtils.setupdb();
        _reg = TestUtils.setupRegistry("App0",TapestryCayenneModule.class,TestModule.class);
        _analyzer = _reg.getService("CayenneDataTypeAnalyzer", DataTypeAnalyzer.class);
    }
    
    public void test_for_nonobjentity_types() {
        _reg.getService(Environment.class).push(BeanModelTypeHolder.class,new BeanModelTypeHolder(SomePOJO.class));
        try {
        assertNull(_analyzer.identifyDataType(new PropertyAdapter() {
                public Object get(Object instance) { return null; }
                public Class getBeanType() { return SomePOJO.class; }
                public ClassPropertyAdapter getClassAdapter() { return null; }
                public String getName() { return "artist"; }
                public Method getReadMethod() { return null; }
                public Class getType() { return Artist.class; }
                public Method getWriteMethod() { return null; }
                public boolean isCastRequired() { return false; }
                public boolean isRead() { return false; }
                public boolean isUpdate() { return false; }
                public void set(Object instance, Object value) { }
                public <T extends Annotation> T getAnnotation(Class<T> annotationClass) { return null; }
                }));
        } catch (NullPointerException e) {
            fail("Should not have thrown a NPE analyzing the property: artist in bean class SomePOJO.",e);
        }
    }

}

class SomePOJO {
    public Artist getArtist() {
        return new Artist();
    }
}
