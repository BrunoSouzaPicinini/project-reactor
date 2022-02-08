package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

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
    public void fluxSubscriberLimitedNumbers() {
        Integer[] numbers = {1, 2, 3, 4, 5};
        Flux<Integer> flux = Flux.just(numbers)
                .log();

        log.info("--------------------------");
        flux.subscribe(i -> log.info("Number:{}", i),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                subscription -> subscription.request(2));
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(numbers)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers() {
        Flux<Integer> flux = Flux.range(1, 5)
                .log();

        log.info("--------------------------");
        flux.subscribe(i -> log.info("Number {}", i));
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberFromList() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Flux<Integer> flux = Flux.fromIterable(numbers)
                .log();

        log.info("--------------------------");
        flux.subscribe(i -> log.info("Number:{}", i));
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(numbers.toArray(Integer[]::new))
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersEror() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Flux<Integer> flux = Flux.fromIterable(numbers)
                .log()
                .map(i -> {
                    if (i == 4) {
                        throw new IndexOutOfBoundsException("index error");
                    }
                    return i;
                });

        log.info("--------------------------");
        flux.subscribe(i -> log.info("Number:{}", i),
                Throwable::printStackTrace,
                () -> log.info("DONE!"));
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }

}
