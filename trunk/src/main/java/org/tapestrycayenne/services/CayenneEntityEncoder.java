/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.services;

import java.util.regex.Pattern;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.tapestry.ValueEncoder;

/**
 * Basic CayenneDataObject ValueEncoder.
 * This works for objects which extends CayenneDataObject.  
 * If you're using the POJO facilities of cayenne, you will need to contribute your own value encoders.
 * This ValueEncoder also assumes that the primary key of your object is a single int column.
 * @author Robert Zeigler
 *
 */
public class CayenneEntityEncoder implements ValueEncoder<CayenneDataObject> {
    
    private final ObjectContextProvider _provider;
    
    public CayenneEntityEncoder(final ObjectContextProvider provider) {
        _provider = provider;
    }
    
    private Pattern _pattern = Pattern.compile("::");

    public String toClient(CayenneDataObject dao) {
        return dao.getObjectId().getEntityName() + "::" + 
            Integer.toString(DataObjectUtils.intPKForObject(dao));
    }

    public CayenneDataObject toValue(String val) {
        String[] vals = _pattern.split(val);
        if (vals.length != 2) {
            //TODO i18n this
            throw new RuntimeException("Unable to convert " + val + " into a CayenneDataObject");
        }
        return (CayenneDataObject) 
            DataObjectUtils.objectForPK(
                    _provider.currentContext(),
                    vals[0],
                    Integer.parseInt(vals[1]));
    }


}
