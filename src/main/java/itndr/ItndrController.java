package itndr;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.common.util.concurrent.MoreExecutors;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
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
        return redirect(UUID.randomUUID().toString());
    }

    @Get("/{id}")
    public Mono<ModelAndView<FillModel>> page(@PathVariable String id) {
        return document(id)
                .map(snapshot -> {
                    if (!snapshot.exists()) {
                        return showForm(id, false, false);
                    } else {
                        Double storedOffer = snapshot.getDouble("offer");
                        Double storedDemand = snapshot.getDouble("demand");

                        if (storedOffer == null || storedDemand == null) {
                            return showForm(id, storedOffer != null, storedDemand != null);
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
                            return toMono(ref.create(fields)).map(ignore -> redirect(id));
                        } else {
                            return Mono.just(HttpResponse.ok(showForm(id, false, false)));
                        }
                    } else {
                        Double storedOffer = snapshot.getDouble("offer");
                        Double storedDemand = snapshot.getDouble("demand");

                        if (storedOffer != null && storedDemand != null) {
                            return Mono.just(redirect(id));
                        }

                        if (offer != null && storedOffer == null && demand == null) {
                            return toMono(ref.update("offer", offer)).map(ignore -> redirect(id));
                        }

                        if (demand != null && storedDemand == null && offer == null) {
                            return toMono(ref.update("demand", demand)).map(ignore -> redirect(id));
                        }

                        return Mono.just(HttpResponse.ok(showForm(id, storedOffer != null, storedDemand != null)));
                    }
                });
    }

    private static ModelAndView<FillModel> showForm(String id, boolean offerFilled, boolean demandFilled) {
        return new ModelAndView<>("form", new FillModel(id, offerFilled, demandFilled));
    }

    private static <T> MutableHttpResponse<T> redirect(String id) {
        return HttpResponse.<T>status(HttpStatus.FOUND)
                .headers(headers -> headers.location(URI.create("/" + id)));
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