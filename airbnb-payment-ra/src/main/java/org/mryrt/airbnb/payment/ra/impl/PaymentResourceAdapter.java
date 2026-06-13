package org.mryrt.airbnb.payment.ra.impl;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.Connector;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.ResourceAdapterInternalException;
import jakarta.resource.spi.TransactionSupport;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import java.io.Serializable;

@Connector(
        displayName = "AirBnb Payment Gateway RA",
        reauthenticationSupport = false,
        transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction
)
public class PaymentResourceAdapter implements ResourceAdapter, Serializable {

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {}

    @Override
    public void stop() {}

    @Override
    public void endpointActivation(MessageEndpointFactory mef, ActivationSpec spec) throws ResourceException {}

    @Override
    public void endpointDeactivation(MessageEndpointFactory mef, ActivationSpec spec) {}

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return new XAResource[0];
    }

    @Override
    public boolean equals(Object o) { return o instanceof PaymentResourceAdapter; }

    @Override
    public int hashCode() { return PaymentResourceAdapter.class.hashCode(); }
}
