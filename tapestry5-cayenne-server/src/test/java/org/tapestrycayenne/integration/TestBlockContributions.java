package org.tapestrycayenne.integration;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.test.PageTester;
import org.tapestrycayenne.TestUtils;
import org.tapestrycayenne.model.Artist;
import org.tapestrycayenne.services.ObjectContextProvider;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Test(groups="all")
public class TestBlockContributions extends Assert {
    
    private Registry _registry;
    private PageTester _tester;
    private ObjectContextProvider _provider;
    private ValueEncoder<Persistent> _encoder;
    private List<Artist> _data;

    @SuppressWarnings("unchecked")
    @BeforeClass
    void setup() throws Exception {
        TestUtils.setupdb();
        _tester = new PageTester("org.tapestrycayenne.integration.app0","app","src/test/app0");
        _registry = _tester.getRegistry();
        _provider = _registry.getService(ObjectContextProvider.class);
        _data = TestUtils.basicData(_provider.currentContext());
        _encoder = _registry.getService("CayenneEntityEncoder", ValueEncoder.class);
    }
    
    @AfterTest
    void shutdown() {
        if (_tester != null) {
            _tester.shutdown();
        }
    }
    
    @Test
    public void testToOne() {
        Document doc = _tester.renderPage("TestToOneControl");
        
        //Verify the label
        Element el = doc.getElementById("toOneList:label");
        assertEquals(el.getChildMarkup(),"Artist");
        
        //Verify the select list.
        el = doc.getElementById("toOneList");
        assertEquals(el.getChildren().size()-1,_data.size());
        
        //we expect the list of items to be sorted by the @Label.
        Collections.sort(_data,new Comparator<Artist>() {
            public int compare(Artist o1, Artist o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Iterator<Node> children = el.getChildren().iterator();
        //skip the first node: it's blank.
        children.next();
        for(Artist a : _data) {
            Element option = (Element) children.next();
            String val = option.getAttribute("value");
            Persistent obj = _encoder.toValue(val);
            assertEquals(obj,a,"Incorrect order of persistent objects!");
        }
    }
    
    
}
