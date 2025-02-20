package com.learning.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void test_sinks_many() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        //when
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then

        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 1 :: " + i);
        });

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 :: " + i);
        });

        replaySink.tryEmitNext(3);

        Flux<Integer> integerFlux3 = replaySink.asFlux();
        integerFlux3.subscribe((i) -> {
            System.out.println("Subscriber 3 :: " + i);
        });
    }

    @Test
    void test_sinks_multicast() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().multicast().onBackpressureBuffer();
        //when
        replaySink.tryEmitNext(1);
        replaySink.tryEmitNext(2);

        //then
        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 1 :: " + i);
        });

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 :: " + i);
        });

        replaySink.tryEmitNext(3);
    }

    @Test
    void test_sinks_unicast() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().unicast().onBackpressureBuffer();
        //when
        replaySink.tryEmitNext(1);
        replaySink.tryEmitNext(2);

        //then
        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe((i) -> System.out.println("Subscriber 1 :: " + i));


        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe((i) -> {
            System.out.println("Subscriber 2 :: " + i);
        });

        replaySink.tryEmitNext(3);
    }
}
