package cz.martyn.cloud.number;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@EnableDiscoveryClient
@SpringBootApplication
public class FibonacciApplication {

    public static void main(String[] args) {
        SpringApplication.run(FibonacciApplication.class, args);
    }

}

@RestController
final class FibonacciController {

    private final LoadingCache<Integer, BigInteger> CACHE
            = CacheBuilder.newBuilder().build(CacheLoader.from(this::computeFibonacci));

    @RequestMapping("/fibonacci/{n}")
    public BigInteger fibonacci(@PathVariable int n) {
        return computeFibonacci(n);
    }

    private BigInteger computeFibonacci(int n) {
        Preconditions.checkArgument(n >= 0, "number must be >= 0");
        switch (n) {
            case 0:
                return BigInteger.ZERO;
            case 1:
                return BigInteger.ONE;
            default:
                return CACHE.getUnchecked(n - 1).add(CACHE.getUnchecked(n - 2));
        }
    }

}

@RestController
class ServiceInstanceRestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

}