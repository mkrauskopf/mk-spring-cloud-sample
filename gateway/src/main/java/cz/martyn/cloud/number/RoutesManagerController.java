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

    private static final String REGISTRY_EUREKA_APPS = "/registry/eureka/apps/";
    private static final String SEPARATOR = "/";
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
        whiteList.remove(appId.toLowerCase());  // Standardizing input according to application names are lowercase strings by convention.
    }

    /**
     * Just for Proof-of-concept purposes.
     *
     * @param appId - Application ID in discovery service
     * @param instanceName - instance name in discovery service
     */
    @RequestMapping(value = "/removeApp", method = RequestMethod.DELETE)
    public void removeApplication(@RequestParam("appId") final String appId, @RequestParam("instance") final String instanceName, final HttpServletRequest request) {
        removeFromWhitelist(appId);
        removingApplicationInstanceFromEureka(appId, instanceName, request);
    }

    private void removingApplicationInstanceFromEureka(final String appId, final String instanceName, final HttpServletRequest request) {
        String url = getUrl(request);
        RestOperations restOperations = new RestTemplate();
        restOperations.delete(url + REGISTRY_EUREKA_APPS + appId + SEPARATOR + instanceName);
    }

    private String getUrl(final HttpServletRequest request) {
        StringBuilder s = new StringBuilder();
        s.
                append(request.getScheme()).
                append("://").
                append(request.getServerName());
        if (request.getServerPort() != 80) {
            s.
                    append(":").
                    append(request.getServerPort());
        }
        return s.toString();
    }

}
