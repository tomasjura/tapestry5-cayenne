/*
 * Created on Apr 4, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.Persistent;
import org.apache.tapestry.ioc.Registry;
import org.apache.tapestry.ioc.services.TypeCoercer;
import org.tapestrycayenne.AbstractDBTest;
import org.tapestrycayenne.model.Artist;
import org.tapestrycayenne.model.StringPKEntity;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class TestCayenneEntityEncoder extends AbstractDBTest {
    
    private CayenneEntityEncoder _encoder;
    private ObjectContextProvider _provider;
    private Registry _registry;

    @BeforeTest
    void setupDB() throws Exception {
        AbstractDBTest.setupdb();
        _registry = AbstractDBTest.setupRegistry("App0",TapestryCayenneModule.class);
        _provider = _registry.getService(ObjectContextProvider.class);
        _encoder = new CayenneEntityEncoder(_provider,_registry.getService(TypeCoercer.class));
    }
    
    @AfterTest
    void shutdownRegistry() {
        if (_registry != null) {
            _registry.shutdown();
        }
    }
    
    @SuppressWarnings("unused")
    @DataProvider(name="conversions")
    private Object[][] conversionValues() {
        Artist a = _provider.currentContext().newObject(Artist.class);
        a.setName("test");
        StringPKEntity spke = _provider.currentContext().newObject(StringPKEntity.class);
        spke.setId("testingstrings");
        _provider.currentContext().commitChanges();
        Artist a2 = new Artist();
        Artist a3 = _provider.currentContext().newObject(Artist.class);
        
        return new Object[][] {
                //Null object handling.
                {null,"nil"},
                //transient object handling
                {a2,"Artist::" + "h" + a2.hashCode()},
                //"new" object handling.
                {a3, "Artist::" + "h" + a3.hashCode()},
                //committed object handling, int pk.
                {a,"Artist::" + DataObjectUtils.intPKForObject(a)},
                //committed object handling non-numeric pk
                {spke,"StringPKEntity::testingstrings"}
                //TODO might be nice to have a way to store objs in the url in a "tamper-proof" fashion.
                //at least as an option.
        };
    }
    
    @Test(dataProvider="conversions")
    void testConversion(Persistent serverVal, String clientVal) {
        String client = _encoder.toClient(serverVal);
        assertEquals(client,clientVal,"Encoder incorrectly encoded artist value");
        Persistent server = _encoder.toValue(client);
        assertEquals(server,serverVal,"Encoder incorrectly converted client value to server value");
    }
}
