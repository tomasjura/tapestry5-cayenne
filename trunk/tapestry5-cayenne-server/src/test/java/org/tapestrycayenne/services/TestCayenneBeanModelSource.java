package org.tapestrycayenne.services;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.BeanModelSource;
import static org.easymock.EasyMock.*;
import org.tapestrycayenne.TestUtils;
import org.tapestrycayenne.model.Artist;
import org.tapestrycayenne.model.Painting;
import org.tapestrycayenne.model.StringPKEntity;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Test(groups="all")
public class TestCayenneBeanModelSource extends Assert {
    
    private Registry _reg;
    private BeanModelSource _source;
    
    @BeforeClass
    void setup() throws Exception {
        TestUtils.setupdb();
        _reg = TestUtils.setupRegistry("App0",TapestryCayenneModule.class);
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
        Map<String,String> artistPropsWithRelationship = new HashMap<String,String>(artistProps);
        artistPropsWithRelationship.put("paintingList", "to_many_list");
        artistPropsWithRelationship.put("paintingsByTitle","to_many_map");
        artistPropsWithRelationship.put("numPaintings","number");
        Map<String,String> paintingProps = new HashMap<String,String>();
        paintingProps.put(Painting.ARTIST_PROPERTY,"to_one");
        paintingProps.put(Painting.PRICE_PROPERTY,"number");
        paintingProps.put(Painting.TITLE_PROPERTY,"text");
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
                    artistPropsWithRelationship,
                },
                {
                    Painting.class,
                    true,
                    paintingProps,
                    
                },
                {
                    Painting.class,
                    false,
                    paintingProps,
                    
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
            assertTrue(names.contains(key),"Model missing property " + key);
            assertEquals(model.get(key).getDataType(),props.get(key),"Property has wrong datatype");
        }
        for(String name : names) {
            assertTrue(props.containsKey(name),"Model contained extraneous property: " + name);
        }
    }
}
