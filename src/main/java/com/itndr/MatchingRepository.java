package com.itndr;

import reactor.core.publisher.Mono;

public interface MatchingRepository {
    Mono<Matching> getById(String id);

    Mono<Boolean> save(String id, Matching matching);

    Mono<Boolean> updateOffer(String id, Double offer);

    Mono<Boolean> updateDemand(String id, Double demand);
}
