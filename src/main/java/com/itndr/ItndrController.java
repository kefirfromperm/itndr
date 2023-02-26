package com.itndr;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.views.ModelAndView;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

@Controller()
public class ItndrController {
    private final MatchingRepository matchingRepository;

    public ItndrController(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

    @Get
    public HttpResponse<?> index() {
        return redirect(UUID.randomUUID().toString());
    }

    @Get("/{id}")
    public Mono<ModelAndView<ShowModel>> page(@PathVariable String id) {
        return matchingRepository.getById(id)
                .map(matching -> {
                    Double storedOffer = matching.getOffer();
                    Double storedDemand = matching.getDemand();

                    if (storedOffer == null || storedDemand == null) {
                        return showForm(id, matching);
                    }

                    if (storedOffer >= storedDemand) {
                        return new ModelAndView<ShowModel>("match", null);
                    } else {
                        return new ModelAndView<ShowModel>("mismatch", null);
                    }
                })
                .defaultIfEmpty(showEmptyForm(id));
    }

    @Post(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public Mono<HttpResponse<ModelAndView<ShowModel>>> save(
            @PathVariable String id,
            @Body Matching model
    ) {
        return matchingRepository.getById(id)
                .map(matching -> {
                    Double storedOffer = matching.getOffer();
                    Double storedDemand = matching.getDemand();

                    if (storedOffer != null && storedDemand != null) {
                        return Mono.just(ItndrController.<ModelAndView<ShowModel>>redirect(id));
                    }

                    if (model.hasOffer() && storedOffer == null && !model.hasDemand()) {
                        return matchingRepository.updateOffer(id, model.getOffer())
                                .map(ignore -> ItndrController.<ModelAndView<ShowModel>>redirect(id));
                    }

                    if (model.hasDemand() && storedDemand == null && !model.hasOffer()) {
                        return matchingRepository.updateDemand(id, model.getDemand())
                                .map(ignore -> ItndrController.<ModelAndView<ShowModel>>redirect(id));
                    }

                    return Mono.just(HttpResponse.ok(showForm(id, matching)));
                })
                .switchIfEmpty(
                        Mono.fromCallable(() -> {
                            if (model.hasOffer() && !model.hasDemand() || !model.hasOffer() && model.hasDemand()) {
                                return matchingRepository.save(id, model)
                                        .map(ignore -> ItndrController.redirect(id));
                            } else if (model.hasOffer() && model.hasDemand()) {
                                return Mono.just(HttpResponse.ok(showError(id, model,
                                        "Please fill in only one field salary expectation if you are a candidate or the salary upper limit if you are a recruiter."
                                )));
                            } else {
                                return Mono.just(HttpResponse.ok(showError(id, model,
                                        "Please fill in one field salary expectation if you are a candidate or the salary upper limit if you are a recruiter."
                                )));
                            }
                        })
                )
                .flatMap(Function.identity());
    }

    private static ModelAndView<ShowModel> showForm(String id, Matching matching) {
        return new ModelAndView<>(
                "form",
                new ShowModel(
                        id,
                        matching.getOffer() != null,
                        matching.getDemand() != null,
                        matching.getCurrency(),
                        matching.getPeriod(),
                        matching.getPayType(),
                        null
                )
        );
    }

    private static ModelAndView<ShowModel> showEmptyForm(String id) {
        return new ModelAndView<>(
                "form",
                new ShowModel(
                        id,
                        false,
                        false,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    private static ModelAndView<ShowModel> showError(String id, Matching matching, String errorMessage) {
        return new ModelAndView<>(
                "form",
                new ShowModel(
                        id,
                        false,
                        false,
                        matching.getCurrency(),
                        matching.getPeriod(),
                        matching.getPayType(),
                        errorMessage
                )
        );
    }

    private static <T> HttpResponse<T> redirect(String id) {
        return HttpResponse.<T>status(HttpStatus.FOUND)
                .headers(headers -> headers.location(URI.create("/" + id)));
    }
}