package com.itndr;

import io.micronaut.core.annotation.Introspected;

@SuppressWarnings("unused")
@Introspected
public class ShowModel {
    private final String id;
    private final boolean offerFilled;
    private final boolean demandFilled;
    private final String errorMessage;

    public ShowModel(String id, boolean offerFilled, boolean demandFilled, String errorMessage) {
        this.id = id;
        this.offerFilled = offerFilled;
        this.demandFilled = demandFilled;
        this.errorMessage = errorMessage;
    }

    public String getId() {
        return id;
    }

    public boolean isOfferFilled() {
        return offerFilled;
    }

    public boolean isDemandFilled() {
        return demandFilled;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
