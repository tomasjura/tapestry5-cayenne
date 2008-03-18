/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package org.tapestrycayenne.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for use with Cayenne-related services
 * @author robertz
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER,FIELD})
@Documented
public @interface Cayenne {

}
