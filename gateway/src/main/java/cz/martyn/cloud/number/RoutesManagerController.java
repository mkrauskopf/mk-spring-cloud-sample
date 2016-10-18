package cz.martyn.cloud.number;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.martyn.cloud.number.filter.WhiteList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RoutesManagerController {

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

    @RequestMapping(value = "/removeFromWhitelist", method = GET)
    public void removeFromWhitelist(@RequestParam("name") final String routeName) {
        whiteList.remove(routeName);
        // TODO - remove route
        // TODO - deregister from Eureka
    }

}
