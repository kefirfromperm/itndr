package itndr;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.common.util.concurrent.MoreExecutors;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.views.ModelAndView;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller()
public class ItndrController {
    private final Firestore firestore;

    public ItndrController(Firestore firestore) {
        this.firestore = firestore;
    }

    @Get
    public HttpResponse<?> index() {
        return HttpResponse.redirect(URI.create("/" + UUID.randomUUID()))
                .header("Cache-Control", "no-cache");
    }

    @Get("/{id}")
    public Mono<ModelAndView<FillModel>> page(@PathVariable String id) {
        return document(id)
                .map(snapshot -> {
                    if (!snapshot.exists()) {
                        return new ModelAndView<>("form", new FillModel(id, false, false));
                    } else {
                        Double storedOffer = snapshot.getDouble("offer");
                        Double storedDemand = snapshot.getDouble("demand");

                        if (storedOffer == null || storedDemand == null) {
                            return new ModelAndView<>("form", new FillModel(id, storedOffer != null, storedDemand != null));
                        }

                        if (storedOffer >= storedDemand) {
                            return new ModelAndView<>("match", null);
                        } else {
                            return new ModelAndView<>("mismatch", null);
                        }
                    }
                });
    }

    @Post(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Mono<HttpResponse<ModelAndView<FillModel>>> save(
            @PathVariable String id,
            @Nullable Double offer,
            @Nullable Double demand
    ) {
        return document(id)
                .flatMap(snapshot -> {
                    final var ref = snapshot.getReference();
                    if (!snapshot.exists()) {
                        if (offer != null && demand == null || offer == null && demand != null) {
                            Map<String, Double> fields = new HashMap<>();
                            if (offer != null) {
                                fields.put("offer", offer);
                            }
                            if (demand != null) {
                                fields.put("demand", demand);
                            }
                            return toMono(ref.create(fields))
                                    .map(ignore -> HttpResponse.redirect(URI.create("/" + id)));
                        } else {
                            return Mono.just(HttpResponse.ok(new ModelAndView<>("form", new FillModel(id, false, false))));
                        }
                    } else {
                        Double storedOffer = snapshot.getDouble("offer");
                        Double storedDemand = snapshot.getDouble("demand");

                        if (offer != null && storedOffer == null && demand == null) {
                            return toMono(ref.update("offer", offer))
                                    .map(ignore -> HttpResponse.redirect(URI.create("/" + id)));
                        } else if (demand != null && storedDemand == null && offer == null) {
                            return toMono(ref.update("demand", demand))
                                    .map(ignore -> HttpResponse.redirect(URI.create("/" + id)));
                        } else {
                            return Mono.just(HttpResponse.ok(new ModelAndView<>("form", new FillModel(id, false, false))));
                        }
                    }
                });
    }

    private Mono<DocumentSnapshot> document(String id) {
        return toMono(firestore.collection("matching").document(id).get());
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