package cz.martyn.cloud.number;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.netflix.zuul.context.RequestContext;

import cz.martyn.cloud.number.filter.WhiteList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RoutesManagerController {

    public static final String REGISTRY_EUREKA_APPS = "/registry/eureka/apps/";
    public static final String SEPARATOR = "/";
    @Autowired
    private RouteLocator locator;
    @Autowired
    private WhiteList whiteList;

    @RequestMapping(value = "/addRoute", method = GET)
    public void addRoute(@RequestParam("name") final String routeName, @RequestParam("targetUrl") final String targetUrl) {
        ((DiscoveryClientRouteLocator) locator).addRoute("/" + routeName + "/**", targetUrl);
    }

    @RequestMapping(value = "/addToWhitelist", method = GET)
    public void addToWhitelist(@RequestParam("name") final String routeName) {
        whiteList.add(routeName);
    }

    @RequestMapping(value = "/removeFromWhitelist", method = RequestMethod.DELETE)
    public void removeFromWhitelist(@RequestParam("appId") final String appId) {
        whiteList.remove(appId);
    }
}
