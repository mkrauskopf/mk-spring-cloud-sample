package cz.martyn.cloud.number;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class NumberGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NumberGeneratorApplication.class, args);
    }

}

@RestController
final class FibonacciController {

    @Autowired
    private FibonacciClient fibClient;

    @RequestMapping("/nThFibonacci/{n}")
    public BigInteger nThFibonacci(@PathVariable BigInteger n) {
        return fibClient.sendMessage(n);
    }

}

@FeignClient("fibonacci")
interface FibonacciClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/fibonacci/{n}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BigInteger sendMessage(@PathVariable("n") BigInteger n);

}

