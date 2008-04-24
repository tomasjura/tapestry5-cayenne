package org.tapestrycayenne.services;

import org.apache.cayenne.Persistent;

public interface NonPersistedObjectStorer {
    
    /**
     * Stores a non-persisted persistent object for later retrieval.
     * @return a String key for the dao.
     */
    String store(Persistent dao);
    
    /**
     * Retrieves the non-persisted persistent object from storage.
     * @param key the key as returned from store
     * @param objEntityName the name of the object entity.
     */
    Persistent retrieve(String key,String objEntityName);

}
