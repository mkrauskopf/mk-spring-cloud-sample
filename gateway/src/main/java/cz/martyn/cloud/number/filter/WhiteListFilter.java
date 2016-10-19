package cz.martyn.cloud.number.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * White list filter.
 * <p>
 * Created by mkalinovits on 10/18/16.
 */
public class WhiteListFilter extends ZuulFilter {

    private static final String REGISTRY_EUREKA_APPS = "/registry/eureka/apps/";
    private static final Logger LOG = LoggerFactory.getLogger(WhiteListFilter.class);

    private final WhiteList whitelist;

    @Autowired
    public WhiteListFilter(final WhiteList whiteList) {
        this.whitelist = whiteList;
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
        final RequestContext ctx = RequestContext.getCurrentContext();
        final String uri = ctx.getRequest().getRequestURI();
        if (doAimRegistry(uri) && !isOnWhitelist(uri)) {
            ctx.setSendZuulResponse(false);
            LOG.info("Request from " + uri + " is blocked.");
        }
        return null;
    }

    private boolean doAimRegistry(final String uri) {
        return uri.contains(REGISTRY_EUREKA_APPS);
    }

    private boolean isOnWhitelist(final String uri) {
        boolean result = false;
        if (uri.split(REGISTRY_EUREKA_APPS).length > 1) {
            String toBeRegistered = uri.split(REGISTRY_EUREKA_APPS)[1];
            String serviceName = toBeRegistered.split("/")[0];
            result = whitelist.contains(serviceName);
        }
        return result;
    }

}
