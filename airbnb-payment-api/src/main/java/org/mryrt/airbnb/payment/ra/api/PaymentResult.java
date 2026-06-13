package org.mryrt.airbnb.payment.ra.api;

import java.io.Serializable;

public class PaymentResult implements Serializable {

    private final String paymentId;
    private final String confirmationUrl;

    public PaymentResult(String paymentId, String confirmationUrl) {
        this.paymentId = paymentId;
        this.confirmationUrl = confirmationUrl;
    }

    public String getPaymentId() { return paymentId; }
    public String getConfirmationUrl() { return confirmationUrl; }
}
