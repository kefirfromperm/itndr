package com.itndr;

import io.micronaut.core.annotation.Introspected;

@SuppressWarnings("unused")
@Introspected
public class ShowModel {
    private final String id;
    private final boolean offerFilled;
    private final boolean demandFilled;
    private final String currency;
    private final String period;
    private final String payType;
    private final String errorMessage;

    public ShowModel(
            String id,
            boolean offerFilled,
            boolean demandFilled,
            String currency,
            String period,
            String payType,
            String errorMessage
    ) {
        this.id = id;
        this.offerFilled = offerFilled;
        this.demandFilled = demandFilled;
        this.currency = currency;
        this.period = period;
        this.payType = payType;
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

    public String getCurrency() {
        return currency;
    }

    public String getPeriod() {
        return period;
    }

    public String getPayType() {
        return payType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
