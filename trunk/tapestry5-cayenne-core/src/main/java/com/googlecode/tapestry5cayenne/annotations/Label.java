package com.googlecode.tapestry5cayenne.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a particular method as the "label" for the object type.
 * The label is used when rendering selects for objects, as a short description for the object, etc.
 * @author robertz
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Label {
}
