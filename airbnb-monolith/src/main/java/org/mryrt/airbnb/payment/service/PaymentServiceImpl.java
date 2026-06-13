package org.mryrt.airbnb.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.mryrt.airbnb.payment.ra.api.PaymentConnection;
import org.mryrt.airbnb.payment.ra.api.PaymentConnectionFactory;
import org.mryrt.airbnb.payment.ra.api.PaymentResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${airbnb.payment.jndi:java:/eis/PaymentGateway}")
    private String paymentJndi;

    @Value("${airbnb.payment.return-url:http://127.0.0.1:58144/payment-result}")
    private String returnUrl;

    @Override
    public PaymentResult createPayment(BigDecimal amount, String description) throws Exception {
        PaymentConnectionFactory factory = new JndiTemplate().lookup(paymentJndi, PaymentConnectionFactory.class);
        try (PaymentConnection connection = factory.getConnection()) {
            PaymentResult result = connection.createPayment(amount, description, returnUrl);
            log.debug("Payment created: id={}, url={}", result.getPaymentId(), result.getConfirmationUrl());
            return result;
        }
    }
}
