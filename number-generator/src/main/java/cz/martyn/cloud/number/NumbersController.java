package cz.martyn.cloud.number;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class NumbersController {

    @Autowired
    private FibonacciClient fibClient;

    @Autowired
    private PrimesClient primesClient;

    @RequestMapping("/nThFibonacci/{n}")
    public BigInteger nThFibonacci(@PathVariable BigInteger n) {
        return fibClient.sendMessage(n);
    }

    @RequestMapping("/nThPrime/{n}")
    public BigInteger nThPrime(@PathVariable BigInteger n) {
        return primesClient.sendMessage(n);
    }

}

@FeignClient(value = "fibonacci", fallback = FibonacciClientFallback.class)
interface FibonacciClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/fibonacci/{nTh}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BigInteger sendMessage(@PathVariable("nTh") BigInteger nTh);

}

@Component
class FibonacciClientFallback implements FibonacciClient {

    @Override
    public BigInteger sendMessage(@PathVariable("nTh") BigInteger nTh) {
        // This is indeed, very clever fallback value ;) But OK for presentation purpose.
        return BigInteger.valueOf(42);
    }

}

@FeignClient("primes")
interface PrimesClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/prime/{nTh}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BigInteger sendMessage(@PathVariable("nTh") BigInteger nTh);

}
