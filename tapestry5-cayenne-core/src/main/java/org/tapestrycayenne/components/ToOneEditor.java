package org.tapestrycayenne.components;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.tapestry.Field;
import org.apache.tapestry.FieldValidator;
import org.apache.tapestry.SelectModel;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Environmental;
import org.apache.tapestry.corelib.components.Select;
import org.apache.tapestry.ioc.annotations.Inject;
import org.apache.tapestry.services.PropertyEditContext;
import org.tapestrycayenne.internal.RelationshipSelectModel;
import org.tapestrycayenne.services.ObjectContextProvider;

public class ToOneEditor implements Field {
    
    @Environmental
    private PropertyEditContext _context;
    
    @Inject
    private ObjectContextProvider _provider;
    
    @SuppressWarnings("unused")
    @Component(parameters={
            "model=model",
            "value=value",
            "validate=prop:validation"
    })
    private Select _toOneList;

    
    public Persistent getValue() {
        return (Persistent) _context.getPropertyValue();
    }
    
    public void setValue(Persistent value) {
        _context.setPropertyValue(value);
    }
    
    public SelectModel getModel() {
        Class type = _context.getPropertyType();
        ObjectContext ctxt = _provider.currentContext();
        return new RelationshipSelectModel(type,ctxt);
    }
    
    public FieldValidator<?> getValidation() {
        return _context.getValidator(_toOneList);
    }

    public String getControlName() {
        return _toOneList.getControlName();
    }

    public String getLabel() {
        return _context.getLabel();
    }

    public boolean isDisabled() {
        return _toOneList.isDisabled();
    }

    public boolean isRequired() {
        return _toOneList.isRequired();
    }

    public String getClientId() {
        return _toOneList.getClientId();
    }
}
