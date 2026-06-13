package org.mryrt.airbnb.payment.ra.impl;

import org.mryrt.airbnb.payment.ra.api.PaymentConnection;
import org.mryrt.airbnb.payment.ra.api.PaymentResult;
import ru.deelter.yookassa.YooKassa;
import ru.deelter.yookassa.data.impl.Amount;
import ru.deelter.yookassa.data.impl.Currency;
import ru.deelter.yookassa.data.impl.Payment;
import ru.deelter.yookassa.data.impl.requests.PaymentCreateData;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentConnectionImpl implements PaymentConnection {

    private PaymentManagedConnection mc;

    public PaymentConnectionImpl(PaymentManagedConnection mc) {
        this.mc = mc;
    }

    void setManagedConnection(PaymentManagedConnection mc) {
        this.mc = mc;
    }

    @Override
    public PaymentResult createPayment(BigDecimal amount, String description, String returnUrl) throws Exception {
        PaymentManagedConnectionFactory mcf = mc.getMcf();

        if (Boolean.TRUE.equals(mcf.getMockMode())) {
            String paymentId = UUID.randomUUID().toString();
            String confirmUrl = mcf.getMockBaseUrl() + "/api/v1/payments/" + paymentId + "/mock-confirm";
            return new PaymentResult(paymentId, confirmUrl);
        }

        YooKassa yooKassa = YooKassa.create(mcf.getShopId(), mcf.getSecretKey());
        Payment payment = yooKassa.createPayment(PaymentCreateData.builder()
                .amount(Amount.from(amount.longValue(), Currency.RUB))
                .description(description)
                .redirect(returnUrl)
                .capture(true)
                .build());
        return new PaymentResult(
                payment.getId().toString(),
                payment.getConfirmation().getUrl()
        );
    }

    @Override
    public void close() {
        if (mc != null) {
            mc.closeHandle(this);
            mc = null;
        }
    }
}
