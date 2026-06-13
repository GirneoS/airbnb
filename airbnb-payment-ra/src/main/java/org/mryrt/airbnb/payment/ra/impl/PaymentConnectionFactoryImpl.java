package org.mryrt.airbnb.payment.ra.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.Referenceable;
import jakarta.resource.spi.ConnectionManager;
import org.mryrt.airbnb.payment.ra.api.PaymentConnection;
import org.mryrt.airbnb.payment.ra.api.PaymentConnectionFactory;

import javax.naming.Reference;

public class PaymentConnectionFactoryImpl implements PaymentConnectionFactory, Referenceable {

    private final PaymentManagedConnectionFactory mcf;
    private final ConnectionManager cm;
    private Reference reference;

    public PaymentConnectionFactoryImpl(PaymentManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    @Override
    public PaymentConnection getConnection() throws ResourceException {
        return (PaymentConnection) cm.allocateConnection(mcf, null);
    }

    @Override
    public Reference getReference() { return reference; }

    @Override
    public void setReference(Reference reference) { this.reference = reference; }
}
