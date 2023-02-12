package com.itndr;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
@Requires(env = "test")
public class MapMatchingRepository implements MatchingRepository {
    private final ConcurrentMap<String, Matching> map = new ConcurrentHashMap<>();

    @Override
    public Mono<Matching> getById(String id) {
        return Mono.fromCallable(() -> map.get(id));
    }

    @Override
    public Mono<Boolean> save(String id, Matching matching) {
        return Mono.fromCallable(() -> {
            var mapValue = map.putIfAbsent(id, matching);
            if (mapValue != null) {
                throw new RuntimeException("Matching already exists");
            }
            return true;
        });
    }

    @Override
    public Mono<Boolean> updateOffer(String id, Double offer) {
        return Mono.fromCallable(() -> {
            map.computeIfPresent(id, (key, value) -> {
                value.setOffer(offer);
                return value;
            });
            return true;
        });
    }

    @Override
    public Mono<Boolean> updateDemand(String id, Double demand) {
        return Mono.fromCallable(() -> {
            map.computeIfPresent(id, (key, value) -> {
                value.setDemand(demand);
                return value;
            });
            return true;
        });
    }
}
