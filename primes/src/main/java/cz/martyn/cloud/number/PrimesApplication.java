package cz.martyn.cloud.number;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class PrimesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrimesApplication.class, args);
    }

}

@RestController
final class PrimesController {

    private final ArrayList<Integer> primes;
    private int counter;

    public PrimesController() {
        primes = new ArrayList<>();
        primes.add(2);
        primes.add(3);
        counter = 4;
    }

    @RequestMapping("/prime/{nTh}")
    public Integer prime(@PathVariable int nTh) {
        return computePrime(nTh);
    }

    // http://stackoverflow.com/a/14742691/526228
    private Integer computePrime(int nTh) {
        while (primes.size() < nTh) {
            if (counter % 2 != 0 && counter % 3 != 0) {
                int temp = 4;
                while (temp * temp <= counter) {
                    if (counter % temp == 0)
                        break;
                    temp++;
                }
                if (temp * temp > counter) {
                    primes.add(counter);
                }
            }
            counter++;
        }

        return primes.get(nTh - 1);
    }

}
