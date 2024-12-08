package com.learning.reactiveprogrammingusingreactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {


    Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "jack", "sam"));
    }

    Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .map(String::toUpperCase);
    }

    Flux<String> namesFlux_map_filter(int stringLength) {
        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .map(name -> name.length() + "-" + name);
    }


    Flux<String> namesFlux_flatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitString);
    }

    Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    Flux<String> namesFlux_concatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> splitStringWithDelay(String name) {
        var split = name.split("");
        var random = new Random().nextInt(1000);
        return Flux
                .fromArray(split)
                .delayElements(Duration.ofMillis(random));
    }

    public Flux<String> splitString(String name) {
        String[] split = name.split("");
        return Flux.fromArray(split);
    }


    Flux<String> namesFluxImmutability() {
        var names = Flux.fromIterable(List.of("alex", "jack", "sam"));
        names.map(String::toUpperCase);
        return names;
    }

    Mono<String> namesMono() {
        return Mono.just("alex");
    }

    Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength);
    }

    Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        return Mono.just(List.of(s.split("")));
    }

    Flux<String> namesFlux_transform(int stringLength) {
        Function<Flux<String>, Flux<String>> filtermap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .transform(filtermap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filtermap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default")
                .transform(filtermap);

        return Flux.fromIterable(List.of("alex", "jack", "sam"))
                .transform(filtermap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> explore_concat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concat_mono() {
        var abcFlux = Flux.just("A", "B", "C");
        var dMono = Mono.just("D");
        return Flux.concat(abcFlux, dMono);
    }

    public Flux<String> explore_merge() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(120));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeWith_mono() {
        var aMono = Mono.just("A")
                .delayElement(Duration.ofMillis(100));
        var bMono = Mono.just("B")
                .delayElement(Duration.ofMillis(120));

        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> explore_mergeSequence() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(120));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> explore_zip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }

    public Flux<String> explore_zip_4() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }
}
