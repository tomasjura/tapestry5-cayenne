package org.tapestrycayenne.internal;

import org.apache.tapestry5.OptionModel;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * An implementation of OptionModel that uses for a label, the results of a method marked with @Label 
 */
public class LabelOptionModel implements OptionModel {
    
    private final String _label;
    private final Object _value;
    
    public LabelOptionModel(Object value, Method label) {
        _value = value;
        _label = Labeler.labelForObject(value,label);
    }

    public Map<String, String> getAttributes() {
        return null;
    }

    public String getLabel() {
        return _label;
    }

    public Object getValue() {
        return _value;
    }

    public boolean isDisabled() {
        return false;
    }
}
