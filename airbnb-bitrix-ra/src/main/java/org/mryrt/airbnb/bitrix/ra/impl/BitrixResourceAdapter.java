package org.mryrt.airbnb.bitrix.ra.impl;

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
        displayName = "AirBnb Bitrix24 CRM Resource Adapter",
        reauthenticationSupport = false,
        transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction
)
public class BitrixResourceAdapter implements ResourceAdapter, Serializable {

    @Override
    public void start(BootstrapContext context) throws ResourceAdapterInternalException {}

    @Override
    public void stop() {}

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec)
            throws ResourceException {}

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {}

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) {
        return new XAResource[0];
    }

    @Override
    public boolean equals(Object other) { return other instanceof BitrixResourceAdapter; }

    @Override
    public int hashCode() { return BitrixResourceAdapter.class.hashCode(); }
}
