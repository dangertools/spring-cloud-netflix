package org.springframework.cloud.netflix.zuul;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.zuul.http.ZuulServlet;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableConfigurationProperties(ZuulProxyProperties.class)
@ConditionalOnClass(ZuulServlet.class)
@ConditionalOnExpression("${zuul.proxy.enabled:true}")
public class ZuulProxyConfiguration extends AbstractZuulConfiguration {

    @Bean
    public ZuulProxyProperties zuulProxyProperties() {
        return new ZuulProxyProperties();
    }

    @Bean
    //ZuulProxyProperties doesn't implement ZuulProperties so there are not 2 implementations (see ZuulServerConfiguration
    public ZuulProperties zuulProperties() {
        return new ZuulProperties() {
            @Override
            public String getMapping() {
                return zuulProxyProperties().getMapping();
            }

            @Override
            public boolean isStripMapping() {
                return zuulProxyProperties().isStripMapping();
            }

            @Override
            public String getRoutePrefix() {
                return zuulProxyProperties().getRoutePrefix();
            }

            @Override
            public boolean isAddProxyHeaders() {
                return zuulProxyProperties().isAddProxyHeaders();
            }
        };
    }

    @Override
    protected ZuulProperties getProperties() {
        return zuulProperties();
    }
}
