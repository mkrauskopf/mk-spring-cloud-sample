package cz.martyn.cloud.number;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
