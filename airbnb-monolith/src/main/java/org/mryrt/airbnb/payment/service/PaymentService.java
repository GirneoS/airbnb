package org.mryrt.airbnb.payment.service;

import org.mryrt.airbnb.payment.ra.api.PaymentResult;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentResult createPayment(BigDecimal amount, String description) throws Exception;
}
