package com.googlecode.tapestry5cayenne.integration;

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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.model.Painting;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

@Test(groups="all",sequential=true)
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
    
    @AfterClass
    void shutdown() {
        if (_tester != null) {
            _tester.shutdown();
        }
    }
    
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
    
    /** tests the "to_one" viewer; also tests editor submission.
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
    
    private Document assertToManyHead() {
        Document doc = _tester.renderPage("TestToManyControl");
        //make sure the stylesheet shows up.
        List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(), "head/link");
        //should be 2: one for tapestry, one for t5cayenne
        assertEquals(els.size(),2);
        assertTrue(els.get(1).getAttribute("href").contains("ToManyViewer.css"));
        //ok... make sure we have the right thing on the bean display...
        return doc;
    }
    
    
    public void test_tomany_viewer_few_elements() {
        assertEquals(_data.get(0).getName(),"Dali");
        Document doc = assertToManyHead();
        List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(),"body/div/div/div/ul");
        //one for the paintingList property, and one for the paintings as a map property.
        assertEquals(els.size(),2);
        assertEquals(els.get(0).getChildren().size(),_data.get(0).getPaintingList().size());
        Iterator it = _data.get(0).getPaintingList().iterator();
        for(Node n : els.get(0).getChildren()) {
            //should be a li...
            Element el = (Element) n;
            assertEquals(el.getName(),"li");
            StringBuilder blder = new StringBuilder();
            doc.getMarkupModel().encode(it.next().toString(), blder);
            assertEquals(el.getChildMarkup().trim(),blder.toString());
        }
        //now test the map...
        it = _data.get(0).getPaintingsByTitle().keySet().iterator();
        for(Node n : els.get(1).getChildren()) {
            Element el = (Element) n;
            assertEquals(el.getName(),"li");
            assertEquals(el.getChildMarkup().trim(),it.next().toString());
        }
    }
    
    /**
     * Test what happens with lots of paintings. Currently, it should "kick over" to generic descriptive text at 20 paintings.
     */
    public void test_tomany_viewer_many_elements() {
        assertEquals(_data.get(0).getName(),"Dali");
        List<Painting> paintings = TestUtils.addPaintings(_data.get(0), 18, _provider.currentContext());
        for(Painting p : paintings) {
            _data.get(0).addToPaintingList(p);
        }
        Document doc = assertToManyHead();
        //no ul to grab anymore...
        List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(), "body/div/div/div");
        assertEquals(els.get(1).getChildMarkup().trim(),"20 associated items");
        assertEquals(els.get(3).getChildMarkup().trim(),"20 associated items");
    }
    
}
