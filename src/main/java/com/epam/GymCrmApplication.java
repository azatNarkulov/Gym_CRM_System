package com.epam;

import com.epam.config.AppConfig;
import com.epam.config.HibernateConfig;
import com.epam.config.WebConfig;
import com.epam.filter.AuthenticationFilter;
import com.epam.filter.RestLoggingFilter;
import com.epam.filter.TransactionIdFilter;
import com.epam.service.UserService;
import jakarta.persistence.EntityManagerFactory;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class GymCrmApplication {

    private static final int PORT = 8080;

    static void main() throws Exception {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class, HibernateConfig.class);
        rootContext.refresh();

        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(WebConfig.class);
        dispatcherContext.setParent(rootContext);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        UserService userService = rootContext.getBean(UserService.class);

        String emfBeanName = rootContext.getBeanNamesForType(EntityManagerFactory.class)[0];

        OpenEntityManagerInViewFilter osivFilter = new OpenEntityManagerInViewFilter();
        osivFilter.setEntityManagerFactoryBeanName(emfBeanName);

        FilterDef osiv = new FilterDef();
        osiv.setFilterName("openEntityManagerInView");
        osiv.setFilter(osivFilter);
        ctx.addFilterDef(osiv);

        FilterMap osivMap = new FilterMap();
        osivMap.setFilterName("openEntityManagerInView");
        osivMap.addURLPattern("/api/*");
        ctx.addFilterMap(osivMap);

        registerFilter(ctx, "transactionIdFilter", new TransactionIdFilter());
        registerFilter(ctx, "restLoggingFilter", new RestLoggingFilter());
        registerFilter(ctx, "authenticationFilter", new AuthenticationFilter(userService));

        DispatcherServlet dispatcherServlet = new DispatcherServlet(dispatcherContext);
        Wrapper wrapper = Tomcat.addServlet(ctx, "dispatcher", dispatcherServlet);
        wrapper.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/", "dispatcher");

        ctx.getServletContext().setAttribute(
                org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                rootContext);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static void registerFilter(Context ctx, String name, jakarta.servlet.Filter filter) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(name);
        filterDef.setFilter(filter);
        ctx.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(name);
        filterMap.addURLPattern("/api/*");
        ctx.addFilterMap(filterMap);
    }
}
