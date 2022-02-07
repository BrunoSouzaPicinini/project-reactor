package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {

    @Test
    public void fluxSubscriber() {
        String[] names = {"Bruno", "Amanda", "José", "Maria"};
        Flux<String> fluxString = Flux.just(names)
                .log();

        StepVerifier.create(fluxString)
                .expectNext(names)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers() {
        Integer[] number = {1, 2, 3, 4, 5};
        Flux<Integer> flux = Flux.just(number)
                .log();

        log.info("--------------------------");
        flux.subscribe(i -> log.info("Number:{}", i),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                subscription -> subscription.request(2));
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(number)
                .verifyComplete();
    }
}
