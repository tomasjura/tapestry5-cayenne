package org.tapestrycayenne.components;

import java.util.Collection;

import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tapestrycayenne.internal.Labeler;


@IncludeStylesheet("ToManyViewer.css")
public class ToManyViewer {
    
    /**
     * Threshold at which generic text (x associated items) is displayed,
     * rather than listing the individual elements.
     */
    private static final int SIZE_LIMIT=20;
    
    @Property
    private Object _tmp;
    
    @Parameter
    @Property
    private Collection _source;
    
    @Inject
    private Messages _messages;
    
    public boolean isSmallEnough() {
        return _source.size() < SIZE_LIMIT;
    }
    
    public String getStringFromTmp() {
        return Labeler.htmlLabelForObject(_tmp);
    }
    
    public String getToManyString() throws Exception {
        return _messages.format("associated_items",_source.size());
    }
}
