/*
 * Created on Apr 4, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.*;
import com.googlecode.tapestry5cayenne.services.CayenneEntityEncoder;
import com.googlecode.tapestry5cayenne.services.NonPersistedObjectStorer;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import com.googlecode.tapestry5cayenne.services.TapestryCayenneModule;

@Test(groups="all")
public class TestCayenneEntityEncoder extends Assert {
    
    private CayenneEntityEncoder _encoder;
    private ObjectContextProvider _provider;
    private Registry _registry;

    @BeforeClass
    void setupDB() throws Exception {
        TestUtils.setupdb();
        _registry = TestUtils.setupRegistry("App0",TapestryCayenneModule.class);
        _provider = _registry.getService(ObjectContextProvider.class);
        _encoder = new CayenneEntityEncoder(
                _provider,
                _registry.getService(TypeCoercer.class),
                _registry.getService(NonPersistedObjectStorer.class));
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
        ObjectContext ctxt = _provider.currentContext();
        Artist a = _provider.currentContext().newObject(Artist.class);
        a.setName("test");
        StringPKEntity stringPk = ctxt.newObject(StringPKEntity.class);
        TinyIntPKEntity tinyPk = ctxt.newObject(TinyIntPKEntity.class);
        //have to explicitly set the id here because cayenne wants
        //to start id numbering at 200, and 200 is already too big for tinyPk.
        tinyPk.setObjectId(new ObjectId("TinyIntPKEntity","id",1));
        SmallIntPKEntity smallPk = ctxt.newObject(SmallIntPKEntity.class);
        BigIntPKEntity bigPk = ctxt.newObject(BigIntPKEntity.class);
        //set this to be something bigger than Integer.class can handle
        //(unsigned) int is just over 4 billion, so, 5 billion.
        bigPk.setObjectId(new ObjectId("BigIntPKEntity","id",5000000000L));
        stringPk.setId("testingstrings");
        _provider.currentContext().commitChanges();
        Artist a2 = new Artist();
        Artist a3 = _provider.currentContext().newObject(Artist.class);
        
        return new Object[][] {
                //Null object handling.
                {null,"nil"},
                //transient object handling
                {a2,"Artist::" + "t::" + a2.hashCode()},
                //"new" object handling.
                {a3, "Artist::" + "t::" + a3.hashCode()},
                //committed object handling, int pk.
                {a,"Artist::" + DataObjectUtils.intPKForObject(a)},
                //committed object handling non-numeric pk
                {stringPk ,"StringPKEntity::testingstrings"},
                //committed object, non INTEGER pks...
                {tinyPk,"TinyIntPKEntity::" + DataObjectUtils.intPKForObject(tinyPk)},
                {smallPk,"SmallIntPKEntity::" + DataObjectUtils.intPKForObject(smallPk)},
                {bigPk,"BigIntPKEntity::" + DataObjectUtils.longPKForObject(bigPk)},
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
    
    @Test
    void testDoubleConvertNewObject() {
        Artist a = _provider.currentContext().newObject(Artist.class);
        a.setName("TestArtist");
        String clientString = _encoder.toClient(a);
        assertEquals(a,_encoder.toValue(clientString));
        assertEquals(a,_encoder.toValue(clientString));
    }
    
    @DataProvider(name="strings")
    Object[][] strings() {
        return new Object[][] {
                {"" },
                {" "},
                {"\t"},
        };
    }
    
    @Test(dataProvider="strings")
    void testEmptyStringHandling(String string) {
        assertNull(_encoder.toValue(string));
    }
}