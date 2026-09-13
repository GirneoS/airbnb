package org.mryrt.airbnb.bitrix.ra.api;

import java.io.Serializable;

public class BitrixDealUpdate implements Serializable {

    private final String stageId;
    private final String comments;

    public BitrixDealUpdate(String stageId, String comments) {
        this.stageId = stageId;
        this.comments = comments;
    }

    public String getStageId() {
        return stageId;
    }

    public String getComments() {
        return comments;
    }
}
