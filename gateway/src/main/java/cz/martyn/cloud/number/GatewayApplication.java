package cz.martyn.cloud.number;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import cz.martyn.cloud.number.filter.WhiteList;
import cz.martyn.cloud.number.filter.WhiteListFilter;

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    private WhiteList whiteList;

    public static void main(final String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public WhiteListFilter simpleFilter() {
        return new WhiteListFilter(whiteList());
    }

    @Bean
    public WhiteList whiteList() {
        if (whiteList == null) {
            whiteList = new WhiteList();
        }
        return whiteList;
    }

}
