package com.googlecode.tapestry5cayenne.integration.app0.pages;

import java.sql.SQLException;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

public class CommitAfterTestPage {
    
    @Inject
    private ObjectContext context;
    
    @Inject
    private PersistentManager manager;
    
    @Property
    @Persist
    private Artist artist;
    
    @SetupRender
    public void setupRender() {
        if (artist == null) {
	        List<Artist> artists = manager.listAll(Artist.class);
	        if (artists.isEmpty()) {
	            artist = context.newObject(Artist.class);
	            artist.setName("Dali");
	            context.commitChanges();
	        } else {
	            artist = artists.get(0);
	        }
        }
    }
    
    @CommitAfter
    void onActionFromCommitOk() {
        artist.setName("commitokname");
    }

    @CommitAfter
    void doActionFromRuntimeException() {
        artist.setName("savefailsname");
        throw new RuntimeException("ignore");
    }
    
    void onActionFromRuntimeException() {
        try {
            doActionFromRuntimeException();
        } catch (RuntimeException e) {
            //Ignore
        }
    }
    
    @CommitAfter
    void doActionFromCheckedException() throws SQLException {
        artist.setName("savesokwithcheckedexceptionname");
        throw new SQLException("blah");
    }
    
    void onActionFromCheckedException() {
        try {
            doActionFromCheckedException();
        } catch (SQLException e) {
            //Ignore
        }
    }
}
