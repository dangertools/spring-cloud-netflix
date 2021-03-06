package org.springframework.cloud.netflix.zuul;

import com.netflix.zuul.context.ContextLifecycleFilter;
import com.netflix.zuul.http.ZuulServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.DebugFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Spencer Gibb
 */
public abstract class AbstractZuulConfiguration {

    @Autowired(required=false)
    private TraceRepository traces;

    protected abstract ZuulProperties getProperties();

    @Bean
    public Routes routes() {
        return new Routes(getProperties().getRoutePrefix());
    }

    @Bean
    public ServletRegistrationBean zuulServlet() {
        return new ServletRegistrationBean(new ZuulServlet(), getProperties().getMapping()+"/*");
    }

    @Bean
    public FilterRegistrationBean contextLifecycleFilter() {
        Collection<String> urlPatterns = new ArrayList<>();
        urlPatterns.add(getProperties().getMapping()+"/*");

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new ContextLifecycleFilter());
        filterRegistrationBean.setUrlPatterns(urlPatterns);

        return filterRegistrationBean;
    }

    @Bean
    public FilterInitializer zuulFilterInitializer() {
        return new FilterInitializer();
    }

    // pre filters
    @Bean
    public DebugFilter debugFilter() {
        return new DebugFilter();
    }

    @Bean
    public PreDecorationFilter preDecorationFilter() {
        return new PreDecorationFilter();
    }

    // route filters
    @Bean
    public RibbonRoutingFilter ribbonRoutingFilter() {
        RibbonRoutingFilter filter = new RibbonRoutingFilter();
        if (traces!=null) {
            filter.setTraces(traces);
        }
        return filter;
    }

    // post filters
    @Bean
    public SendResponseFilter sendResponseFilter() {
        return new SendResponseFilter();
    }

}
