/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import java.util.regex.Pattern;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.services.TypeCoercer;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;

/**
 * Basic Persistent ValueEncoder.
 * This works for objects which implements Persistent. If you're using the POJO
 * facilities of cayenne, you will need to contribute your own value encoders.
 * This ValueEncoder assumes a single-column primary key. If your entity uses a
 * multi-column key, you will need to contribute a custom ValueEncoder for that
 * entity.  If the pk type is something other than "INTEGER", you must have
 * an object attribute corresponding to the primary key's db attribute.
 * @author Robert Zeigler
 */
@Marker(Cayenne.class)
public class CayenneEntityEncoder implements ValueEncoder<Persistent> {
    
    private final ObjectContextProvider _provider;
    private final Pattern _pattern = Pattern.compile("::");
    private final TypeCoercer _coercer;
    private final NonPersistedObjectStorer _storer;
    private final PersistentManager _manager;
    private final EncodedValueEncrypter _encrypter;
    
    public CayenneEntityEncoder(
            final ObjectContextProvider provider,
            final TypeCoercer coercer,
            final PersistentManager manager,
            final NonPersistedObjectStorer storer,
            final EncodedValueEncrypter encrypter) {
        _provider = provider;
        _coercer = coercer;
        _storer=storer;
        _manager = manager;
        _encrypter = encrypter;
    }

    public String toClient(final Persistent dao)
    {
        if (dao == null) {
            return _encrypter.encrypt("nil");
        }

        if (dao.getPersistenceState() == PersistenceState.NEW
                || dao.getPersistenceState() == PersistenceState.TRANSIENT) {
            
            final String key = _storer.store(dao);

            //TODO smells of tight coupling here, having to dig through so many layers of objects.
            ObjEntity ent = _provider.currentContext().getEntityResolver().lookupObjEntity(dao.getClass());
            return _encrypter.encrypt(ent.getName() + "::t::" + key);
        }

        final String pk = _coercer.coerce(DataObjectUtils.pkForObject(dao),String.class);
        return _encrypter.encrypt(dao.getObjectId().getEntityName() + "::" + pk);
    }

    public Persistent toValue(String val) {
        val = _encrypter.decrypt(val);
        if (val == null || val.trim().length() == 0) { 
            return null;
        }

        String[] vals = _pattern.split(val);
        if (vals.length < 2) {
            if (vals[0].equals("nil")) {
                return null;
            }

            //TODO i18n this
            throw new RuntimeException("Unable to convert " + val + " into a Cayenne Persistent object");
        }

        if (vals.length == 3) {
            //check to see if it's in storage...
            Persistent obj = _storer.retrieve(vals[2],vals[0]);
            if (obj == null) { 
                throw new RuntimeException("Unable to convert " + val + " into a Cayenne Persistent object: missing object");
            }

            return obj; 
        }
        
        return _manager.find(vals[0], vals[1]);
    }
}
