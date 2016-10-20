package cz.martyn.cloud.number.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

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
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        String uri = req.getRequestURI();
        boolean doRegister = doAimRegistry(ctx, uri);
        boolean isOnWhitelist = isOnWhitelist(uri);
        if (doRegister && !isOnWhitelist) {
            ctx.setSendZuulResponse(false);
            LOG.info("Request '{}' from {}:{} is blocked", uri, req.getRemoteHost(), req.getRemotePort());
        }
        return null;
    }

    private boolean doAimRegistry(final RequestContext ctx, final String uri) {
        return uri.contains(REGISTRY_EUREKA_APPS)
                && (RequestMethod.POST.toString().equals(ctx.getRequest().getMethod())
                    || RequestMethod.PUT.toString().equals(ctx.getRequest().getMethod()));
    }

    private boolean isOnWhitelist(final String uri) {
        boolean result = false;
        if (uri.split(REGISTRY_EUREKA_APPS).length == 0) {
            result = true;
        } else if (uri.split(REGISTRY_EUREKA_APPS).length > 1) {
            String s = uri.split(REGISTRY_EUREKA_APPS)[1];
            result = whitelist.contains(s.split("/")[0]);
        }
        return result;
    }

}