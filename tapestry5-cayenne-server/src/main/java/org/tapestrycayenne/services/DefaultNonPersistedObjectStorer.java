package org.tapestrycayenne.services;

import java.util.Map;

import org.apache.cayenne.Persistent;
import org.apache.commons.collections.map.LRUMap;

/**
 * Simple implementation of NonPersistedObjectStorer.
 * Keeps an application-wide (LRU) map of transient objects.
 * Keys off the hashcode of the object.
 * @author robertz
 */
public class DefaultNonPersistedObjectStorer implements NonPersistedObjectStorer {
    
    private final Map<String,Persistent> _objs;
    
    @SuppressWarnings("unchecked")
    public DefaultNonPersistedObjectStorer() {
        //TODO Make map limit configurable.
        _objs = new LRUMap(500);
    }

    public String store(Persistent dao) {
        //TODO make each invocation of store with the same dao return a unique 
        //key to ensure the _objs.remove won't have adverse effects (eg: multiple ajax calls
        //attempting to retrieve the same object, without storing it again, would be bad right now.
        String key = Integer.toString(dao.hashCode());
        _objs.put(key, dao);
        return key;
    }

    public Persistent retrieve(String key,String objEntityName) {
        return _objs.remove(key);
    }
    
}
