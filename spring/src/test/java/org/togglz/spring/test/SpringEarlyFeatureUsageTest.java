package org.togglz.spring.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.togglz.test.Deployments;

@RunWith(Arquillian.class)
public class SpringEarlyFeatureUsageTest {

    @Deployment
    public static WebArchive createDeployment() {
        return Deployments.getBasicWebArchive()
                .addAsLibrary(Deployments.getTogglzSpringArchive())
                .addAsLibraries(
                        DependencyResolvers.use(MavenDependencyResolver.class)
                                .artifact("org.springframework:spring-web:3.0.7.RELEASE")
                                .resolveAs(JavaArchive.class))
                .addAsWebInfResource("applicationContext.xml")
                .setWebXML("spring-web.xml")                
                .addClass(SpringFeatureConfiguration.class)
                .addClass(BasicFeatures.class)
                .addClass(SpringEarlyFeatureUsageService.class);
    }

    @Test
    public void testEarlyFeatureUsage() throws IOException {

        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        SpringEarlyFeatureUsageService service = applicationContext.getBean(SpringEarlyFeatureUsageService.class);

        assertEquals(false, service.isFeature1Active());
        assertEquals(false, service.isFeature2Active());

    }

}
