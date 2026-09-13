package org.mryrt.airbnb.bitrix.ra.api;

import java.io.Serializable;
import java.math.BigDecimal;

public class BitrixDealRequest implements Serializable {

    private final String title;
    private final String stageId;
    private final String comments;
    private final BigDecimal opportunity;
    private final String currencyId;

    public BitrixDealRequest(
            String title,
            String stageId,
            String comments,
            BigDecimal opportunity,
            String currencyId
    ) {
        this.title = title;
        this.stageId = stageId;
        this.comments = comments;
        this.opportunity = opportunity;
        this.currencyId = currencyId;
    }

    public String getTitle() {
        return title;
    }

    public String getStageId() {
        return stageId;
    }

    public String getComments() {
        return comments;
    }

    public BigDecimal getOpportunity() {
        return opportunity;
    }

    public String getCurrencyId() {
        return currencyId;
    }
}
