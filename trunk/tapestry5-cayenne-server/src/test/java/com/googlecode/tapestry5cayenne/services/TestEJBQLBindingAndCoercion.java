package com.googlecode.tapestry5cayenne.services;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingFactory;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;

@Test
public class TestEJBQLBindingAndCoercion extends Assert {
    
    private Registry registry;
    private List<Artist> data;
    private ObjectContext context;
    
    @BeforeClass
    void setupDB() throws Exception {
        TestUtils.setupdb();
        registry = TestUtils.setupRegistry("App0", TapestryCayenneModule.class);
        context = registry.getService(ObjectContextProvider.class).newContext();
        data = TestUtils.basicData(context);
        new Ordering(Artist.NAME_PROPERTY,true).orderList(data);
    }
    
    public void testBindingFactory() {
        BindingFactory fact =  registry.getService("EJBQLBindingFactory",BindingFactory.class);
        ComponentResources mockResources = mockResources();
        Binding binding = fact.newBinding("testbinding", mockResources, null, "select a from Artist a order by a.name", null);
        assertEquals(binding.getBindingType(),EJBQLQuery.class);
        Object o = binding.get();
        assertTrue(o instanceof EJBQLQuery);
        List<Artist> ret = context.performQuery((EJBQLQuery)o);
        assertArtists(ret);
        EasyMock.verify(mockResources);
    }
    
    public void testQueryToListAndGridDataSourceCoercions() {
        TypeCoercer coercer = registry.getService(TypeCoercer.class);
        Binding b = registry.getService("EJBQLBindingFactory",BindingFactory.class)
                            .newBinding("test", mockResources(), null, "select a from Artist a order by a.name", null);
        
        EJBQLQuery q = (EJBQLQuery) b.get();
        
        List ret = coercer.coerce(q, List.class);
        
        assertArtists(ret);
        
        GridDataSource ds = coercer.coerce(q,GridDataSource.class);
        
        assertEquals(ds.getAvailableRows(),data.size());
        for(int i=0;i<data.size();i++) {
            assertEquals(((Artist)ds.getRowValue(i)).getObjectId(),data.get(i).getObjectId());
        }
        
    }
    
    private ComponentResources mockResources() {
        ComponentResources mockResources = EasyMock.createMock(ComponentResources.class);
        EasyMock.expect(mockResources.getCompleteId()).andReturn("TestComp");
        EasyMock.replay(mockResources);
        return mockResources;
    }
    
    private void assertArtists(List<Artist> actual) {
        assertEquals(actual.size(),data.size());
        for(int i=0;i<data.size();i++) {
            assertEquals(actual.get(i).getObjectId(),data.get(i).getObjectId());
        }
    }

}
