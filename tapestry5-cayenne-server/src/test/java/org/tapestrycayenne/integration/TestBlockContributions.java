package org.tapestrycayenne.integration;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.query.Ordering;
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
        new Ordering(Artist.NAME_PROPERTY,true).orderList(_data);
        _encoder = _registry.getService("CayenneEntityEncoder", ValueEncoder.class);
    }
    
    @AfterTest
    void shutdown() {
        if (_tester != null) {
            _tester.shutdown();
        }
    }
    
    @Test
    public void testToOneEditor() {
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
    
    @Test
    /** tests the "to_one" viewer; note that it also (re)tests the editor.
     * Have to submit the form at the moment as there's no easy way to setup a
     * page and then render that instance of that page. Or if there is, I don't
     * know it.
     */
    public void testToOneViewer() {
        //render the document, select the artist, 
        //submit, then check the view.
        Document doc = _tester.renderPage("TestToOneControl");
        List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(),"body/form/div/div/input");
        assertFalse(els.isEmpty());
        Element submit = els.get(2);
        
        Map<String,String> params = new HashMap<String, String>();
        params.put("price", "100.0");
        params.put("title","dud");
        params.put("toOneList",_encoder.toClient(_data.get(1)));
        doc = _tester.clickSubmit(submit, params);
        
        //make sure that the select is correctly selected.
        els = TestUtils.DOMFindAll(doc.getRootElement(),"body/form/div/div/select/option");
        assertFalse(els.isEmpty());
        //find the option corresponding to _data.get(1).
        assertTrue(els.get(2).getAttribute("selected").equals("selected"));
        assertTrue(els.get(2).getChildMarkup().equals("Picasso"));
        
        //make sure the output is correct.
        els = TestUtils.DOMFindAll(doc.getRootElement(),"body/div/div/div");
        assertEquals(els.get(4).getChildMarkup(),"Artist:");
        assertEquals(els.get(5).getChildMarkup(),"Picasso");
    }
    
}
