/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry.ValueEncoder;
import org.apache.tapestry.ioc.services.TypeCoercer;
import org.apache.tapestry.ioc.annotation.Marker;
import org.tapestrycayenne.annotations.Cayenne;

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
    
    
    @SuppressWarnings("unchecked")
    public CayenneEntityEncoder(
            final ObjectContextProvider provider,
            final TypeCoercer coercer,
            final NonPersistedObjectStorer storer) {
        _provider = provider;
        _coercer = coercer;
        _storer=storer;
    }

    public String toClient(final Persistent dao)
    {
        if (dao == null) {
            return "nil";
        }

        if (dao.getPersistenceState() == PersistenceState.NEW
                || dao.getPersistenceState() == PersistenceState.TRANSIENT) {
            String key = _storer.store(dao);
            //TODO smells of tight coupling here, having to dig through so many layers of objects.
            ObjEntity ent = _provider.currentContext().getEntityResolver().lookupObjEntity(dao.getClass());
            return ent.getName() + "::t::" + key;
            
        }

        final String pk = _coercer.coerce(DataObjectUtils.pkForObject(dao),String.class);
        return dao.getObjectId().getEntityName() + "::" + pk;
    }

    public Persistent toValue(final String val) {
        if (val == null || val.trim().equals("")) { 
            return null;
        }

        final String[] vals = _pattern.split(val);
        if (vals.length < 2)
        {
            if (vals[0].equals("nil")) {
                return null;
            }
            //TODO i18n this
            throw new RuntimeException("Unable to convert " + val + " into a CayenneDataObject");
        }

        if (vals.length == 3) {
            //check to see if it's in storage...
            final Persistent obj = _storer.retrieve(vals[2],vals[0]);
            if (obj == null) { 
                throw new RuntimeException("Unable to convert " + val + " into a CayenneDataObject: missing object");
            }
            return obj; 
        }
        
        final Object pk = _coercer.coerce(vals[1], pkTypeForEntity(vals[0]));
        return (Persistent) 
            DataObjectUtils.objectForPK(_provider.currentContext(), vals[0], pk);
    }
    
    private Class<?> pkTypeForEntity(String entity) {
        final ObjEntity oent = _provider.currentContext().getEntityResolver().getObjEntity(entity);
        final Collection<ObjAttribute> atts = oent.getPrimaryKeys();
        if (atts.size() > 1) {
            throw new RuntimeException("CayenneEntityEncoder can't handle multi-column pks");
        }

        final ObjAttribute attribute = atts.iterator().next(); 
        return attribute.getJavaClass();
    }
}
