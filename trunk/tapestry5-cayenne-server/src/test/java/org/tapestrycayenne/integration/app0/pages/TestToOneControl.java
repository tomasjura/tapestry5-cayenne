package org.tapestrycayenne.integration.app0.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.corelib.components.BeanDisplay;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.tapestrycayenne.model.Painting;

public class TestToOneControl {
    
    @Persist
    private Painting _painting;
    
    @SuppressWarnings("unused")
    @Component(parameters={
            "object=painting"
    })
    private BeanEditForm _form;
    
    @SuppressWarnings("unused")
    @Component(parameters={
           "object=painting" 
    })
    private BeanDisplay _display;
    
    
    
    public Painting getPainting() {
        return _painting;
    }
    
    public void setPainting(Painting p) {
        _painting = p;
    }
}
