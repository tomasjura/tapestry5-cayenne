package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.internal.LabelOptionModel;
import com.googlecode.tapestry5cayenne.model.Artist;

@Test(groups="all")
public class TestLabelOptionModel extends Assert {
    
    @SuppressWarnings("unused")
    @DataProvider(name="options")
    private Object[][] options() throws Exception {
        Artist a = new Artist();
        a.setName("Picasso");
        Method m = Artist.class.getMethod("getName");
        return new Object[][] {
                {null,null,""},
                {a,null,a.toString()},
                {a,m,"Picasso"},
                {null,m,""},
        };
    }
    
    @Test(dataProvider="options")
    public void test(Object obj, Method m, String label) {
        LabelOptionModel model = new LabelOptionModel(obj,m);
        assertEquals(model.getLabel(),label);
        assertEquals(model.getValue(),obj);
        assertEquals(model.isDisabled(),false);
        assertEquals(model.getAttributes(),null);
    }
    
}
