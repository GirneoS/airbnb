package org.mryrt.airbnb.payment.ra.api;

import java.math.BigDecimal;

public interface PaymentConnection extends AutoCloseable {

    PaymentResult createPayment(BigDecimal amount, String description, String returnUrl) throws Exception;

    @Override
    void close();
}
