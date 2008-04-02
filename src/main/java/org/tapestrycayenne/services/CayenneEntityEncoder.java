/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.Persistent;
import org.apache.tapestry.ValueEncoder;

import java.util.regex.Pattern;

/**
 * Basic CayenneDataObject ValueEncoder.
 * This works for objects which implements Persistent.  
 * If you're using the POJO facilities of cayenne, you will need to contribute your own value encoders.
 * This ValueEncoder also assumes that the primary key of your object is a single int column.
 *
 * @author Robert Zeigler
 */
public class CayenneEntityEncoder implements ValueEncoder<Persistent> {
    
    private final ObjectContextProvider _provider;
    private Pattern _pattern = Pattern.compile("::");
    
    public CayenneEntityEncoder(final ObjectContextProvider provider) {
        _provider = provider;
    }

    // TODO null handling
    public String toClient(final Persistent dao)
    {
        return dao.getObjectId().getEntityName() + "::" + 
            Integer.toString(DataObjectUtils.intPKForObject(dao));
    }

    // TODO null handling
    public Persistent toValue(final String val) {
        String[] vals = _pattern.split(val);

        if (vals.length != 2)
        {
            //TODO i18n this
            throw new RuntimeException("Unable to convert " + val + " into a CayenneDataObject");
        }

        return (Persistent) 
            DataObjectUtils.objectForPK(_provider.currentContext(), vals[0], Integer.parseInt(vals[1]));
    }
}
