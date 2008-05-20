package org.tapestrycayenne.integration.app0.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.tapestrycayenne.model.Painting;

public class TestToOneControl {
    
    private Painting _painting;
    
    @Component(parameters={
            "object=painting"
    })
    private BeanEditForm _form;
    
    public Painting getPainting() {
        return _painting;
    }
    
    public void setPainting(Painting p) {
        _painting = p;
    }
}
