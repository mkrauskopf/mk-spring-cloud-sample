package cz.martyn.cloud.number;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import cz.martyn.cloud.number.filter.LoggingFilter;
import cz.martyn.cloud.number.filter.WhiteList;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RoutesManagerController {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String REGISTRY_EUREKA_APPS = "/registry/eureka/apps/";
    private static final String SEPARATOR = "/";

    @Autowired
    private RouteLocator locator;

    @Autowired
    private WhiteList whiteList;

    @RequestMapping(value = "/addRoute", method = GET)
    public void addRoute(@RequestParam("name") final String routeName,
                         @RequestParam("targetUrl") final String targetUrl) {
        ((DiscoveryClientRouteLocator) locator).addRoute("/" + routeName + "/**", targetUrl);
    }

    @RequestMapping(value = "/addToWhitelist", method = GET)
    public void addToWhitelist(@RequestParam("appId") final String appId) {
        whiteList.add(appId);
        LOG.info("Application '{}' added to whitelist", appId);
    }

    @RequestMapping(value = "/removeFromWhitelist", method = RequestMethod.DELETE)
    public void removeFromWhitelist(@RequestParam("appId") final String appId) {
        // Standardizing input according to application names are lowercase strings by convention.
        whiteList.remove(appId.toLowerCase());
        LOG.info("Application '{}' removed from whitelist", appId);
    }

    /**
     * Just for proof-of-concept purposes.
     * <p>
     * Deregisters application from Application Registry and removes it from whitelist.
     * </p>
     *
     * @param appId - Application ID in discovery service
     * @param instanceName - instance name in discovery service
     */
    @RequestMapping(value = "/removeApp", method = RequestMethod.DELETE)
    public void removeApplication(@RequestParam("appId") final String appId,
                                  @RequestParam("instance") final String instanceName,
                                  final HttpServletRequest request) {
        removeFromWhitelist(appId);
        removingApplicationInstanceFromEureka(appId, instanceName, request);
    }

    private void removingApplicationInstanceFromEureka(
            final String appId, final String instanceName, final HttpServletRequest request) {
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
