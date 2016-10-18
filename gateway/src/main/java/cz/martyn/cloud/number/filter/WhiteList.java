package cz.martyn.cloud.number.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Created by mkalinovits on 10/18/16.
 */
@Component
public class WhiteList {
    private List<String> whitelist;

    public WhiteList() {
        whitelist = new ArrayList<>();
        whitelist.add("delta"); // Internal service
    }

    public void add(final String name) {
        whitelist.add(name);
    }

    public void remove(final String name) {
        whitelist.remove(name);
    }

    public boolean contains(final String name) {
        return whitelist.contains(name);
    }
}
