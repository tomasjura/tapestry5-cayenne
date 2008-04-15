package org.tapestrycayenne.annotations;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for use with Cayenne ROP client-related services.
 *
 * @author Kevin Menard
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER,FIELD})
@Documented
public @interface CayenneClient {
}
