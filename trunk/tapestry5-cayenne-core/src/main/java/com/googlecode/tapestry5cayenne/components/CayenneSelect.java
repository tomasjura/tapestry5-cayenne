package com.googlecode.tapestry5cayenne.components;

import com.googlecode.tapestry5cayenne.internal.RelationshipSelectModel;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Displays a selection list for Cayenne persistent objects.  Designed to
 * be used inside of custom forms.
 *
 * @author Kevin Menard
 */
public class CayenneSelect
{
    @Inject
    private ComponentResources resources;

    @Inject
    private ObjectContextProvider provider;

    @Property
    @Parameter(required = true)
    private Persistent value;

    @Property
    @Parameter
    private String label;

    public SelectModel getModel()
    {
        return new RelationshipSelectModel(resources.getBoundType("value"), provider.currentContext());
    }
}
