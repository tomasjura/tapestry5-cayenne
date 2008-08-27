package com.googlecode.tapestry5cayenne.model.auto;

import org.apache.cayenne.CayenneDataObject;

import com.googlecode.tapestry5cayenne.model.Artist;

/**
 * Class _Painting was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Painting extends CayenneDataObject {

    public static final String PRICE_PROPERTY = "price";
    public static final String TITLE_PROPERTY = "title";
    public static final String ARTIST_PROPERTY = "artist";

    public static final String ID_PK_COLUMN = "id";

    public void setPrice(Double price) {
        writeProperty("price", price);
    }
    public Double getPrice() {
        return (Double)readProperty("price");
    }

    public void setTitle(String title) {
        writeProperty("title", title);
    }
    public String getTitle() {
        return (String)readProperty("title");
    }

    public void setArtist(Artist artist) {
        setToOneTarget("artist", artist, true);
    }

    public Artist getArtist() {
        return (Artist)readProperty("artist");
    }


}