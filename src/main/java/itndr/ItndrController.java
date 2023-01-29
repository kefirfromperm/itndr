package itndr;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.views.ModelAndView;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@Controller()
public class ItndrController {

    @Get
    public HttpResponse<?> index() {
        return HttpResponse.redirect(URI.create("/" + UUID.randomUUID()))
                .header("Cache-Control", "no-cache");
    }

    @Get("/{id}")
    public ModelAndView<FillModel> page(@PathVariable String id) {
        return new ModelAndView<>("form", new FillModel(id, false, false));
    }

    @Post(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<FillModel> save(
            @PathVariable String id,
            @Nullable BigDecimal offer,
            @Nullable BigDecimal demand
    ) {
        return HttpResponse.redirect(URI.create("/" + id));
    }
}