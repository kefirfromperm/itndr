package com.itndr;

import io.micronaut.core.annotation.Introspected;

@SuppressWarnings("unused")
@Introspected
public class Matching {
    private Double offer;
    private Double demand;

    public Matching() {
    }

    public Matching(Double offer, Double demand) {
        this.offer = offer;
        this.demand = demand;
    }

    public Double getOffer() {
        return offer;
    }

    public void setOffer(Double offer) {
        this.offer = offer;
    }

    public Double getDemand() {
        return demand;
    }

    public void setDemand(Double demand) {
        this.demand = demand;
    }

    public boolean hasOffer() {
        return offer != null;
    }

    public boolean hasDemand() {
        return demand != null;
    }
}
