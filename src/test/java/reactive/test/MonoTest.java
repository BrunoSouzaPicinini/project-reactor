package reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
/* Reactive Streams
  1. Asynchronous
  2. Non-blocking
  3. Backpressure
  Publisher <- (subscribe) Subscriber
  Subscription is created
  Publisher (onSubscribe with the subscription) -> Subscriber
  Subscription <- (request N) Subscriber
  Publisher -> (onNext) Subscriber
  until:
  1. Publisher sends all the objects requested.
  2. Publisher sends all the objects it has. (onComplete) subscriber and subscription will be canceled
  3. There is an error. (anError) -> subscriber and subscription will be canceled
 */
public class MonoTest {

    @Test
    public void monoSubscriber() {
        String name = "Bruno";

        Mono<String> mono = Mono.just(name)
                .log();

        mono.subscribe();

        log.info("------------------------");
        StepVerifier.create(mono)
                .expectNext("Bruno")
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Bruno";
        Mono<String> mono = Mono.just(name);

        mono.subscribe(s -> log.info("Value is:{}", s));
        log.info("------------------------");

        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();

    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "Bruno";
        Mono<String> mono = Mono.just(name)
                .map(s -> {throw new RuntimeException("Testing mono with error");});

        mono.subscribe(s -> log.info("Value is:{}", s), s -> log.error("Something bad happened"));
        mono.subscribe(s -> log.info("Value is:{}", s), Throwable::printStackTrace);
        log.info("------------------------");

        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();

    }
}
