package cz.martyn.cloud.number.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * White list filter.
 *
 * Created by mkalinovits on 10/18/16.
 */
public class WhiteListFilter extends ZuulFilter {
    private static final String REGISTRY_EUREKA_APPS = "/registry/eureka/apps/";
    private static List<String> whitelist;
    private static Logger log = LoggerFactory.getLogger(WhiteListFilter.class);

    static {
        whitelist = new ArrayList<>();
        whitelist.add("delta");
        whitelist.add("FIBONACCI");
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();
        if (uri.contains(REGISTRY_EUREKA_APPS) && uri.split(REGISTRY_EUREKA_APPS).length > 1 && !whitelist.contains(uri.split(REGISTRY_EUREKA_APPS)[1])) {
            log.info("Registration request from " + uri);
            ctx.setSendZuulResponse(false);
//            throw new RuntimeException("Code: " + 403); //optional
        }

        return null;
    }
}
