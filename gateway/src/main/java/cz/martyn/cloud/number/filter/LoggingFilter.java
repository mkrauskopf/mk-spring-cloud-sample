package cz.martyn.cloud.number.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * Filter logging information about request going via Zuul.
 */
public final class LoggingFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public String filterType() {
        return "post";
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
        LOG.info("{} {} (remote: {}:{})\n" +
                        " - response status: {}",
                req.getMethod(), req.getRequestURL(), req.getRemoteAddr(), req.getRemotePort(),
                ctx.getResponseStatusCode());
        return null;
    }

}
