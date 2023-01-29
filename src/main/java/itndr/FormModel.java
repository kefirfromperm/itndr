package itndr;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class FormModel {
    private BigDecimal demand;
    private BigDecimal offer;

    public FormModel() {
    }

    public BigDecimal getDemand() {
        return demand;
    }

    public void setDemand(BigDecimal demand) {
        this.demand = demand;
    }

    public BigDecimal getOffer() {
        return offer;
    }

    public void setOffer(BigDecimal offer) {
        this.offer = offer;
    }
}
