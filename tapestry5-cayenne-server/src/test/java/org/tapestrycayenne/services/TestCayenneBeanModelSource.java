package org.tapestrycayenne.services;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.ComponentResources;
import org.apache.tapestry.beaneditor.BeanModel;
import org.apache.tapestry.ioc.Messages;
import org.apache.tapestry.ioc.Registry;
import org.apache.tapestry.services.BeanModelSource;
import org.tapestrycayenne.AbstractDBTest;
import org.tapestrycayenne.model.Artist;
import org.tapestrycayenne.model.StringPKEntity;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class TestCayenneBeanModelSource extends AbstractDBTest {
    
    private Registry _reg;
    private BeanModelSource _source;
    
    @BeforeTest
    void setup() throws Exception {
        AbstractDBTest.setupdb();
        _reg = AbstractDBTest.setupRegistry("App0",TapestryCayenneModule.class);
        _source = _reg.getService("CayenneBeanModelSource", BeanModelSource.class);
    }
    
    @AfterTest
    void shutdown_reg() {
        if(_reg != null) {
            _reg.shutdown();
        }
    }
    
    @DataProvider(name="property_tests")
    Object[][] propertyTests() {
        Map<String,String> stringPKProps = new HashMap<String, String>();
        stringPKProps.put(StringPKEntity.ID_PROPERTY,"text");
        stringPKProps.put(StringPKEntity.INT_PROP1_PROPERTY,"number");
        Map<String,String> artistProps = new HashMap<String, String>();
        artistProps.put(Artist.NAME_PROPERTY, "text");
        return new Object[][] {
                {
                    StringPKEntity.class,
                    true,
                    stringPKProps
                },
                {
                    StringPKEntity.class,
                    false,
                    stringPKProps
                },
                {
                    Artist.class,
                    true,
                    artistProps
                },
                {
                    Artist.class,
                    false,
                    artistProps
                }
        };
    }
    
    @Test(dataProvider="property_tests")
    public void test_properties(Class<?> type,boolean filterReadable,Map<String,String> props) {
        //ensure all properties specified are included.
        Messages msgs = createMock(Messages.class);
        ComponentResources res = createMock(ComponentResources.class);
        expect(res.getMessages()).andReturn(msgs);
        
        expect(msgs.contains((String)anyObject())).andReturn(false).anyTimes();
        replay(res,msgs);
        BeanModel<?> model = _source.create(type, filterReadable, res);
        List<String> names = model.getPropertyNames();
        for(String key : props.keySet()) {
            assertTrue(names.contains(key));
            assertEquals(model.get(key).getDataType(),props.get(key));
        }
        for(String name : names) {
            assertTrue(props.containsKey(name),"Model contained extraneous property: " + name);
        }
    }
}
