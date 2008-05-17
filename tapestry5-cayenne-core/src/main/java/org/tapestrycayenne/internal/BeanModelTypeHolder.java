package org.tapestrycayenne.internal;

public class BeanModelTypeHolder {
    
    private final Class<?> _type;
    
    public BeanModelTypeHolder(final Class<?> type) {
        _type = type;
    }
    
    public Class<?> getType() { 
        return _type;
    }
}
