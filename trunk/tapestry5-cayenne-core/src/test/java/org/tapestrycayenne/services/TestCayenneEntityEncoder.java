/*
 * Created on Apr 4, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.Persistent;
import org.tapestrycayenne.AbstractDBTest;
import org.tapestrycayenne.model.Artist;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class TestCayenneEntityEncoder extends AbstractDBTest {
    
    private CayenneEntityEncoder _encoder;
    private ObjectContextProvider _provider;

    @BeforeTest
    void setupDB() throws Exception {
        AbstractDBTest.setupdb();
        _provider = new DataContextProviderImpl();
        _encoder = new CayenneEntityEncoder(_provider);
    }
    
    @DataProvider(name="conversions")
    private Object[][] conversionValues() {
        Artist a = _provider.currentContext().newObject(Artist.class);
        a.setName("test");
        _provider.currentContext().commitChanges();
        return new Object[][] {
                //TODO add test (and appropriate code) for null handling
                //TODO add test (and appropriate code) for transient object handling
                //TODO add test (and appropriate code) for new object handling
                //TODO might be nice to have a "secure" way of storing objects in the url.
                {a,"Artist::" + DataObjectUtils.intPKForObject(a)},
        };
    }
    
    @Test(dataProvider="conversions")
    void testConversion(Persistent serverVal, String clientVal) {
        String client = _encoder.toClient(serverVal);
        assertEquals("Encoder incorrectly encoded artist value",client,clientVal);
        Persistent server = _encoder.toValue(client);
        assertEquals("Encoder incorrectly converted client value to server value",server,serverVal);
    }
}
