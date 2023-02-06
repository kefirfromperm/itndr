package com.itndr;

import io.micronaut.core.annotation.Introspected;

@SuppressWarnings("unused")
@Introspected
public class FillModel {
    private final String id;
    private final boolean offerFilled;
    private final boolean demandFilled;

    public FillModel(String id, boolean offerFilled, boolean demandFilled) {
        this.id = id;
        this.offerFilled = offerFilled;
        this.demandFilled = demandFilled;
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
}
