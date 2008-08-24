package com.googlecode.tapestry5cayenne.components;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.internal.PersistentEntitySelectModel;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

/**
 * Displays a selection list for Cayenne persistent objects.  Designed to
 * be used inside of custom forms.
 *
 * @author Kevin Menard
 */
public class Select
{
    @Inject
    private ComponentResources resources;

    @Inject
    private PersistentManager manager;

    @Property
    @Parameter(required = true)
    @SuppressWarnings("unused")
    private Persistent value;

    @Property
    @Parameter
    @SuppressWarnings("unused")
    private String label;

    public SelectModel getModel()
    {
        return new PersistentEntitySelectModel(resources.getBoundType("value"), manager);
    }
}
