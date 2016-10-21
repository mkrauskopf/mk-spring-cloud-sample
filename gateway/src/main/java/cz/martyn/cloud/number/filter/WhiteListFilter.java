package cz.martyn.cloud.number.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * Filter which let pass calls to application registry service only by services on whitelist.
 */
public class WhiteListFilter extends ZuulFilter {

    // TODO: do not hard-code 'registry' application id.
    private static final String REGISTRY_REST_CALL_PREFIX = "/registry/eureka/apps/";

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
        if (isCallToRegistryService(ctx, uri) && !canUseRegistryService(uri)) {
            ctx.setSendZuulResponse(false);
            LOG.info("Request '{}' from {}:{} is blocked", uri, req.getRemoteHost(), req.getRemotePort());
        }
        return null;
    }

    private boolean isCallToRegistryService(final RequestContext ctx, final String uri) {
        return uri.contains(REGISTRY_REST_CALL_PREFIX)
                && (RequestMethod.POST.toString().equals(ctx.getRequest().getMethod())
                    || RequestMethod.PUT.toString().equals(ctx.getRequest().getMethod()));
    }

    private boolean canUseRegistryService(final String uri) {
        boolean result = false;
        if (uri.split(REGISTRY_REST_CALL_PREFIX).length == 0) {
            result = true;
        } else if (uri.split(REGISTRY_REST_CALL_PREFIX).length > 1) {
            String registryCallSuffix = uri.split(REGISTRY_REST_CALL_PREFIX)[1];
            String appId = registryCallSuffix.split("/")[0];
            result = whitelist.contains(appId);
        }
        return result;
    }

}
