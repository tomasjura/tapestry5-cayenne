package org.tapestrycayenne.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationFinder {

    public static Method methodForAnnotation(Class<? extends Annotation> query, Class<?> type) {
        for(Method m : type.getMethods()) {
            if (m.getAnnotation(query) != null) {
                return m;
            }
        }
        return null;
    }

    public static Field fieldForAnnotation(Class<? extends Annotation> query, Class<?> type) {
        for(Field f : type.getDeclaredFields()) {
            if (f.getAnnotation(query) != null) {
                return f;
            }
        }
        return null;
    }

}
