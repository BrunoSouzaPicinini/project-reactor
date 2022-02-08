package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
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

    @Test
    public void fluxSubscriberNumbersUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        log.info("--------------------------");
        flux.subscribe(new Subscriber<>() {
            private int count = 0;
            private Subscription subscription;
            private final int requestCount = 2;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersNotSoUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        log.info("--------------------------");
        flux.subscribe(new BaseSubscriber<>() {
            private int count = 0;
            private final int requestCount = 2;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(requestCount);
            }

            @Override
            protected void hookOnNext(Integer value) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    request(requestCount);
                }
            }
        });
        log.info("--------------------------");

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }


    @Test
    public void fluxSubscriberIntervalOne() throws Exception {
        Flux<Long> interval = Flux.interval(Duration.ofMillis(100))
                .take(10)
                .log();

        interval.subscribe(i -> log.info("Number {}", i));

        Thread.sleep(3000);
    }

    @Test
    public void fluxSubscriberIntervalTwo() throws Exception {
         StepVerifier.withVirtualTime(this::createInterval)
                 .expectSubscription()
                 .expectNoEvent(Duration.ofDays(1))
                 .thenAwait(Duration.ofDays(1))
                 .expectNext(0L)
                 .expectNext(1L)
                 .thenCancel()
                 .verify();
    }

    private Flux<Long> createInterval() {
        return Flux.interval(Duration.ofDays(1))
                .log();
    }
}
