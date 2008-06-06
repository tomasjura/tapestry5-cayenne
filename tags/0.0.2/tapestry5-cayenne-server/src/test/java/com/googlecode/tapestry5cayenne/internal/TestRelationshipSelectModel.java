package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.OptionModel;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.model.BigIntPKEntity;

@Test(groups="all")
public class TestRelationshipSelectModel extends Assert {
    
    private ObjectContext _context;
    private List<Artist> _data;
    
    @BeforeClass
    void setup() throws Exception {
        TestUtils.setupdb();
        _context = DataContext.getThreadDataContext();
        _data = TestUtils.basicData(_context);
    }

    public void construction() {
        RelationshipSelectModel model = new RelationshipSelectModel(Artist.class,_context);
        assertNull(model.getOptionGroups());
        assertEquals(model.getOptions().size(),_data.size());
        Ordering o = new Ordering("name",true);
        o.orderList(_data);
        Iterator<OptionModel> it = model.getOptions().iterator();
        for(Artist a : _data) {
            assertEquals(it.next().getValue(),a);
        }
    }
    
    @DataProvider(name="sorts")
    public Object[][] sorts() throws Exception {
        return new Object[][] {
                //prop is in model
                {
                    new SelectQuery(Artist.class),
                    Artist.class.getMethod("getName"),
                    Artist.class,
                    new QuerySortResult(QuerySortType.QUERY,new Ordering("name",true))
                },
                //label is a javabeans prop, but not in model
                {
                    new SelectQuery(Artist.class),
                    Artist.class.getMethod("getNumPaintings"),
                    Artist.class,
                    new QuerySortResult(QuerySortType.ORDERING,new Ordering("numPaintings",true))
                },
                {
                    new SelectQuery(Artist.class),
                    Artist.class.getMethod("numPaintings"),
                    Artist.class,
                    new QuerySortResult(QuerySortType.METHOD,null)
                },
                {
                    new SelectQuery(BigIntPKEntity.class),
                    null,
                    BigIntPKEntity.class,
                    new QuerySortResult(QuerySortType.NOSORT,null)
                },
                {
                    new SelectQuery(Artist.class),
                    null,
                    Artist.class,
                    new QuerySortResult(QuerySortType.COMPARABLE,null)
                }
        };
    }
    
    @Test(dataProvider="sorts")
    public void query_sort(SelectQuery sq, Method label, Class<?> type, QuerySortResult expected) {
        QuerySortResult result = RelationshipSelectModel.querySort(sq, label, DataContext.getThreadDataContext(), type);
        assertEquals(result.type,expected.type);
        if (expected.ordering == null) {
            assertNull(result.ordering);
        } else {
            assertEquals(result.ordering.isAscending(),expected.ordering.isAscending());
            assertEquals(result.ordering.getSortSpecString(),expected.ordering.getSortSpecString());
        }
        if (expected.type ==  QuerySortType.QUERY) {
            assertEquals(sq.getOrderings().get(0),result.ordering);
        } else {
            assertEquals(sq.getOrderings().size(),0);
        }
    }
}
