package cz.martyn.cloud.number;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RoutesManagerController {

    @Autowired
    private RouteLocator locator;

    @RequestMapping(value = "/addRoute", method = GET)
    public void addRoute(@RequestParam("name") String routeName, @RequestParam("targetUrl") String targetUrl) {
        ((DiscoveryClientRouteLocator) locator).addRoute("/" + routeName + "/**", targetUrl);
    }

}
