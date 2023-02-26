package com.itndr;

import io.micronaut.core.annotation.Introspected;

import java.util.Objects;

@SuppressWarnings("unused")
@Introspected
public class Matching {
    private Double offer;
    private Double demand;

    private String currency;
    private String period;
    private String payType;

    public Matching() {
    }

    public Double getOffer() {
        return offer;
    }

    public void setOffer(Double offer) {
        this.offer = offer;
    }

    public boolean hasOffer() {
        return offer != null;
    }

    public Double getDemand() {
        return demand;
    }

    public void setDemand(Double demand) {
        this.demand = demand;
    }

    public boolean hasDemand() {
        return demand != null;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Matching matching = (Matching) o;
        return Objects.equals(offer, matching.offer)
                && Objects.equals(demand, matching.demand)
                && Objects.equals(currency, matching.currency)
                && Objects.equals(period, matching.period)
                && Objects.equals(payType, matching.payType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offer, demand, currency, period, payType);
    }
}
