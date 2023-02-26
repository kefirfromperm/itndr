package com.itndr;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.common.util.concurrent.MoreExecutors;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Requires(notEnv = "test")
public class FirestoreMatchingRepository implements MatchingRepository {
    private final Firestore firestore;

    public FirestoreMatchingRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Mono<Matching> getById(String id) {
        return toMono(reference(id).get())
                .mapNotNull(snapshot -> {
                            if (snapshot.exists()) {
                                final var matching = new Matching();
                                matching.setOffer(snapshot.getDouble("offer"));
                                matching.setDemand(snapshot.getDouble("demand"));
                                matching.setCurrency(snapshot.getString("currency"));
                                matching.setPeriod(snapshot.getString("period"));
                                matching.setPayType(snapshot.getString("payType"));
                                return matching;
                            } else {
                                return null;
                            }
                        }
                );
    }

    private DocumentReference reference(String id) {
        return firestore.collection("matching").document(id);
    }

    @Override
    public Mono<Boolean> save(String id, Matching matching) {
        Map<String, Object> fields = new HashMap<>();
        if (matching.hasOffer()) {
            fields.put("offer", matching.getOffer());
        }
        if (matching.hasDemand()) {
            fields.put("demand", matching.getDemand());
        }

        fields.put("currency", matching.getCurrency());
        fields.put("period", matching.getPeriod());
        fields.put("payType", matching.getPayType());

        return toMono(reference(id).create(fields))
                .map(ignore -> true);
    }

    @Override
    public Mono<Boolean> updateOffer(String id, Double offer) {
        return toMono(reference(id).update("offer", offer))
                .map(ignore -> true);
    }

    @Override
    public Mono<Boolean> updateDemand(String id, Double demand) {
        return toMono(reference(id).update("demand", demand))
                .map(ignore -> true);
    }

    public static <T> Mono<T> toMono(ApiFuture<T> apiFuture) {
        return Mono.create(sink -> ApiFutures.addCallback(
                apiFuture,
                new ApiFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable t) {
                        sink.error(t);
                    }

                    @Override
                    public void onSuccess(T result) {
                        sink.success(result);
                    }
                },
                MoreExecutors.directExecutor()
        ));
    }
}
