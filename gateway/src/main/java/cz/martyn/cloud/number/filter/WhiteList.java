package cz.martyn.cloud.number.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Whitelist externalized configuration.
 */
@ConfigurationProperties(prefix = "whitelist")
@Component
public class WhiteList {

    private List<String> servicesNames = new ArrayList<>();

    public List<String> getServicesNames() {
        return servicesNames;
    }

    public void add(final String name) {
        servicesNames.add(name);
    }

    public void remove(final String name) {
        servicesNames.remove(name);
    }

    public boolean contains(final String name) {
        return servicesNames.contains(name.toLowerCase());
    }

}
