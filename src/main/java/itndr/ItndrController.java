package itndr;

import io.micronaut.http.annotation.*;

@Controller("/itndr")
public class ItndrController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}