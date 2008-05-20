/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne.integration.app0.services;

import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValidationDecorator;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.services.PageRenderQueue;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.*;
import org.tapestrycayenne.TestUtils;
import org.tapestrycayenne.services.TapestryCayenneModule;

@SubModule(TapestryCayenneModule.class)
public class AppModule {

    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        configuration.add(SymbolConstants.PRODUCTION_MODE,"false");
    }
    
    public static void contributeApplicationInitializer(OrderedConfiguration<ApplicationInitializerFilter> conf) {
        conf.add("setupdb", new ApplicationInitializerFilter() {

            public void initializeApplication(Context context, ApplicationInitializer handler) {
                try {
                    TestUtils.setupdb();
                    DataContext dc = DataContext.getThreadDataContext();
                    TestUtils.basicData(dc);
                } catch (Exception e) { throw new RuntimeException(e); }
                //put in some artists and paintings...
                handler.initializeApplication(context);
            }
            
        });
    }
    
    public static void contributeMarkupRenderer(
            final OrderedConfiguration<MarkupRendererFilter> filter,
            final Environment environment,
            final PageRenderQueue rq) {
        
        MarkupRendererFilter override = new MarkupRendererFilter() {
            public void renderMarkup(MarkupWriter writer, MarkupRenderer renderer) {
                ValidationDecorator noop = new ValidationDecorator() {
                    public void afterField(Field arg0) {}
                    public void afterLabel(Field arg0) {}
                    public void beforeField(Field arg0) {}
                    public void beforeLabel(Field arg0) {}
                    public void insideField(Field arg0) {}
                    public void insideLabel(Field arg0, Element arg1) {}
                };
                environment.push(ValidationDecorator.class,noop);
                renderer.renderMarkup(writer);
                environment.pop(ValidationDecorator.class);
            }
        };
        filter.add("NoopValidationDecorator", override, "after:DefaultValidationDecorator");
    }


}
