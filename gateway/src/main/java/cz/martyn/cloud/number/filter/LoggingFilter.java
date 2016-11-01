package cz.martyn.cloud.number.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * Filter logging information about requests going via Zuul.
 */
public final class LoggingFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        try {
            String responseBody = getResponseBody(ctx);
            ctx.setResponseBody(responseBody);
            LOG.info("{} {} (remote: {}:{})\n" +
                            " - response status: {}\n" +
                            " - body: {}",
                    req.getMethod(), req.getRequestURL(), req.getRemoteAddr(), req.getRemotePort(),
                    ctx.getResponseStatusCode(), responseBody);
        } catch (IOException e) {
            LOG.error("Error during logging traffic", e);
        }
        return null;
    }

    // based on https://stackoverflow.com/questions/30558408/rewrite-internal-eureka-based-links-to-external-links-in-zuul-proxy/30660922#30660922
    private String getResponseBody(RequestContext context) throws IOException {
        String responseData = null;
        if (context.getResponseBody() != null) {
            context.getResponse().setCharacterEncoding("UTF-8");
            responseData = context.getResponseBody();
        } else if (context.getResponseDataStream() != null) {
            context.getResponse().setCharacterEncoding("UTF-8");
            try (final InputStream responseDataStream = context.getResponseDataStream()) {
                // FIXME What about character encoding of the stream (depends on the response content type)?
                responseData = CharStreams.toString(new InputStreamReader(responseDataStream));
            }
        }
        return responseData;
    }

}
