/*
 * Created on Apr 3, 2008
 * 
 * 
 */
package org.tapestrycayenne.integration.app0.services;

import org.apache.cayenne.access.DataContext;
import org.apache.tapestry.Field;
import org.apache.tapestry.MarkupWriter;
import org.apache.tapestry.TapestryConstants;
import org.apache.tapestry.ValidationDecorator;
import org.apache.tapestry.dom.Element;
import org.apache.tapestry.internal.services.PageRenderQueue;
import org.apache.tapestry.ioc.MappedConfiguration;
import org.apache.tapestry.ioc.OrderedConfiguration;
import org.apache.tapestry.ioc.annotations.SubModule;
import org.apache.tapestry.services.ApplicationInitializer;
import org.apache.tapestry.services.ApplicationInitializerFilter;
import org.apache.tapestry.services.Context;
import org.apache.tapestry.services.Environment;
import org.apache.tapestry.services.MarkupRenderer;
import org.apache.tapestry.services.MarkupRendererFilter;
import org.tapestrycayenne.TestUtils;
import org.tapestrycayenne.services.TapestryCayenneModule;

@SubModule(TapestryCayenneModule.class)
public class AppModule {

    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        System.out.println("Contributing my defaults...");
        configuration.add(TapestryConstants.SUPPORTED_LOCALES_SYMBOL, "en");
        configuration.add(TapestryConstants.PRODUCTION_MODE_SYMBOL,"false");
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
