package com.learning.reactiveprogrammingusingreactor;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        //given

        //when
        var names = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(names)
                .expectNext("alex", "jack", "sam")
                .verifyComplete();

        StepVerifier.create(names)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFluxUpperCase() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_map();

        //then
        StepVerifier.create(names)
                .expectNext("ALEX", "JACK", "SAM")
                .verifyComplete();

    }

    @Test
    void namesFluxUpperCaseFilter() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_map_filter(3);

        //then
        StepVerifier.create(names)
                .expectNext("4-ALEX", "4-JACK")
                .verifyComplete();

    }

    @Test
    void namesFluxImmutability() {
        //when
        var names = fluxAndMonoGeneratorService.namesFluxImmutability();

        //then
        StepVerifier.create(names)
                .expectNext("alex", "jack", "sam")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_flatmap(2);

        //then
        StepVerifier.create(names)
                .expectNext(("alex" + "jack" + "sam").toUpperCase().split(""))
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_flatmap_async(3);

        //then
        StepVerifier.create(names)
//                .expectNext(("alex" + "jack").toUpperCase().split(""))
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_concatmap(3);

        //then
        StepVerifier.create(names)
                .expectNext(("alex" + "jack").toUpperCase().split(""))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap() {
        //when
        var names = fluxAndMonoGeneratorService.namesMono_flatMap(3);

        //then
        StepVerifier.create(names)
                .expectNext(List.of(("alex").toUpperCase().split("")))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        //when
        var names = fluxAndMonoGeneratorService.namesMono_flatMapMany(3);

        //then
        StepVerifier.create(names)
                .expectNext(("alex").toUpperCase().split(""))
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_transform(3);

        //then
        StepVerifier.create(names)
                .expectNext(("alex" + "jack").toUpperCase().split(""))
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_empty_value() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_transform(6);

        //then
        StepVerifier.create(names)
                .expectNext(("default"))
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        //when
        var names = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(6);

        //then
        StepVerifier.create(names)
                .expectNext(("default").toUpperCase().split(""))
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        //given
        var expectedResult = new String[]{"A", "B", "C", "D", "E", "F"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_concat();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void explore_concat_mono() {
        //given
        var expectedResult = new String[]{"A", "B", "C", "D"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_concat_mono();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void explore_concatWith() {
        //given
        var expectedResult = new String[]{"A", "B", "C", "D", "E", "F"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_concatWith();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        //given
        var expectedResult = new String[]{"A", "D", "B", "E", "C", "F"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_merge();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void explore_mergeWith_mono() {
        //given
        var expectedResult = new String[]{"B", "A"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_mergeWith_mono();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();

    }

    @Test
    void explore_mergeSequence() {
        //given
        var expectedResult = new String[]{"A", "B", "C", "D", "E", "F"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_mergeSequence();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void explore_zip() {

        //given
        var expectedResult = new String[]{"AD", "BE", "CF"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_zip();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }

    @Test
    void explore_zip_4() {


        //given
        var expectedResult = new String[]{"AD14", "BE25", "CF36"};

        //when
        var alpha = fluxAndMonoGeneratorService.explore_zip_4();

        //then
        StepVerifier.create(alpha)
                .expectNext(expectedResult)
                .verifyComplete();
    }
}