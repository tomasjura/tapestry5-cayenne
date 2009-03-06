package com.googlecode.tapestry5cayenne.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.services.BeanEditContext;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.ValidationConstraintGenerator;

public class CayenneConstraintGenerator implements
        ValidationConstraintGenerator {
    
    private final ObjectContextProvider provider;
    private Environment environment;
    
    public CayenneConstraintGenerator(
            final Environment environment,
            final ObjectContextProvider provider) {
        this.provider = provider;
        this.environment = environment;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> buildConstraints(Class propertyType, AnnotationProvider provider) {
        BeanEditContext bec = environment.peek(BeanEditContext.class);
        PropertyEditContext pec = environment.peek(PropertyEditContext.class);
        
        if (bec == null || pec == null) {//not much we can do...
            return null;
        }
        
        Class<?> beanType = bec.getBeanClass();
        EntityResolver er = this.provider.currentContext().getEntityResolver();
        ObjEntity oent = er.lookupObjEntity(beanType);
        if (oent == null) {
            return null;
        }
        
        DbAttribute dbatt = extractDbAttribute(pec.getPropertyId(), oent);
        if (dbatt == null) {
            return null;
        }
        List<String> ret = new ArrayList<String>();
        if (dbatt.isMandatory()) {
            ret.add("required");
        }
        //little tricky here. If it's a string, we add a maxlength validator, otherwise, we ignore it, for now.
        //a guess at a max bounds.
        if (dbatt.getMaxLength() > 0 && CharSequence.class.isAssignableFrom(propertyType)) {
            ret.add(String.format("maxlength=%d",dbatt.getMaxLength()));
        }
        if (ret.isEmpty()) {
            return null;
        }
        return ret;
    }
    
    private DbAttribute extractDbAttribute(String propId, ObjEntity oent) {
        ObjAttribute oatt = (ObjAttribute) oent.getAttribute(propId);
        if (oatt == null) {
            ObjRelationship rel = (ObjRelationship) oent.getRelationship(propId);
            if (rel == null || rel.isToMany() || rel.getDbRelationships().isEmpty()) {
		        return null;
            }
            
            DbRelationship dbrel = rel.getDbRelationships().get(0);
            //assume only one
            if (dbrel.getSourceAttributes().isEmpty()) {
                return null;
            }
            return dbrel.getSourceAttributes().iterator().next();
        }
        return oatt.getDbAttribute();
        
    }

}
